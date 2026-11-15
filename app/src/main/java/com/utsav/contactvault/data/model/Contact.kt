package com.utsav.contactvault.data.model

/**
 * Data class representing a phone number with its category label (Mobile, Home, Work, etc.)
 */
data class PhoneNumber(
    val number: String,
    val typeLabel: String
)

/**
 * Data class representing a Contact fetched via ContentResolver from ContactsContract.
 */
data class Contact(
    val id: String,
    val lookupKey: String,
    val displayName: String,
    val phoneNumbers: List<PhoneNumber>,
    val photoUri: String? = null
) {
    /**
     * Primary phone number for quick display in lists.
     */
    val primaryPhone: String
        get() = phoneNumbers.firstOrNull()?.number ?: "No phone number available"

    /**
     * First letter for circular initial avatar.
     */
    val initial: String
        get() = displayName.trim().firstOrNull()?.uppercaseChar()?.toString() ?: "?"
}
