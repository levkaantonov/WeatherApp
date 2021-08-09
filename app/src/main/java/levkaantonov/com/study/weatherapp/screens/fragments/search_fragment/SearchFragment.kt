package levkaantonov.com.study.weatherapp.screens.fragments.search_fragment

import android.os.Bundle
import android.view.*
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetBehavior
import dagger.hilt.android.AndroidEntryPoint
import levkaantonov.com.study.weatherapp.R
import levkaantonov.com.study.weatherapp.databinding.FragmentSearchBinding
import levkaantonov.com.study.weatherapp.models.common.LoadState

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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(layoutInflater, container, false)
        return binding.root
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
        val searchItem = menu.findItem(R.id.action_search)
        prepareSearchView(searchItem)
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
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        searchView?.setOnQueryTextListener(null)
    }
}