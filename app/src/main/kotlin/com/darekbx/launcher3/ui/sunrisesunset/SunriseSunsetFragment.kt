package com.darekbx.launcher3.ui.sunrisesunset

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.darekbx.launcher3.R
import com.darekbx.launcher3.databinding.FragmentSunriseSunsetBinding
import com.darekbx.launcher3.viewmodel.SunriseSunsetViewModel
import org.koin.android.viewmodel.ext.android.viewModel

class SunriseSunsetFragment : Fragment() {

    private val sunriseSunsetViewModel: SunriseSunsetViewModel by viewModel()

    private var _binding: FragmentSunriseSunsetBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSunriseSunsetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sunriseSunsetViewModel.sunriseSunset().observe(viewLifecycleOwner, {
            displaySunriseSunset(it.sunrise, it.sunset)
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun displaySunriseSunset(sunrise: String, sunset: String) {
        binding.sunriseText.setText(getString(R.string.sunrise_format, sunrise))
        binding.sunsetText.setText(getString(R.string.sunset_format, sunset))
    }
}
