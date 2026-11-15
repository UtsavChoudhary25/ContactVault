package com.utsav.contactvault.ui.viewmodel

import com.utsav.contactvault.data.model.Contact

/**
 * UI State representation for the ContactVault application screens.
 */
sealed interface ContactsUiState {
    /** Initial state when app opens before checking permissions or loading data */
    object Initial : ContactsUiState

    /** State while fetching contacts asynchronously */
    object Loading : ContactsUiState

    /** Successfully fetched contacts state */
    data class Success(
        val contacts: List<Contact>,
        val searchQuery: String = "",
        val filteredContacts: List<Contact> = contacts,
        val selectedContact: Contact? = null
    ) : ContactsUiState

    /** Empty state when no contacts exist on the device */
    data class Empty(
        val message: String = "No contacts found on your device."
    ) : ContactsUiState

    /** Error state if contact fetching fails */
    data class Error(
        val message: String
    ) : ContactsUiState

    /** Permission denied state with flag for permanent denial */
    data class PermissionDenied(
        val isPermanentlyDenied: Boolean = false
    ) : ContactsUiState
}
