package com.darekbx.launcher3.ui.weather

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager
import com.darekbx.launcher3.databinding.FragmentWeatherBinding
import com.darekbx.launcher3.ui.RefreshableFragment
import com.darekbx.launcher3.viewmodel.WeatherViewModel
import org.koin.android.viewmodel.ext.android.viewModel

class WeatherFragment : Fragment(), RefreshableFragment {

    private val weatherViewModel: WeatherViewModel by viewModel()

    private var _binding: FragmentWeatherBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentWeatherBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        weatherViewModel.rainPrediction.observe(viewLifecycleOwner) { rainPredictionImage ->
            binding.rainImage.setImageBitmap(rainPredictionImage)
            binding.progress.visibility = View.GONE
        }
        weatherViewModel.error.observe(viewLifecycleOwner) {
            binding.progress.visibility = View.GONE
        }
        binding.progress.visibility = View.VISIBLE
        weatherViewModel.rainPrediction(useAntistorm)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun refresh() {
        binding.progress.visibility = View.VISIBLE
        weatherViewModel.rainPrediction(useAntistorm)
    }

    private val useAntistorm: Boolean
        get() = settingsPreferences.getBoolean("use_antistorm", false)

    private val settingsPreferences by lazy {
        PreferenceManager.getDefaultSharedPreferences(requireContext())
    }
}
