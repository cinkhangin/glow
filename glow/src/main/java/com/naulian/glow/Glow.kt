package com.naulian.glow

import android.text.Spanned
import androidx.core.text.HtmlCompat

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


class Glow {
    fun highlight(source: String): Spanned {
        //order matter
        val output = source.italic(Rex.lists)
            .color(Rex.punctuations, Color.keyword)
            .color2(Rex.variables, Color.variable)
            .color(Rex.keywords, Color.keyword)
            .color(Rex.strings, Color.string)
            .color(Rex.numbers, Color.number)
            //.replace(Rex.properties, Color.property)
            .color(Rex.methods, Color.method)
            .color(Rex.comments, Color.comment)
            .color(Rex.documentations, Color.comment)
            .replace("  ", "&nbsp;&nbsp;")
            .replace("\n", "<br>")
        return HtmlCompat.fromHtml(output, HtmlCompat.FROM_HTML_MODE_COMPACT)
    }
}
