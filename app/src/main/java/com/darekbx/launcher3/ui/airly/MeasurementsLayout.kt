package com.darekbx.launcher3.ui.airly

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.AdapterView
import androidx.constraintlayout.solver.widgets.analyzer.BasicMeasure
import com.darekbx.launcher3.airly.domain.Measurements

class MeasurementsLayout(context: Context, attrs: AttributeSet?):
    AdapterView<MeasurementsAdapter>(context, attrs) {

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        if (!changed || childCount == 0) {
            return
        }

        for (measurementIndex in 0 until adapter.count) {
            val measurements = adapter.getItem(measurementIndex)
            addMeasurmentsView(measurements)
        }

        invalidate()
    }

    override fun getAdapter() = measurementsAdapter

    override fun setAdapter(adapter: MeasurementsAdapter) {
        measurementsAdapter = adapter
    }

    override fun getSelectedView(): View? = null

    override fun setSelection(position: Int) {}

    private fun createMeasurmentsView(measurementIndex: Int): View {
        val measurements = adapter.getItem(measurementIndex)
        val measurementsView = adapter.getView(measurementIndex, null, this)

        with (measurementsView) {
            measure(-1, -1)
            layout(


            )
        }

        addViewInLayout(measurementsView, measurementIndex, null, true)
    }

    private lateinit var measurementsAdapter: MeasurementsAdapter
}
