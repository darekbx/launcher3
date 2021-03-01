package com.darekbx.launcher3.ui.screenon

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.darekbx.launcher3.R
import com.darekbx.launcher3.databinding.FragmentScreenOnBinding
import com.darekbx.launcher3.viewmodel.ScreenOnViewModel
import org.koin.android.viewmodel.ext.android.viewModel

class ScreenOnFragment : Fragment(R.layout.fragment_screen_on) {

    private val screenOnViewModel: ScreenOnViewModel by viewModel()

    private var _binding: FragmentScreenOnBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentScreenOnBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        screenOnViewModel.screenOn.observe(viewLifecycleOwner, { secondsOn ->
            displayScreenOn(secondsOn)
        })
    }

    private fun displayScreenOn(secondsOn: Long?) {
        if (secondsOn == null) return
        val hours = secondsOn / 3600
        val minutes = (secondsOn % 3600) / 60
        val seconds = secondsOn % 60
        binding.screenOn.setText(getString(R.string.screen_on_format,
            "${hours.padTwoDigit()}:${minutes.padTwoDigit()}:${seconds.padTwoDigit()}"))
    }

    private fun Long.padTwoDigit() = this.toString().padStart(2, '0')
}
