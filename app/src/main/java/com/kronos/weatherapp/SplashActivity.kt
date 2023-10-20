package com.kronos.weatherapp

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.*
import android.provider.Settings
import androidx.annotation.Nullable
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.kronos.core.extensions.binding.activityBinding
import com.kronos.core.util.navigate
import com.kronos.core.util.validatePermission
import com.kronos.weatherapp.databinding.ActivitySplashBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {

    private val binding by activityBinding<ActivitySplashBinding>(R.layout.activity_splash)
    private var grantedAll = false
    private var grantedFullStorage = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.run {
            lifecycleOwner = this@SplashActivity
            setContentView(root)
            checkFullStorageAccess()
        }
    }

    private fun checkPermission() {
        grantedAll =
            validatePermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) && validatePermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        if (!grantedAll) {
            ActivityCompat.requestPermissions(
                this, arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ),
                1
            )
        } else {
            init()
        }
    }

    private fun checkFullStorageAccess() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            grantedFullStorage = Environment.isExternalStorageManager()
            if (!grantedFullStorage) {
                MaterialAlertDialogBuilder(this)
                    .setMessage(getString(R.string.permission_dialog_message))
                    .setTitle(getString(R.string.permission_dialog_title))
                    .setPositiveButton(R.string.ok) { dialogInterface, _ ->
                        com.kronos.core.util.startActivityForResult(
                            this,
                            Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION,
                            150
                        )
                        dialogInterface.dismiss()
                    }
                    .setNegativeButton(R.string.cancel) { dialogInterface, _ ->
                        dialogInterface.dismiss()
                        finish()
                    }
                    .create()
                    .show()
            }else{
                init()
            }
        }else{
            checkPermission()
        }
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, @Nullable data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 150) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                checkFullStorageAccess()
            }
        }
    }

    private fun init() {
        Handler(Looper.getMainLooper()).postDelayed({
            navigate(this, MainActivity::class.java)
            finish()
        }, 300)
    }


}