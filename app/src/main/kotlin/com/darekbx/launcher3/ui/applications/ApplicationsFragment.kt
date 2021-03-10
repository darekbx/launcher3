package com.darekbx.launcher3.ui.applications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.darekbx.launcher3.databinding.FragmentApplicationsBinding
import com.darekbx.launcher3.viewmodel.ApplicationsViewModel
import org.koin.android.viewmodel.ext.android.viewModel

class ApplicationsFragment : Fragment() {

    companion object {
        private const val COLUMNS_COUNT = 4
    }

    private val applicationsReceiver =  object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            when (intent.action) {
                Intent.ACTION_PACKAGE_ADDED, Intent.ACTION_PACKAGE_REMOVED ->
                    applicationsViewModel.listApplications()
            }
        }
    }

    private val applicationsViewModel: ApplicationsViewModel by viewModel()

    private var _binding: FragmentApplicationsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentApplicationsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        requireContext().unregisterReceiver(applicationsReceiver)
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        registerApplicationsBroadcast()

        binding.applicationsList.adapter = applicationAdapter
        binding.applicationsList.layoutManager = GridLayoutManager(requireContext(), COLUMNS_COUNT)

        val itemTouchHelper = ItemTouchHelper(itemTouchCallback)
        itemTouchHelper.attachToRecyclerView(binding.applicationsList)

        applicationsViewModel.listApplications().observe(viewLifecycleOwner, { applications ->
            applicationAdapter.applications = applications.toMutableList()
        })
    }

    private fun startActivityByPackageName(application: Application?) {
        if (application == null) return
        val intent = requireContext()
            .packageManager
            .getLaunchIntentForPackage(application.packageName)
        startActivity(intent)
    }

    private fun applicationSettings(application: Application?) {
        if (application == null) return
        startActivity(
            Intent(
                Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                Uri.fromParts("package", application.packageName, null)
            )
        )
    }

    private fun registerApplicationsBroadcast() {
        requireContext().registerReceiver(applicationsReceiver, IntentFilter().apply {
            addAction(Intent.ACTION_PACKAGE_ADDED)
            addAction(Intent.ACTION_PACKAGE_REMOVED)
            addDataScheme("package")
        })
    }

    private val itemTouchFlags by lazy {
        ItemTouchHelper.UP or ItemTouchHelper.DOWN or ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
    }

    private val itemTouchCallback = object: ItemTouchHelper.SimpleCallback(itemTouchFlags, 0) {

        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ): Boolean {
            val fromPosition = viewHolder.adapterPosition
            val toPosition = target.adapterPosition
            val item = applicationAdapter.applications.removeAt(fromPosition)
            applicationAdapter.applications.add(toPosition, item)
            recyclerView.adapter?.notifyItemMoved(fromPosition, toPosition)
            applicationsViewModel.savePosition(item, toPosition)
            return false
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) { }
    }

    private val applicationAdapter by lazy {
        ApplicationAdapter().apply {
            onItemClick = { startActivityByPackageName(it) }
            onItemLongClick = { applicationSettings(it) }
        }
    }
}
