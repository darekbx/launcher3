package com.darekbx.launcher3.ui.airly

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.databinding.DataBindingUtil
import com.darekbx.launcher3.R
import com.darekbx.launcher3.airly.domain.DistanceMeasurements
import com.darekbx.launcher3.databinding.AdapterMeasurmentBinding

class MeasurementsAdapter(context: Context) :
    ArrayAdapter<DistanceMeasurements>(context, R.layout.adapter_measurment) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val binding = when (convertView) {
            null -> DataBindingUtil.inflate(inflater, R.layout.adapter_measurment, parent, false)
            else -> DataBindingUtil.getBinding<AdapterMeasurmentBinding>(convertView)
        } as AdapterMeasurmentBinding

        return binding.apply {
            distanceMeasurements = getItem(position)
            executePendingBindings()
        }.root
    }

    private val inflater by lazy { LayoutInflater.from(context) }
}
