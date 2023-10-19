package com.naulian.glow

import android.content.Context
import com.naulian.anhance.isNightMode

object CodeTheme {

    private val defaultLight = Theme(
        keyword = "#0033B3",
        string = "#067D17",
        variable = "#871094",
        method = "#FF9B00"
    )

    private val defaultDark = Theme(
        keyword = "#2274cf",
        string = "#069c1b",
        variable = "#a65ab3",
        method = "#db8800",
        normal = "#f6fff3",
        comment = "#727272",
    )

    @Deprecated("Deprecated\nUse CodeTheme.default(context) instead.")
    val kotlinLight = defaultLight

    fun default(context: Context) =
        if (context.isNightMode()) defaultDark else defaultLight
}