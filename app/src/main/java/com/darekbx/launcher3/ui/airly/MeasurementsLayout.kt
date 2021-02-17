package com.darekbx.launcher3.ui.airly

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.AdapterView

class MeasurementsLayout(context: Context, attrs: AttributeSet?) :
    AdapterView<MeasurementsAdapter>(context, attrs) {

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        if (changed || childCount == 0) {
            for (measurementIndex in 0 until adapter.count) {
                addMeasurmentsView(measurementIndex)
                invalidate()
            }
        }
    }

    override fun getAdapter() = measurementsAdapter

    override fun setAdapter(adapter: MeasurementsAdapter) {
        measurementsAdapter = adapter
    }

    override fun getSelectedView(): View? = null

    override fun setSelection(position: Int) { /* Do nothing */ }

    private fun addMeasurmentsView(measurementIndex: Int): View {
        val measurementsView = adapter.getView(measurementIndex, null, this)

        with(measurementsView) {
            measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED)
            val x = 0
            val y = measuredHeight * measurementIndex
            layout(x, y, x + measuredWidth, y + measuredHeight)
            addViewInLayout(this, measurementIndex, null, true)
        }

        return measurementsView
    }

    private lateinit var measurementsAdapter: MeasurementsAdapter
}
