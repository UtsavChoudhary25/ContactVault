package com.utsav.contactvault.data.repository

import android.content.Context
import android.provider.ContactsContract
import com.utsav.contactvault.data.model.Contact
import com.utsav.contactvault.data.model.PhoneNumber
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Repository responsible for accessing device contacts using Android ContentResolver
 * and ContactsContract API.
 */
class ContactsRepository(private val context: Context) {

    /**
     * Fetches contacts asynchronously using ContentResolver and ContactsContract.CommonDataKinds.Phone.CONTENT_URI.
     * Aggregates multiple phone numbers under a single contact record.
     */
    suspend fun getContacts(): List<Contact> = withContext(Dispatchers.IO) {
        val contactsMap = mutableMapOf<String, MutableContactHolder>()

        val projection = arrayOf(
            ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
            ContactsContract.CommonDataKinds.Phone.LOOKUP_KEY,
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME_PRIMARY,
            ContactsContract.CommonDataKinds.Phone.NUMBER,
            ContactsContract.CommonDataKinds.Phone.TYPE,
            ContactsContract.CommonDataKinds.Phone.LABEL,
            ContactsContract.CommonDataKinds.Phone.PHOTO_URI
        )

        val sortOrder = "${ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME_PRIMARY} ASC"

        try {
            val cursor = context.contentResolver.query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                projection,
                null,
                null,
                sortOrder
            )

            cursor?.use { c ->
                val idIndex = c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID)
                val lookupIndex = c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.LOOKUP_KEY)
                val nameIndex = c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME_PRIMARY)
                val numberIndex = c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
                val typeIndex = c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE)
                val labelIndex = c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.LABEL)
                val photoIndex = c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_URI)

                while (c.moveToNext()) {
                    val id = if (idIndex != -1) c.getString(idIndex) ?: "" else ""
                    val lookupKey = if (lookupIndex != -1) c.getString(lookupIndex) ?: "" else ""
                    val name = if (nameIndex != -1) c.getString(nameIndex) ?: "Unknown Contact" else "Unknown Contact"
                    val rawNumber = if (numberIndex != -1) c.getString(numberIndex) ?: "" else ""
                    val type = if (typeIndex != -1) c.getInt(typeIndex) else ContactsContract.CommonDataKinds.Phone.TYPE_OTHER
                    val customLabel = if (labelIndex != -1) c.getString(labelIndex) else null
                    val photoUri = if (photoIndex != -1) c.getString(photoIndex) else null

                    // Format phone number label (Mobile, Home, Work, etc.)
                    val typeLabel = ContactsContract.CommonDataKinds.Phone.getTypeLabel(
                        context.resources,
                        type,
                        customLabel
                    ).toString()

                    val cleanNumber = rawNumber.trim()
                    if (cleanNumber.isNotEmpty() && id.isNotEmpty()) {
                        val holder = contactsMap.getOrPut(id) {
                            MutableContactHolder(
                                id = id,
                                lookupKey = lookupKey,
                                displayName = name,
                                photoUri = photoUri,
                                phoneNumbers = mutableListOf()
                            )
                        }

                        // Avoid duplicate numbers for the same contact
                        if (holder.phoneNumbers.none { it.number == cleanNumber }) {
                            holder.phoneNumbers.add(PhoneNumber(cleanNumber, typeLabel))
                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        // Convert mutable holders to immutable Contact domain models
        contactsMap.values
            .map { holder ->
                Contact(
                    id = holder.id,
                    lookupKey = holder.lookupKey,
                    displayName = holder.displayName,
                    phoneNumbers = holder.phoneNumbers.toList(),
                    photoUri = holder.photoUri
                )
            }
            .sortedBy { it.displayName.lowercase() }
    }

    private data class MutableContactHolder(
        val id: String,
        val lookupKey: String,
        val displayName: String,
        val photoUri: String?,
        val phoneNumbers: MutableList<PhoneNumber>
    )
}
