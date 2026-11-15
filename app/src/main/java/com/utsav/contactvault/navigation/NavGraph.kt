package com.utsav.contactvault.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.utsav.contactvault.ui.screens.AboutScreen
import com.utsav.contactvault.ui.screens.DashboardScreen
import com.utsav.contactvault.ui.screens.DetailScreen
import com.utsav.contactvault.ui.screens.PermissionScreen
import com.utsav.contactvault.ui.viewmodel.ContactsUiState
import com.utsav.contactvault.ui.viewmodel.ContactsViewModel

sealed class Screen(val route: String) {
    object Permission : Screen("permission")
    object Dashboard : Screen("dashboard")
    object Detail : Screen("detail/{contactId}") {
        fun createRoute(contactId: String) = "detail/$contactId"
    }
    object About : Screen("about")
}

@Composable
fun ContactVaultNavGraph(
    navController: NavHostController,
    viewModel: ContactsViewModel,
    hasPermission: Boolean,
    onRequestPermission: () -> Unit,
    onOpenSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()

    val startDestination = if (hasPermission) Screen.Dashboard.route else Screen.Permission.route

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable(Screen.Permission.route) {
            val isPermanentlyDenied = uiState is ContactsUiState.PermissionDenied &&
                    (uiState as ContactsUiState.PermissionDenied).isPermanentlyDenied

            PermissionScreen(
                onRequestPermission = onRequestPermission,
                onOpenSettings = onOpenSettings,
                isPermanentlyDenied = isPermanentlyDenied
            )
        }

        composable(Screen.Dashboard.route) {
            DashboardScreen(
                uiState = uiState,
                searchQuery = searchQuery,
                onSearchQueryChanged = viewModel::onSearchQueryChanged,
                onRefresh = viewModel::loadContacts,
                onContactClick = { contact ->
                    navController.navigate(Screen.Detail.createRoute(contact.id))
                },
                onAboutClick = {
                    navController.navigate(Screen.About.route)
                }
            )
        }

        composable(
            route = Screen.Detail.route,
            arguments = listOf(navArgument("contactId") { type = NavType.StringType })
        ) { backStackEntry ->
            val contactId = backStackEntry.arguments?.getString("contactId") ?: ""
            val contact = viewModel.getContactById(contactId)

            if (contact != null) {
                DetailScreen(
                    contact = contact,
                    onBackClick = { navController.popBackStack() }
                )
            } else {
                navController.popBackStack()
            }
        }

        composable(Screen.About.route) {
            AboutScreen(
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}
