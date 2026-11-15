package com.utsav.contactvault.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.utsav.contactvault.data.model.Contact
import com.utsav.contactvault.data.repository.ContactsRepository
import com.utsav.contactvault.util.PermissionUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for managing Contacts state, search filtering, and repository calls.
 */
class ContactsViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = ContactsRepository(application)

    private val _uiState = MutableStateFlow<ContactsUiState>(ContactsUiState.Initial)
    val uiState: StateFlow<ContactsUiState> = _uiState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private var allContacts: List<Contact> = emptyList()

    init {
        checkPermissionAndLoadContacts()
    }

    /**
     * Checks runtime permission status and loads contacts if granted.
     */
    fun checkPermissionAndLoadContacts() {
        if (PermissionUtils.hasContactsPermission(getApplication())) {
            loadContacts()
        } else {
            _uiState.value = ContactsUiState.PermissionDenied(isPermanentlyDenied = false)
        }
    }

    /**
     * Called when runtime permission is explicitly granted by the user.
     */
    fun onPermissionGranted() {
        loadContacts()
    }

    /**
     * Called when permission is denied.
     */
    fun onPermissionDenied(isPermanentlyDenied: Boolean) {
        _uiState.value = ContactsUiState.PermissionDenied(isPermanentlyDenied = isPermanentlyDenied)
    }

    /**
     * Loads contacts from ContentResolver via ContactsRepository.
     */
    fun loadContacts() {
        viewModelScope.launch {
            _uiState.value = ContactsUiState.Loading
            try {
                val contacts = repository.getContacts()
                allContacts = contacts
                if (contacts.isEmpty()) {
                    _uiState.value = ContactsUiState.Empty()
                } else {
                    val query = _searchQuery.value
                    val filtered = filterContactsList(contacts, query)
                    _uiState.value = ContactsUiState.Success(
                        contacts = contacts,
                        searchQuery = query,
                        filteredContacts = filtered
                    )
                }
            } catch (e: Exception) {
                _uiState.value = ContactsUiState.Error("Failed to load contacts: ${e.message}")
            }
        }
    }

    /**
     * Updates search query and filters contact list live.
     */
    fun onSearchQueryChanged(newQuery: String) {
        _searchQuery.value = newQuery
        val currentState = _uiState.value
        if (currentState is ContactsUiState.Success) {
            val filtered = filterContactsList(allContacts, newQuery)
            _uiState.value = currentState.copy(
                searchQuery = newQuery,
                filteredContacts = filtered
            )
        }
    }

    /**
     * Helper to filter contacts by display name or phone numbers.
     */
    private fun filterContactsList(list: List<Contact>, query: String): List<Contact> {
        if (query.isBlank()) return list
        val trimmedQuery = query.trim().lowercase()
        return list.filter { contact ->
            contact.displayName.lowercase().contains(trimmedQuery) ||
                    contact.phoneNumbers.any { it.number.lowercase().contains(trimmedQuery) }
        }
    }

    /**
     * Returns a contact by ID from current list.
     */
    fun getContactById(id: String): Contact? {
        return allContacts.find { it.id == id }
    }
}
