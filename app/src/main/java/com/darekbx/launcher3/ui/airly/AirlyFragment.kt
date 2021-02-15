package com.darekbx.launcher3.ui.airly

import android.Manifest
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.observe
import com.darekbx.launcher3.R
import com.darekbx.launcher3.databinding.FragmentAirlyBinding
import com.darekbx.launcher3.ui.PermissionRequester
import com.darekbx.launcher3.viewmodel.AirlyViewModel
import org.koin.android.viewmodel.ext.android.viewModel

class AirlyFragment: Fragment(R.layout.fragment_airly) {

    private val airlyViewModel: AirlyViewModel by viewModel()

    private var _binding: FragmentAirlyBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAirlyBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        refreshOnLongClick()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun refreshOnLongClick() {
        binding.root.setOnLongClickListener {
            permissionRequester.runWithPermissions {
                displayMeasurements()
            }
            false
        }
    }

    private fun displayMeasurements() =
            airlyViewModel.installations.observe(viewLifecycleOwner, Observer { installations ->
                val ids = installations.map { it.id }
                airlyViewModel.measurements(*ids.toIntArray()).observe(viewLifecycleOwner, Observer {  measurments ->


                })
            })

    private val permissionRequester =
            PermissionRequester(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    onDenied = { shoRationale() },
                    onShowRationale = { showDeniedPermissionInformation() }
            )

    private fun shoRationale() {
        Toast.makeText(requireContext(), R.string.location_permission_rationale, Toast.LENGTH_SHORT).show()
    }

    private fun showDeniedPermissionInformation() {
        Toast.makeText(requireContext(), R.string.location_permission_denied, Toast.LENGTH_SHORT).show()
    }
}
