package com.naulian.glow

import android.content.Context
import com.naulian.anhance.isNightMode

object CodeTheme {

    @Deprecated("Deprecated\nUse CodeTheme.default(context) instead.")
    val kotlinLight = Theme()

    private val defaultLight = Theme(
        background = "#ffffff",
        normal = "#000000",
        string = "#067D17",
        comment = "#8C8C8C",
        method = "#FF9B00",
        number = "#0000FF",
        keyword = "#0033B3",
        variable = "#000000",
        property = "#871094"
    )

    fun default(context: Context) =
        if (context.isNightMode()) Theme() else defaultLight
}