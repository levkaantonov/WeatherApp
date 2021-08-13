package levkaantonov.com.study.weatherapp.screens.fragments.search_fragment

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.view.*
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.ActivityResultRegistry
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.fragment.findNavController
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.material.bottomsheet.BottomSheetBehavior
import dagger.hilt.android.AndroidEntryPoint
import levkaantonov.com.study.weatherapp.R
import levkaantonov.com.study.weatherapp.databinding.FragmentSearchBinding
import levkaantonov.com.study.weatherapp.models.common.LoadState
import levkaantonov.com.study.weatherapp.util.PERMISSION_COARSE_LOCATION
import levkaantonov.com.study.weatherapp.util.PERMISSION_FINE_LOCATION
import levkaantonov.com.study.weatherapp.util.PERMISSION_REQUEST
import java.util.*

@AndroidEntryPoint
class SearchFragment : Fragment() {
    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SearchFragmentViewModel by viewModels()

    private var searchView: SearchView? = null
    private var _searchAdapter: SearchAdapter? = null
    private val searchAdapter get() = checkNotNull(_searchAdapter)
    private var _favoritesAdapter: SearchAdapter? = null
    private val favoritesAdapter get() = checkNotNull(_favoritesAdapter)
    private var _bottomSheetBehaviorFavorites: BottomSheetBehavior<LinearLayout>? = null
    private val bottomSheetBehaviorFavorites get() = checkNotNull(_bottomSheetBehaviorFavorites)
    private var permissionRequest: PermissionRequestDialog? = null
    private var _searchItem: MenuItem? = null
    private val menuItemSearch get() = checkNotNull(_searchItem)

