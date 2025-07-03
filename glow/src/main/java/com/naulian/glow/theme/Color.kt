@file:Suppress("unused")

package com.naulian.glow.theme

import android.util.Log
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import com.naulian.anhance.toColorInt
import com.naulian.modify.Fonts

fun spanStyle(color: Color) = SpanStyle(
    color = color, fontFamily = Fonts.JetBrainsMono
)

fun String.toAnnotatedString(): AnnotatedString {
    return buildAnnotatedString {
        append(this@toAnnotatedString)
    }
}

fun String.hexToColor(): Color {
    return if (startsWith("#")) {
        val legacyColor = this.toColorInt()
        Color(legacyColor)
    } else {
        Log.i("Glow","hexToColor: color should start with #. for $this")
        Color.Black
    }
}

/**
 * Converts a Jetpack Compose Color to a hex string
 * @param includeAlpha Whether to include alpha channel in the hex string
 * @return Hex string representation (e.g., "FFFFFF" or "FFFFFFFF")
 */
fun Color.toHex(includeAlpha: Boolean = false): String {
    val argb = this.toArgb()

    return if (includeAlpha) {
        // Include alpha channel (AARRGGBB format)
        String.format("%08X", argb)
    } else {
        // Only RGB channels (RRGGBB format)
        String.format("%06X", argb and 0xFFFFFF)
    }
}

/**
 * Alternative method using individual color components
 */
fun Color.toHexString(includeAlpha: Boolean = false): String {
    val red = (this.red * 255).toInt()
    val green = (this.green * 255).toInt()
    val blue = (this.blue * 255).toInt()
    val alpha = (this.alpha * 255).toInt()

    return if (includeAlpha) {
        String.format("%02X%02X%02X%02X", alpha, red, green, blue)
    } else {
        String.format("%02X%02X%02X", red, green, blue)
    }
}

// Usage examples:
fun main() {
    // Example usage
    val whiteColor = Color(0xFFFFFFFF)
    val redColor = Color(0xFFFF0000)
    val blueColor = Color(0xFF0000FF)
    val transparentRed = Color(0x80FF0000)

    println("White: ${whiteColor.toHex()}") // Output: "FFFFFF"
    println("White with alpha: ${whiteColor.toHex(true)}") // Output: "FFFFFFFF"

    println("Red: ${redColor.toHex()}") // Output: "FF0000"
    println("Blue: ${blueColor.toHex()}") // Output: "0000FF"

    println("Transparent red: ${transparentRed.toHex()}") // Output: "FF0000"
    println("Transparent red with alpha: ${transparentRed.toHex(true)}") // Output: "80FF0000"
}