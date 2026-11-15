package com.utsav.contactvault.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import com.utsav.contactvault.data.model.Contact

object ShareUtils {

    /**
     * Shares contact details via Android Sharesheet using ACTION_SEND intent.
     */
    fun shareContact(context: Context, contact: Contact) {
        val phoneNumbersText = contact.phoneNumbers.joinToString("\n") { phone ->
            "• ${phone.typeLabel}: ${phone.number}"
        }

        val shareText = """
            📱 Contact Details (Shared via ContactVault)
            ━━━━━━━━━━━━━━━━━━━━━━━━━━━━
            Name: ${contact.displayName}
            
            Phone Numbers:
            $phoneNumbersText
            ━━━━━━━━━━━━━━━━━━━━━━━━━━━━
        """.trimIndent()

        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, "Contact Details: ${contact.displayName}")
            putExtra(Intent.EXTRA_TEXT, shareText)
        }

        val chooserIntent = Intent.createChooser(shareIntent, "Share contact details via")
        try {
            context.startActivity(chooserIntent)
            Toast.makeText(context, "Initiating contact share...", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(context, "Unable to share contact: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    /**
     * Dials a phone number using ACTION_DIAL intent (opens phone dialer with number prefilled).
     */
    fun dialPhoneNumber(context: Context, phoneNumber: String) {
        if (phoneNumber.isBlank()) return
        val cleanNumber = phoneNumber.replace(Regex("[^0-9+]"), "")
        val dialIntent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$cleanNumber"))
        try {
            context.startActivity(dialIntent)
        } catch (e: Exception) {
            Toast.makeText(context, "Cannot open dialer: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Sends SMS using ACTION_SENDTO intent.
     */
    fun sendSms(context: Context, phoneNumber: String) {
        if (phoneNumber.isBlank()) return
        val cleanNumber = phoneNumber.replace(Regex("[^0-9+]"), "")
        val smsIntent = Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:$cleanNumber"))
        try {
            context.startActivity(smsIntent)
        } catch (e: Exception) {
            Toast.makeText(context, "Cannot open SMS app: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
}
