package levkaantonov.com.study.weatherapp.screens.fragments.search_fragment

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import levkaantonov.com.study.weatherapp.R
import levkaantonov.com.study.weatherapp.databinding.FragmentSearchBinding
import levkaantonov.com.study.weatherapp.models.common.Resource
import levkaantonov.com.study.weatherapp.util.TextWatcher

@AndroidEntryPoint
class SearchFragment : Fragment() {
    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SearchFragmentViewModel by viewModels()
    private var searchView: SearchView? = null
    private var adapter: SearchAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = SearchAdapter(object : LocationItemClickListener {
            override fun click(woeId: Int) = actionOnItemClick(woeId)
        })

        binding.recyclerView.adapter = adapter
        subscribeUi(adapter!!)
        setHasOptionsMenu(true)
    }

    fun actionOnItemClick(woeId: Int) {
        val action = SearchFragmentDirections.actionSearchFragmentToDetailsFragment(woeId)
        findNavController().navigate(action)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_fragment_search, menu)

        val searchItem = menu.findItem(R.id.action_search)
        searchView = searchItem.actionView as SearchView
        searchView?.isSubmitButtonEnabled = true

        searchView?.setOnQueryTextListener(
            TextWatcher(
                ::searchViewOnTextSubmit, ::searchViewOnQueryChanged
            )
        )
    }

    private fun searchViewOnTextSubmit(query: String): Boolean {
        viewModel.searchLocations(query)
        return true
    }


    private fun searchViewOnQueryChanged(query: String): Boolean {
        if (query.isBlank()) {
            adapter?.submitList(null)
            return false
        }
        return true
    }

    private fun subscribeUi(adapter: SearchAdapter) {
        viewModel.locations.observe(viewLifecycleOwner) { locations ->
            when (locations) {
                is Resource.Error -> {
                    Toast.makeText(requireContext(), locations.msg, Toast.LENGTH_SHORT).show()
                    binding.progress.isVisible = false
                }
                is Resource.Success -> {
                    adapter.submitList(locations.data)
                    binding.progress.isVisible = false
                }
                is Resource.Loading -> {
                    binding.progress.isVisible = true
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}