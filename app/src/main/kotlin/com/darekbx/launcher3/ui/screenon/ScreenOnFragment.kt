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
import java.util.concurrent.TimeUnit

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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        screenOnViewModel.screenOn.observe(viewLifecycleOwner, { secondsOn ->
            displayScreenOn(secondsOn)
        })
    }

    private fun displayScreenOn(secondsOn: Long?) {
        val second = TimeUnit.MINUTES.toSeconds(1)
        val hour = TimeUnit.HOURS.toSeconds(1)
        if (secondsOn == null) return
        val hours = secondsOn / hour
        val minutes = (secondsOn % hour) / second
        val seconds = secondsOn % second
        binding.screenOn.setText(getString(R.string.screen_on_format,
            "${hours.padTwoDigit()}:${minutes.padTwoDigit()}:${seconds.padTwoDigit()}"))
    }

    private fun Long.padTwoDigit() = this.toString().padStart(2, '0')
}
