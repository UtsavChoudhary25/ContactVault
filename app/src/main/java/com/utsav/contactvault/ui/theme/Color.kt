package com.utsav.contactvault.ui.theme

import androidx.compose.ui.graphics.Color

// Light Theme Colors
val PrimaryLight = Color(0xFF1976D2)
val OnPrimaryLight = Color(0xFFFFFFFF)
val PrimaryContainerLight = Color(0xFFD0E4FF)
val OnPrimaryContainerLight = Color(0xFF001D36)

val SecondaryLight = Color(0xFF00897B)
val OnSecondaryLight = Color(0xFFFFFFFF)
val SecondaryContainerLight = Color(0xFFB2DFDB)
val OnSecondaryContainerLight = Color(0xFF00201A)

val BackgroundLight = Color(0xFFF8F9FA)
val OnBackgroundLight = Color(0xFF1A1C1E)
val SurfaceLight = Color(0xFFFFFFFF)
val OnSurfaceLight = Color(0xFF1A1C1E)
val SurfaceVariantLight = Color(0xFFE1E2EC)
val OnSurfaceVariantLight = Color(0xFF44474F)

// Dark Theme Colors
val PrimaryDark = Color(0xFF9ECAFF)
val OnPrimaryDark = Color(0xFF003258)
val PrimaryContainerDark = Color(0xFF00497D)
val OnPrimaryContainerDark = Color(0xFFD0E4FF)

val SecondaryDark = Color(0xFF80CBC4)
val OnSecondaryDark = Color(0xFF003731)
val SecondaryContainerDark = Color(0xFF004D40)
val OnSecondaryContainerDark = Color(0xFFB2DFDB)

val BackgroundDark = Color(0xFF121316)
val OnBackgroundDark = Color(0xFFE2E2E6)
val SurfaceDark = Color(0xFF1A1C1E)
val OnSurfaceDark = Color(0xFFE2E2E6)
val SurfaceVariantDark = Color(0xFF44474F)
val OnSurfaceVariantDark = Color(0xFFC4C6D0)

// Avatar Background Palette
val AvatarColors = listOf(
    Color(0xFFE57373), // Red
    Color(0xFFF06292), // Pink
    Color(0xFFBA68C8), // Purple
    Color(0xFF9575CD), // Deep Purple
    Color(0xFF7986CB), // Indigo
    Color(0xFF64B5F6), // Blue
    Color(0xFF4FC3F7), // Light Blue
    Color(0xFF4DD0E1), // Cyan
    Color(0xFF4DB6AC), // Teal
    Color(0xFF81C784), // Green
    Color(0xFFAED581), // Light Green
    Color(0xFFFFB74D), // Orange
    Color(0xFFFF8A65)  // Deep Orange
)

/**
 * Returns a deterministic background color for an avatar based on contact name.
 */
fun getAvatarColor(name: String): Color {
    val hash = name.fold(0) { acc, char -> acc + char.code }
    val index = Math.abs(hash) % AvatarColors.size
    return AvatarColors[index]
}
