package com.darekbx.launcher3.ui

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager
import com.darekbx.launcher3.R
import com.darekbx.launcher3.databinding.FragmentMainBinding
import com.darekbx.launcher3.ui.airly.AirlyFragment
import com.darekbx.launcher3.ui.actionicons.ActionIconsFragment
import com.darekbx.launcher3.ui.screenon.ScreenOnFragment
import com.darekbx.launcher3.ui.settings.SettingsActivity
import com.darekbx.launcher3.ui.sunrisesunset.SunriseSunsetFragment
import com.darekbx.launcher3.ui.weather.WeatherFragment
import org.koin.core.component.KoinApiExtension

@KoinApiExtension
class MainFragment : Fragment() {

    companion object {
        private val ENABLE_SCREEN_ON = false
    }

    private val resultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == SettingsActivity.PREFERENCES_WERE_CHANGED) {
            setWeatherSwitchState()
        }
    }

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bindGlobalRefreshButton()
        bindAppsButton()
        bindWeatherSwitch()
        bindSettingsButton()

        permissionRequester.runWithPermissions {
            if (childFragmentManager.fragments.isEmpty()) {
                val transaction = childFragmentManager
                    .beginTransaction()
                    .add(R.id.airly_fragment, AirlyFragment())
                    .add(R.id.weather_fragment, WeatherFragment())
                    //.add(R.id.sunrise_sunset_fragment, SunriseSunsetFragment())
                    .add(R.id.main_icons_fragment, ActionIconsFragment())

                if (ENABLE_SCREEN_ON) {
                    transaction.add(R.id.screen_on_fragment, ScreenOnFragment())
                }

                transaction.commit()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun bindWeatherSwitch() {
        setWeatherSwitchState()
        binding.weatherSwitch.setOnCheckedChangeListener { _, isChecked ->
            settingsPreferences.edit().putBoolean("use_antistorm", isChecked).apply()
            val weatherFragment =
                childFragmentManager.findFragmentById(R.id.weather_fragment) as WeatherFragment
            weatherFragment.refresh()
        }
    }

    private fun setWeatherSwitchState() {
        val useAntistorm = settingsPreferences.getBoolean("use_antistorm", false)
        binding.weatherSwitch.isChecked = useAntistorm
        val label = when (useAntistorm) {
            true -> R.string.use_antistorm
            false -> R.string.use_rainviewer
        }
        binding.weatherLabel.setText(label)
    }

    private fun bindSettingsButton() {
        binding.settings.setOnClickListener {
            openSettings()
        }
    }

    private fun openSettings() {
        resultLauncher.launch(Intent(requireContext(), SettingsActivity::class.java))
    }

    private fun bindGlobalRefreshButton() {
        binding.globalRefresh.setOnClickListener {
            globalRefresh()
        }
    }

    private fun bindAppsButton() {
        binding.openOwnSpace.setOnClickListener {
            startActvivityByPackageName("com.darekbx.ownspace")
        }
    }

    private fun globalRefresh() {
        childFragmentManager.fragments
            .filter { it is RefreshableFragment }
            .forEach { (it as RefreshableFragment).refresh() }
    }

    private val permissionRequester by lazy {
        PermissionRequester(
            requireActivity(),
            Manifest.permission.ACCESS_FINE_LOCATION,
            onDenied = { showRationale() },
            onShowRationale = { showDeniedPermissionInformation() }
        )
    }

    private fun showRationale() {
        Toast.makeText(requireContext(), R.string.location_permission_rationale, Toast.LENGTH_SHORT)
            .show()
    }

    private fun showDeniedPermissionInformation() {
        Toast.makeText(requireContext(), R.string.location_permission_denied, Toast.LENGTH_SHORT)
            .show()
    }

    private val settingsPreferences by lazy {
        PreferenceManager.getDefaultSharedPreferences(requireContext())
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
