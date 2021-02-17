package com.darekbx.launcher3.ui.airly

import android.Manifest
import android.database.DataSetObserver
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.darekbx.launcher3.R
import com.darekbx.launcher3.databinding.FragmentAirlyBinding
import com.darekbx.launcher3.ui.PermissionRequester
import com.darekbx.launcher3.viewmodel.AirlyViewModel
import org.koin.android.viewmodel.ext.android.viewModel

class AirlyFragment : Fragment(R.layout.fragment_airly) {

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

        binding.measurementLayout.adapter = measurementsAdapter

        permissionRequester.runWithPermissions {
            displayMeasurements()
        }
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
        airlyViewModel.installations.observe(viewLifecycleOwner, { installations ->
            val ids = installations.map { it.id }
            airlyViewModel.measurements(ids).observe(
                viewLifecycleOwner, { measurments ->
                    measurementsAdapter.add(measurments)
                })
        })

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

    private val measurementsAdapter by lazy {
        MeasurementsAdapter(requireContext()).apply {
            setNotifyOnChange(true)
            registerDataSetObserver(adapterObserver)
        }
    }

    private val adapterObserver = object : DataSetObserver() {
        override fun onChanged() {
            binding.measurementLayout.removeAllViewsInLayout()
            binding.measurementLayout.requestLayout()
        }
    }
}
