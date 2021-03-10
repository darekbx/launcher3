package com.darekbx.launcher3.ui

import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.Gravity
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import com.darekbx.launcher3.R
import com.darekbx.launcher3.databinding.ActivityDrawerBinding
import com.darekbx.launcher3.screenon.ScreenOnReceiver
import com.darekbx.launcher3.ui.applications.ApplicationsFragment

class DrawerActivity : AppCompatActivity(), DrawerLayout.DrawerListener {

    private val screenOnReceiver by lazy { ScreenOnReceiver() }

    private var _binding: ActivityDrawerBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _binding = ActivityDrawerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.drawerLayout.addDrawerListener(this)

        if (supportFragmentManager.fragments.isEmpty()) {
            supportFragmentManager
                .beginTransaction()
                .add(R.id.content_frame, MainFragment())
                .add(R.id.right_drawer, ApplicationsFragment())
                .commit()
        }

        registerReceiver(screenOnReceiver, IntentFilter().apply {
            addAction(Intent.ACTION_USER_PRESENT)
            addAction(Intent.ACTION_SCREEN_OFF)
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(screenOnReceiver)
        binding.drawerLayout.removeDrawerListener(this)
        _binding = null
    }

    override fun onBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(Gravity.RIGHT)) {
            binding.drawerLayout.closeDrawers()
        }
    }

    override fun onDrawerSlide(drawerView: View, slideOffset: Float) { }

    override fun onDrawerOpened(drawerView: View) { }

    override fun onDrawerClosed(drawerView: View) { }

    override fun onDrawerStateChanged(newState: Int) { }
}
