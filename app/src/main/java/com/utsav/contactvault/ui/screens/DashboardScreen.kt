package com.utsav.contactvault.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.utsav.contactvault.data.model.Contact
import com.utsav.contactvault.ui.components.ContactCard
import com.utsav.contactvault.ui.components.ContactSearchBar
import com.utsav.contactvault.ui.components.EmptyState
import com.utsav.contactvault.ui.viewmodel.ContactsUiState

/**
 * Main Contacts Dashboard screen displaying search bar, total count, and sorted contact cards.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    uiState: ContactsUiState,
    searchQuery: String,
    onSearchQueryChanged: (String) -> Unit,
    onRefresh: () -> Unit,
    onContactClick: (Contact) -> Unit,
    onAboutClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "ContactVault",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        if (uiState is ContactsUiState.Success) {
                            Text(
                                text = "MAD Lab Exp 10 • Contacts ContentProvider",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
                actions = {
                    if (uiState is ContactsUiState.Success) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.primaryContainer,
                            modifier = Modifier.padding(end = 8.dp)
                        ) {
                            Text(
                                text = "${uiState.filteredContacts.size} / ${uiState.contacts.size}",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }
                    }

                    IconButton(onClick = onRefresh) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Refresh Contacts"
                        )
                    }

                    IconButton(onClick = onAboutClick) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "About Experiment"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (uiState) {
                is ContactsUiState.Loading, ContactsUiState.Initial -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Querying Contacts via ContentResolver...",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                is ContactsUiState.Empty -> {
                    EmptyState(
                        title = "No Contacts Found",
                        message = uiState.message,
                        actionLabel = "Refresh",
                        onAction = onRefresh
                    )
                }

                is ContactsUiState.Error -> {
                    EmptyState(
                        title = "Error Loading Contacts",
                        message = uiState.message,
                        actionLabel = "Retry",
                        onAction = onRefresh
                    )
                }

                is ContactsUiState.Success -> {
                    ContactSearchBar(
                        query = searchQuery,
                        onQueryChanged = onSearchQueryChanged
                    )

                    if (uiState.filteredContacts.isEmpty()) {
                        EmptyState(
                            title = "No Matching Contacts",
                            message = "No contacts match search query \"$searchQuery\"",
                            icon = Icons.Default.SearchOff,
                            actionLabel = "Clear Search",
                            onAction = { onSearchQueryChanged("") }
                        )
                    } else {
                        LazyColumn(
                            contentPadding = PaddingValues(bottom = 24.dp)
                        ) {
                            items(
                                items = uiState.filteredContacts,
                                key = { it.id }
                            ) { contact ->
                                ContactCard(
                                    contact = contact,
                                    onClick = { onContactClick(contact) }
                                )
                            }
                        }
                    }
                }

                is ContactsUiState.PermissionDenied -> {
                    // Handled in root NavGraph or parent container
                }
            }
        }
    }
}
