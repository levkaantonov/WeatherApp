package levkaantonov.com.study.weatherapp.screens.fragments.search_fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import levkaantonov.com.study.weatherapp.databinding.FragmentSearchBinding
import levkaantonov.com.study.weatherapp.util.Resource

@AndroidEntryPoint
class SearchFragment : Fragment() {
    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SearchFragmentViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSearchBinding.inflate(layoutInflater, container, false)

        val adapter = SearchAdapter(object : LocationItemClickListener {
            override fun click(position: Int) {
                Toast.makeText(requireContext(), position.toString(), Toast.LENGTH_SHORT).show()
            }
        })

        binding.recyclerView.adapter = adapter
        binding.toolbar.btnSearch.setOnClickListener {
            viewModel.searchLocations(binding.toolbar.etSearch.text.toString())
        }

        subscribeUi(adapter)

        return binding.root
    }

    private fun subscribeUi(adapter: SearchAdapter) {

        Log.d("TAG", "subscribeUi:")
        viewModel.locations.observe(viewLifecycleOwner) { locations ->
            when (locations) {
                is Resource.Error -> Toast.makeText(
                    requireContext(),
                    locations.msg,
                    Toast.LENGTH_SHORT
                ).show()
                is Resource.Loading -> Toast.makeText(
                    requireContext(),
                    "Loading",
                    Toast.LENGTH_SHORT
                ).show()
                is Resource.Success -> adapter.submitList(locations?.data)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}