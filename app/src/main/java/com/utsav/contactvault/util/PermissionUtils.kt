package com.utsav.contactvault.util

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.core.content.ContextCompat

object PermissionUtils {

    const val CONTACTS_PERMISSION = Manifest.permission.READ_CONTACTS

    /**
     * Checks if READ_CONTACTS permission has been granted.
     */
    fun hasContactsPermission(context: Context): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            CONTACTS_PERMISSION
        ) == PackageManager.PERMISSION_GRANTED
    }

    /**
     * Opens Application Details Settings page so the user can grant permission manually.
     */
    fun openAppSettings(context: Context) {
        val intent = Intent(
            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
            Uri.fromParts("package", context.packageName, null)
        ).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    }
}
