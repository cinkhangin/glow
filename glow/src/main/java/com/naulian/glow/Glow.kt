package com.naulian.glow

import androidx.core.text.HtmlCompat

private fun color(hex: String): Int {
    return android.graphics.Color.parseColor(hex)
}

private fun String.color(color: String) =
    "<font color=$color>$this</font>"

private fun String.italic() = "<i>$this</i>"
private fun String.bold() = "<b>$this</b>"

private fun String.italic(rex: Regex) =
    replace(rex) { it.value.italic() }

private fun String.bold(rex: Regex) =
    replace(rex) { it.value.bold() }

private fun String.color(rex: Regex, color: String) =
    replace(rex) { it.value.color(color) }

private fun String.color2(rex: Regex, color: String) =
    replace(rex) { "${it.groups[1]?.value} ${it.groups[2]?.value?.color(color)}" }


object Glow {
    fun highlight(source: String, theme: Theme = Theme()): HighLight {
        return highLightKotlin(source, theme)
    }

    private fun highLightKotlin(source: String, theme: Theme): HighLight {
        //order matter
        val output = source.italic(KotlinRegex.lists)
            //color punctuations
            .color(KotlinRegex.punctuations, theme.keyword)
            .color(KotlinRegex.numbers, theme.number)
            .color2(KotlinRegex.variables, theme.variable)
            .color(KotlinRegex.keywords, theme.keyword)
            .color(KotlinRegex.strings, theme.string)
            .color(KotlinRegex.instanceProperty, theme.instanceProperty)
            //.replace(Rex.properties, Color.property)
            //.color(Rex.methods, Color.method)
            .color(KotlinRegex.comments, theme.comment)
            //resolve /* for multiline comment
            .replace("/<font color=${theme.keyword}>*</font>", "/*")
            .replace("<font color=${theme.keyword}>*</font>/", "*/")
            //highlight multiline comment
            .color(KotlinRegex.documentations, theme.comment)
            .replace("  ", "&nbsp;&nbsp;")
            .replace("\n", "<br>")

        val spanned = HtmlCompat.fromHtml(output, HtmlCompat.FROM_HTML_MODE_COMPACT)
        return HighLight(spanned, output)
    }
}
