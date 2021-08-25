package levkaantonov.com.study.weatherapp.screens.fragments.details_fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import dagger.hilt.android.AndroidEntryPoint
import levkaantonov.com.study.weatherapp.databinding.FragmentDetailsBinding
import levkaantonov.com.study.weatherapp.models.common.Resource

@AndroidEntryPoint
class DetailsFragment : Fragment() {
    private var _binding: FragmentDetailsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: DetailsFragmentViewModel by viewModels()
    private val arguments: DetailsFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailsBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initialize()
    }

    /*
        Инициализация.
     */
    private fun initialize() {
        val adapter = DetailsAdapter()
        binding.recyclerView.adapter = adapter
        observeWeather(adapter)
        viewModel.getWeather(arguments.woeid)
    }

    /*
        Подписка на результат поиска погоды.
     */
    private fun observeWeather(adapter: DetailsAdapter) {
        viewModel.weather.observe(viewLifecycleOwner) { weather ->
            when (weather) {
                is Resource.Loading -> binding.progress.isVisible = true
                is Resource.Success -> {
                    binding.progress.isVisible = false
                    val consolidatedWeather = weather.data?.consolidated_weather ?: return@observe
                    adapter.submitList(consolidatedWeather)
                }
                is Resource.Error -> {
                    binding.progress.isVisible = false
                    Toast.makeText(requireContext(), weather.msg, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}