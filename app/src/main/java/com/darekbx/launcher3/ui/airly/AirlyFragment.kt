package com.darekbx.launcher3.ui.airly

import android.Manifest
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.observe
import com.darekbx.launcher3.databinding.FragmentAirlyBinding
import com.darekbx.launcher3.ui.PermissionRequester
import com.darekbx.launcher3.viewmodel.AirlyViewModel
import org.koin.android.viewmodel.ext.android.viewModel

class AirlyFragment: Fragment() {

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

        _binding?.refresh?.setOnClickListener {
            permissionRequester.runWithPermissions {
                displayMeasurements()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun displayMeasurements() =
            airlyViewModel.installations.observe(viewLifecycleOwner, { installations ->
                val ids = installations.map { it.id }
                airlyViewModel.measurements(*ids.toIntArray()).observe(viewLifecycleOwner, { measurments ->


                })
            })

    private val permissionRequester =
            PermissionRequester(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    onDenied = { Toast.makeText(requireContext(), "Denied", Toast.LENGTH_SHORT).show() },
                    onShowRationale = { Toast.makeText(requireContext(), "Show rationale", Toast.LENGTH_SHORT).show() }
            )
}
