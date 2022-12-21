package com.naulian.glow

import android.text.Spanned
import androidx.core.text.HtmlCompat

private fun String.color(color: String) =
    "<font color=$color>$this</font>"

private fun String.replace(rex: Regex, color: String) =
    replace(rex) { it.value.color(color) }


class Glow {
    fun highlight(source: String): Spanned {
        val output = source.replace(Rex.keywords , Color.keyword)
            .replace(Rex.strings , Color.string)
            .replace(Rex.numbers , Color.number)
            //.replace(Rex.properties, Color.property)
            .replace(Rex.methods , Color.method)
            .replace(Rex.comments, Color.comment)
            .replace(Rex.documentations , Color.comment)
            .replace("  ", "&nbsp;&nbsp;")
            .replace("\n","<br>")
        return HtmlCompat.fromHtml(output, HtmlCompat.FROM_HTML_MODE_COMPACT)
    }
}
