package com.darekbx.launcher3.ui.applications

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.darekbx.launcher3.databinding.AdapterApplicationBinding

class ApplicationAdapter :
    RecyclerView.Adapter<ApplicationAdapter.ViewHolder>() {

    class ViewHolder(val viewBinding: AdapterApplicationBinding) :
        RecyclerView.ViewHolder(viewBinding.root)

    private var _applications = mutableListOf<Application>()

    var applications: MutableList<Application>
        @SuppressLint("NotifyDataSetChanged")
        set(value) {
            _applications = value
            notifyDataSetChanged()
        }
        get() = _applications

    var onItemClick: (Application?) -> Unit = { }
    var onItemLongClick: (Application?) -> Unit = { }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = AdapterApplicationBinding.inflate(inflater, parent, false)
        binding.root.setOnClickListener { onItemClick(binding.application) }
        binding.root.setOnLongClickListener { onItemLongClick(binding.application).run { true } }
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.viewBinding.application = _applications[position]
    }

    override fun getItemCount() = applications.size
}
