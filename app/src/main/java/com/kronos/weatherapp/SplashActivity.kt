package com.kronos.weatherapp

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import androidx.annotation.Nullable
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.kronos.core.extensions.binding.activityBinding
import com.kronos.core.util.PreferencesUtil
import com.kronos.core.util.navigate
import com.kronos.core.util.setLanguageForApp
import com.kronos.core.util.validatePermission
import com.kronos.logger.LoggerType
import com.kronos.logger.interfaces.ILogger
import com.kronos.weatherapp.databinding.ActivitySplashBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {

    private val binding by activityBinding<ActivitySplashBinding>(R.layout.activity_splash)
    private var grantedAll = false
    private var grantedFullStorage = false

    @Inject
    lateinit var logger: ILogger

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setLanguageForApp(baseContext,PreferencesUtil.getPreference(applicationContext,applicationContext.getString(R.string.default_lang_key),applicationContext.getString(R.string.default_language_value))!!)
        binding.run {
            lifecycleOwner = this@SplashActivity
            setContentView(root)
            if (!checkPermission()) {
                showPermissionRequiredDialog()
            } else {
                logger.write(this::class.java.name, LoggerType.INFO, "Permission checked")
                init()
            }
            logger.write(this::class.java.name, LoggerType.INFO,"Splash activity Initialized")
        }
    }

    private fun checkPermission():Boolean {
        grantedAll =
            validatePermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) && validatePermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
            grantedAll = validatePermission(this, Manifest.permission.POST_NOTIFICATIONS)

        return grantedAll
    }

    private fun showPermissionRequiredDialog(){
        MaterialAlertDialogBuilder(
            this,
            com.kronos.resources.R.style.Widget_AlertDialog_RoundShapeTheme
        )
            .setTitle(com.kronos.resources.R.string.permission_dialog_title)
            .setMessage(com.kronos.resources.R.string.permission_dialog_message)
            .setCancelable(false)
            .setPositiveButton(com.kronos.resources.R.string.button_text_ok) { dialogInterface, _ ->
                askPermission()
                dialogInterface.dismiss()
            }
            .setNegativeButton(com.kronos.resources.R.string.cancel) { dialogInterface, _ ->
                dialogInterface.dismiss()
                finish()
            }
            .create().show()
    }

    private fun askPermission() {
        checkFullStorageAccess()
    }

    private fun checkFullStorageAccess() {
        logger.write(this::class.java.name, LoggerType.INFO,"Checking storage permission")

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            grantedFullStorage = Environment.isExternalStorageManager()
            if (!grantedFullStorage) {
                MaterialAlertDialogBuilder(this, com.kronos.resources.R.style.Widget_AlertDialog_RoundShapeTheme)
                    .setTitle(R.string.storage_permission_dialog_title)
                    .setMessage(R.string.storage_permission_dialog_message)
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
                    .create().show();
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
            logger.write(this::class.java.name, LoggerType.INFO,"Permission granted")
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
        logger.write(this::class.java.name, LoggerType.INFO,"Splash activity timer run")
        navigate(this, MainActivity::class.java)
        finish()
    }


}