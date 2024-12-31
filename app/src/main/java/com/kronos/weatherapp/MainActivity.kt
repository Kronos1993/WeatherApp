package com.kronos.weatherapp

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.kronos.core.extensions.binding.activityBinding
import com.kronos.core.util.PreferencesUtil
import com.kronos.core.util.setLanguageForApp
import com.kronos.core.util.validatePermission
import com.kronos.weatherapp.databinding.ActivityMainBinding
import com.kronos.weatherapp.ui.settings.OnSettingChangeObserver
import com.kronos.weatherapp.ui.settings.OnSettingChangePublisher
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val binding by activityBinding<ActivityMainBinding>(R.layout.activity_main)
    private var grantedAll = false

    @Inject
    lateinit var onSettingChangePublisher: OnSettingChangePublisher

    private var onSettingChangeObserver = object : OnSettingChangeObserver {
        override fun onSettingChange(oldValue: String, newValue: String) {
            this@MainActivity.recreate()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.run {
            lifecycleOwner = this@MainActivity
            setContentView(root)
            checkPermission()
            onSettingChangePublisher.subscribe(onSettingChangeObserver)
        }
    }

    private fun checkPermission() {
        grantedAll =
            validatePermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) && validatePermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) && validatePermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            )
        if (!grantedAll) {
            ActivityCompat.requestPermissions(
                this, arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.POST_NOTIFICATIONS
                ),
                1
            )
        } else {
            init()
        }
    }

    private fun init(){
        setLanguageForApp(this,
            PreferencesUtil.getPreference(this,this.getString(R.string.default_lang_key),this.getString(R.string.default_language_value))!!)
        val navView: BottomNavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        navView.setupWithNavController(navController)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        var grantedPermissions = true
        for (grantResult in grantResults) {
            grantedPermissions =
                grantedPermissions and (grantResult == PackageManager.PERMISSION_GRANTED)
        }
        if (grantedPermissions) {
            init()
        } else {
            finish()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        onSettingChangePublisher.unSubscribe(onSettingChangeObserver)
    }

}