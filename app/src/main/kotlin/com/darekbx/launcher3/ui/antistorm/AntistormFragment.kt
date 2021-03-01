package com.darekbx.launcher3.ui.antistorm

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.darekbx.launcher3.R
import com.darekbx.launcher3.databinding.FragmentAntistormBinding
import com.darekbx.launcher3.viewmodel.AntistormViewModel
import org.koin.android.viewmodel.ext.android.viewModel

class AntistormFragment : Fragment(R.layout.fragment_antistorm) {

    private val antistormViewModel: AntistormViewModel by viewModel()

    private var _binding: FragmentAntistormBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAntistormBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }
}
