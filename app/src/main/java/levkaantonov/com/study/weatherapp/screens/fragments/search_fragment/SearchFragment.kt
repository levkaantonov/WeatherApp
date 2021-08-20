package levkaantonov.com.study.weatherapp.screens.fragments.search_fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.*
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.gms.location.*
import com.google.android.material.bottomsheet.BottomSheetBehavior
import dagger.hilt.android.AndroidEntryPoint
import levkaantonov.com.study.weatherapp.R
import levkaantonov.com.study.weatherapp.databinding.FragmentSearchBinding
import levkaantonov.com.study.weatherapp.models.common.GpsEvent
import levkaantonov.com.study.weatherapp.models.common.LoadState
import levkaantonov.com.study.weatherapp.models.ui.Address
import levkaantonov.com.study.weatherapp.screens.common.PermissionRequestDialog
import levkaantonov.com.study.weatherapp.util.PERMISSION_COARSE_LOCATION
import levkaantonov.com.study.weatherapp.util.PERMISSION_FINE_LOCATION
import java.util.*

@AndroidEntryPoint
class SearchFragment : Fragment() {

    //region fields
    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SearchFragmentViewModel by viewModels()
    private val gpsViewModel: GpsViewModel by viewModels()

    private var searchView: SearchView? = null

    private var _searchAdapter: SearchAdapter? = null
    private val searchAdapter get() = checkNotNull(_searchAdapter)

    private var _favoritesAdapter: SearchAdapter? = null
    private val favoritesAdapter get() = checkNotNull(_favoritesAdapter)

    private var _bottomSheetBehaviorFavorites: BottomSheetBehavior<LinearLayout>? = null
    private val bottomSheetBehaviorFavorites get() = checkNotNull(_bottomSheetBehaviorFavorites)

    private var permissionRequest: PermissionRequestDialog? = null

    private var _searchMenuItem: MenuItem? = null
    private val menuItemSearch get() = checkNotNull(_searchMenuItem)

    private var _gpsMenuItem: MenuItem? = null
    private val menuItemGps get() = checkNotNull(_gpsMenuItem)
    //endregion

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        permissionRequest = buildPermissionRequest()
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
        setSearchAdapter()
        setFavoritesAdapter()
        observeStateOfLoading()
        setBottomSheetFavorites()
        observeGpsValues()
    }

    private fun observeGpsValues() {
        gpsViewModel.gpsData.observe(viewLifecycleOwner) { event ->
            when (event) {
                is GpsEvent.CurrentLocation -> {
                    setGpsResult(event.address)
                    setIconGpsIsTurnedOff()
                }
                is GpsEvent.State -> {
                    if (!event.isEnabled) {
                        showTurnOnGpsDialog()
                    }
                }
                is GpsEvent.Fault -> {
                    setIconGpsIsTurnedOff()
                    Toast.makeText(requireContext(), event.msg, Toast.LENGTH_SHORT).show()
                }
                else -> {
                    setIconGpsIsTurnedOff()
                }
            }
        }
    }

    private fun showTurnOnGpsDialog() {
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
    }

    private fun setBottomSheetFavorites() {
        binding.apply {
            favoritesBottomSheet.recyclerViewFavorites.adapter = favoritesAdapter
            recyclerView.adapter = searchAdapter
            _bottomSheetBehaviorFavorites = BottomSheetBehavior.from(favoritesBottomSheet.root)
        }
    }

    private fun setFavoritesAdapter() {
        _favoritesAdapter = SearchAdapter(
            object : LocationItemClickListener {
                override fun onClickItem(id: Int) =
                    actionOnItemClick(id)

                override fun onClickFavoritesIcon(id: Int) {
                    viewModel.clickOnFavIconInFavoritesResults(favoritesAdapter.currentList[id])
                }
            })
        observeFavoritesLocations(favoritesAdapter)
    }

    private fun setSearchAdapter() {
        _searchAdapter = SearchAdapter(
            object : LocationItemClickListener {
                override fun onClickItem(id: Int) =
                    actionOnItemClick(id)

                override fun onClickFavoritesIcon(id: Int) {
                    viewModel.clickOnFavIconInSearchResults(searchAdapter.currentList[id])
                }
            })
        observeSearchResults(searchAdapter)
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
    private fun observeStateOfLoading() {
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
        _searchMenuItem = menu.findItem(R.id.action_search)
        _gpsMenuItem = menu.findItem(R.id.action_get_gps_location)
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

    private fun buildPermissionRequest(): PermissionRequestDialog {
        return PermissionRequestDialog(
            requireActivity().activityResultRegistry,
            this,
            ::requestedPermissionsCallback
        )
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
            getCurrentLocation()
        } else {
            Toast.makeText(
                requireContext(),
                getString(R.string.permissions_not_granted),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun getCurrentLocation() {
        setIconGpsIsTurnedOn()
        gpsViewModel.getCurrentLocation()
    }

    private fun setIconGpsIsTurnedOn() {
        menuItemGps.setIcon(R.drawable.ic_gps_fixed)
    }

    private fun setIconGpsIsTurnedOff() {
        menuItemGps.setIcon(R.drawable.ic_gps)
    }

    private fun setGpsResult(addresses: Address) {
        searchView?.let {
            menuItemSearch.expandActionView()
            it.setQuery(addresses.locality?.lowercase(), false)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        searchView?.setOnQueryTextListener(null)
    }
}