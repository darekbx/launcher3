package com.darekbx.launcher3.ui.screenon

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.darekbx.launcher3.R
import com.darekbx.launcher3.databinding.FragmentScreenOnBinding
import com.darekbx.launcher3.screenon.ScreenOnController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.android.ext.android.inject

class ScreenOnFragment : Fragment(R.layout.fragment_screen_on) {

    private val screenOnController: ScreenOnController by inject()

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

        CoroutineScope(Dispatchers.IO).launch {
            screenOnController.currentDailyTime().collect { screenOnPreferences ->
                val seconds = screenOnPreferences.dailyTime / 1000L
                withContext(Dispatchers.Main) {
                    binding.screenOn.setText(
                        context?.getString(R.string.screen_on_format, "${seconds}s")
                    )
                }
            }
        }
    }
}
