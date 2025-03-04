@file:Suppress("unused")

package com.naulian.glow

@Suppress("MemberVisibilityCanBePrivate")
object CodeTheme {

    val defaultDark = Theme()
    val defaultLight = Theme(
        background = "#F2F3F4",
        surface = "#FFFFFF",
        normal = "#000000",
        string = "#067D17",
        comment = "#AEAEAE",
        method = "#FF9B00",
        number = "#0000FF",
        keyword = "#0033B3",
        variable = "#000000",
        property = "#871094"
    )

    fun default() = defaultLight
}

val RosePine = Theme(
    background = "#191724",
    surface = "#1f1d2e",
    normal = "#e0def4",
    comment = "#6e6a86",
    variable = "#eb6f92",
    string = "#9ccfd8",
    method = "#ebbcba",
    keyword = "#31748f",
    property = "#f6c177",
    number = "#c4a7e7"
)

val RosePineMoon = Theme(
    background = "#232136",
    surface = "#2a273f",
    normal = "#e0def4",
    comment = "#6e6a86",
    variable = "#eb6f92",
    string = "#9ccfd8",
    method = "#ea9a97",
    keyword = "#3e8fb0",
    property = "#f6c177",
    number = "#c4a7e7"
)


val RosePineDawn = Theme(
    background = "#faf4ed",
    surface = "#fffaf3",
    normal = "#575279",
    comment = "#9893a5",
    variable = "#b4637a",
    string = "#56949f",
    method = "#d7827e",
    keyword = "#286983",
    property = "#ea9d34",
    number = "#907aa9"
)

