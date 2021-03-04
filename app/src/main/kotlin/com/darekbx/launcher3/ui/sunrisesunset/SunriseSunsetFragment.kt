package com.darekbx.launcher3.ui.sunrisesunset

import android.Manifest
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.darekbx.launcher3.R
import com.darekbx.launcher3.databinding.FragmentSunriseSunsetBinding
import com.darekbx.launcher3.ui.PermissionRequester
import com.darekbx.launcher3.viewmodel.SunriseSunsetViewModel
import org.koin.android.viewmodel.ext.android.viewModel

class SunriseSunsetFragment : Fragment(R.layout.fragment_sunrise_sunset) {

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
        permissionRequester.runWithPermissions {
            sunriseSunsetViewModel.sunriseSunset.observe(viewLifecycleOwner, {
                displaySunriseSunset(it.sunrise, it.sunset)
            })
        }
    }

    private fun displaySunriseSunset(sunrise: String, sunset: String) {
        binding.sunriseText.setText(getString(R.string.sunrise_format, sunrise))
        binding.sunsetText.setText(getString(R.string.sunset_format, sunset))
    }

    private val permissionRequester =
        PermissionRequester(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION,
            onDenied = { showRationale() },
            onShowRationale = { showDeniedPermissionInformation() }
        )

    private fun showRationale() {
        Toast.makeText(requireContext(), R.string.location_permission_rationale, Toast.LENGTH_SHORT)
            .show()
    }

    private fun showDeniedPermissionInformation() {
        Toast.makeText(requireContext(), R.string.location_permission_denied, Toast.LENGTH_SHORT)
            .show()
    }
}
