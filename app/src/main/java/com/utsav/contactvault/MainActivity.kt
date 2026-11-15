package com.utsav.contactvault

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.core.app.ActivityCompat
import androidx.navigation.compose.rememberNavController
import com.utsav.contactvault.navigation.ContactVaultNavGraph
import com.utsav.contactvault.navigation.Screen
import com.utsav.contactvault.ui.theme.ContactVaultTheme
import com.utsav.contactvault.ui.viewmodel.ContactsViewModel
import com.utsav.contactvault.util.PermissionUtils

class MainActivity : ComponentActivity() {

    private val viewModel: ContactsViewModel by viewModels()

    private var hasPermissionState by mutableStateOf(false)

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasPermissionState = isGranted
        if (isGranted) {
            viewModel.onPermissionGranted()
        } else {
            val showRationale = ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                PermissionUtils.CONTACTS_PERMISSION
            )
            viewModel.onPermissionDenied(isPermanentlyDenied = !showRationale)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        hasPermissionState = PermissionUtils.hasContactsPermission(this)

        setContent {
            ContactVaultTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()

                    ContactVaultNavGraph(
                        navController = navController,
                        viewModel = viewModel,
                        hasPermission = hasPermissionState,
                        onRequestPermission = { requestPermission() },
                        onOpenSettings = { PermissionUtils.openAppSettings(this) }
                    )
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // Re-check permission on resume (e.g., when returning from Settings)
        val currentPermission = PermissionUtils.hasContactsPermission(this)
        if (currentPermission != hasPermissionState) {
            hasPermissionState = currentPermission
            if (currentPermission) {
                viewModel.onPermissionGranted()
            }
        }
    }

    private fun requestPermission() {
        requestPermissionLauncher.launch(PermissionUtils.CONTACTS_PERMISSION)
    }
}
