package com.darekbx.launcher3.ui.airly

import android.database.DataSetObserver
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.darekbx.launcher3.R
import com.darekbx.launcher3.databinding.FragmentAirlyBinding
import com.darekbx.launcher3.ui.RefreshableFragment
import com.darekbx.launcher3.viewmodel.AirlyViewModel
import org.koin.android.viewmodel.ext.android.viewModel

class AirlyFragment : Fragment(), RefreshableFragment {

    private val airlyViewModel: AirlyViewModel by viewModel()

    private var _binding: FragmentAirlyBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAirlyBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.measurementLayout.adapter = measurementsAdapter

        airlyViewModel.installations.observe(viewLifecycleOwner) { installations ->
            airlyViewModel.loadMeasurements((installations.map { it.id }))

            if (installations.isEmpty()) {
                binding.progress.visibility = View.GONE
            }
        }

        airlyViewModel.distanceMeasurements.observe(viewLifecycleOwner) { distanceMeasurements ->
            measurementsAdapter.add(distanceMeasurements)
            val limitsString = distanceMeasurements.measurements.rateLimits?.run {
                getString(
                    R.string.airly_limits_format,
                    dayLimit, dayRemaining, minuteLimit, minuteRemaining
                )
            }
            if (limitsString != null) {
                binding.limits.setText(limitsString)

                /**
                 * Progress should be hidden after the last one of loaded measurement
                 */
                binding.progress.visibility = View.GONE
            }
        }

        airlyViewModel.error.observe(viewLifecycleOwner) {
            binding.progress.visibility = View.GONE
        }

        displayMeasurements()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun refresh() {
        displayMeasurements()
    }

    private fun displayMeasurements() {
        binding.progress.visibility = View.VISIBLE
        measurementsAdapter.clear()
        airlyViewModel.loadInstallations()
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
