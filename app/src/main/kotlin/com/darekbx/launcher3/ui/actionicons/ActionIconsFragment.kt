package com.darekbx.launcher3.ui.actionicons

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.darekbx.launcher3.R
import com.darekbx.launcher3.databinding.FragmentActionIconsBinding
import com.darekbx.launcher3.dotpad.DotsReceiver

class ActionIconsFragment : Fragment() {

    private var _binding: FragmentActionIconsBinding? = null
    private val binding get() = _binding!!

    private val dotsReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.hasExtra(DotsReceiver.DOTS_COUNT_KEY)) {
                displayDotsCount(intent.getIntExtra(DotsReceiver.DOTS_COUNT_KEY, -1))
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        requireActivity().registerReceiver(
            dotsReceiver,
            IntentFilter(DotsReceiver.DOTS_FORWARD_ACTION)
        )
        _binding = FragmentActionIconsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding) {
            cameraButton.setOnClickListener { openCamera() }
            phoneButton.setOnClickListener { openPhone() }
            messageButton.setOnClickListener { openMessages() }
            dotPadButton.setOnClickListener { openDotpad() }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        requireActivity().unregisterReceiver(dotsReceiver)
        _binding = null
    }

    private fun openCamera() {
        startActvivityByPackageName("com.google.android.GoogleCamera")
    }

    private fun openMessages() {
        startActvivityByPackageName("com.google.android.apps.messaging")
    }

    private fun openPhone() {
        startActivity(Intent(Intent.ACTION_DIAL))
    }

    private fun openDotpad() {
        startActvivityByPackageName("com.darekbx.dotpad3")
    }

    private fun displayDotsCount(count: Int) {
        binding.dotPadButton.text = "$count"
    }

    private fun startActvivityByPackageName(packageName: String) {
        val launchIntent = activity
            ?.packageManager
            ?.getLaunchIntentForPackage(packageName)
        if (launchIntent != null) {
            startActivity(launchIntent)
        }
    }
}
