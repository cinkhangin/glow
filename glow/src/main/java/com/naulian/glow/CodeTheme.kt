@file:Suppress("unused")

package com.naulian.glow

import android.content.Context
import com.naulian.anhance.isNightMode

@Suppress("MemberVisibilityCanBePrivate")
object CodeTheme {

    val defaultLight = Theme(
        background = "#FFFFFF",
        normal = "#000000",
        string = "#067D17",
        comment = "#AEAEAE",
        method = "#FF9B00",
        number = "#0000FF",
        keyword = "#0033B3",
        variable = "#000000",
        property = "#871094"
    )

    val defaultDark = Theme()

    fun default(context: Context) =
        if (context.isNightMode()) defaultDark else defaultLight
}