    private val fusedLocationProviderClient by lazy {
        LocationServices.getFusedLocationProviderClient(requireContext())
    }
    private val locationCallback by lazy {
        object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                locationResult.locations.let {
                    val location = it.firstOrNull()
                    location ?: return@let
                    val geocoder = Geocoder(requireContext(), Locale.ENGLISH)
                    val addresses =
                        geocoder.getFromLocation(location.latitude, location.longitude, 1)
                    setLocationResult(addresses)
                }
                stopLocationUpdates()
            }
        }
    }

    private val locationRequest by lazy {
        LocationRequest.create().apply {
            interval = 10000
            fastestInterval = 5000
            priority = LocationRequest.PRIORITY_LOW_POWER
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        permissionRequest = PermissionRequestDialog(
            requireActivity().activityResultRegistry,
            this,
            ::requestedPermissionsCallback
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initialize()
        setHasOptionsMenu(true)
    }

    /*
        Инициализация.
     */
    private fun initialize() {
        _searchAdapter = SearchAdapter(
            object : LocationItemClickListener {
                override fun onClickItem(id: Int) =
                    actionOnItemClick(id)

                override fun onClickFavoritesIcon(id: Int) {
                    viewModel.clickOnFavIconInSearchResults(searchAdapter.currentList[id])
                }
            })
        observeSearchResults(searchAdapter)

        _favoritesAdapter = SearchAdapter(
            object : LocationItemClickListener {
                override fun onClickItem(id: Int) =
                    actionOnItemClick(id)

                override fun onClickFavoritesIcon(id: Int) {
                    viewModel.clickOnFavIconInFavoritesResults(favoritesAdapter.currentList[id])
                }
            })
        observeFavoritesLocations(favoritesAdapter)

        observeStateOfLoading(viewModel)

        binding.apply {
            favoritesBottomSheet.recyclerViewFavorites.adapter = favoritesAdapter
            recyclerView.adapter = searchAdapter
            _bottomSheetBehaviorFavorites = BottomSheetBehavior.from(favoritesBottomSheet.root)
        }
    }

    /*
        Подписка на результаты поиска.
     */
    private fun observeSearchResults(adapter: SearchAdapter) {
        viewModel.locations.observe(viewLifecycleOwner) { locations ->
            adapter.submitList(locations)
        }
    }

    /*
       Подписка на избранные локации.
    */
    private fun observeFavoritesLocations(adapter: SearchAdapter) {
        viewModel.favoritesLocation.observe(viewLifecycleOwner) { favorites ->
            adapter.submitList(favorites)
        }
    }

    /*
        Подписка на состояние поиска.
     */
    private fun observeStateOfLoading(viewModel: SearchFragmentViewModel) {
        viewModel.loadState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is LoadState.Loading -> binding.progress.isVisible = true
                is LoadState.Success -> binding.progress.isVisible = false
                is LoadState.Error -> {
                    binding.progress.isVisible = false
                    Toast.makeText(requireContext(), state.msg, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    /*
        Клик по пункту списка, навигация на фрагмент с расширенной информацией.
     */
    private fun actionOnItemClick(woeId: Int) {
        val action = SearchFragmentDirections.actionSearchFragmentToDetailsFragment(woeId)
        findNavController().navigate(action)
    }

    private fun showFavorites() {
        bottomSheetBehaviorFavorites.apply {
            state =
                if (state == BottomSheetBehavior.STATE_EXPANDED)
                    BottomSheetBehavior.STATE_COLLAPSED
                else
                    BottomSheetBehavior.STATE_EXPANDED
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_fragment_search, menu)
        _searchItem = menu.findItem(R.id.action_search)
        prepareSearchView(menuItemSearch)
    }

    private fun prepareSearchView(searchItem: MenuItem) {
        searchView = searchItem.actionView as SearchView
        searchView?.let {
            val pendingQuery = viewModel.savedQuery.value
            if (pendingQuery != null && pendingQuery.isNotEmpty()) {
                searchItem.expandActionView()
                it.setQuery(pendingQuery, false)
            }

            it.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    if (query != null) {
                        binding.recyclerView.scrollToPosition(0)
                        viewModel.searchLocations(query)
                        it.clearFocus()
                    }
                    return true
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    viewModel.setSavedQuery(newText.orEmpty())
                    return true
                }
            })
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_favorites -> {
                showFavorites()
                true
            }
            R.id.action_get_gps_location -> {
                getLocation()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun getLocation() {
        permissionRequest?.requestPermissions(
            arrayOf(PERMISSION_FINE_LOCATION, PERMISSION_COARSE_LOCATION)
        )
    }

    private fun checkGpsIsEnabled() {
        var gpsIsEnabled = false
        var networkIsEnabled = false

        try {
            val builder = LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest)

            val settingsClient = LocationServices.getSettingsClient(requireContext())

            val task = settingsClient.checkLocationSettings(builder.build())
            task.apply {
                addOnSuccessListener { response ->
                    response?.let {
                        it.locationSettingsStates?.let { state ->
                            gpsIsEnabled = state.isGpsPresent
                            networkIsEnabled = state.isNetworkLocationPresent
                        }
                    }

                    if (response == null || (!gpsIsEnabled && !networkIsEnabled)) {
                        AlertDialog
                            .Builder(requireContext())
                            .setTitle(getString(R.string.enable_gps_service))
                            .setCancelable(false)
                            .setPositiveButton(getString(R.string.Ok)) { _, _ ->
                                val startSettingActivityIntent =
                                    Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                                startActivity(startSettingActivityIntent)
                            }.setNegativeButton(getString(R.string.Cancel), null)
                            .show()
                    } else {
                        startLocationUpdates()
                    }
                }

                addOnFailureListener { exception ->
                    if (exception is ResolvableApiException) {
                        try {
                            with(exception) {
                                startResolutionForResult(
                                    requireActivity(),
                                    PERMISSION_REQUEST
                                )
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun startLocationUpdates() {
        try {
            fusedLocationProviderClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            )
        } catch (e: SecurityException) {
            e.printStackTrace()
        }
    }

    private fun stopLocationUpdates() {
        fusedLocationProviderClient.removeLocationUpdates(locationCallback)
    }

    private fun setLocationResult(addresses: List<Address>) {
        searchView?.let {
            menuItemSearch.expandActionView()
            it.setQuery(addresses.firstOrNull()?.locality?.lowercase(), false)
        }
    }

    private fun requestedPermissionsCallback(permissions: MutableMap<String, Boolean>) {
        var countOfGrantedPermissions = 0
        permissions.entries.forEach { permission ->
            if (!permission.value) {
                return@forEach
            }
            countOfGrantedPermissions++
        }

        if (countOfGrantedPermissions == permissions.size) {
            checkGpsIsEnabled()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        searchView?.setOnQueryTextListener(null)
    }
}

class PermissionRequestDialog(
    activityResultRegistry: ActivityResultRegistry,
    lifecycleOwner: LifecycleOwner,
    callback: (permissions: MutableMap<String, Boolean>) -> Unit
) {
    private val permissionRequest = activityResultRegistry.register(
        REG_KEY,
        lifecycleOwner,
        ActivityResultContracts.RequestMultiplePermissions(), callback
    )

    fun requestPermissions(
        requestedPermissions: Array<String>
    ) {
        permissionRequest.launch(requestedPermissions)
    }

    private companion object {
        private const val REG_KEY = "PermissionRequestDialog"
    }
}