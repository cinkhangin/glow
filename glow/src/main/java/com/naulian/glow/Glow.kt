package com.naulian.glow

import android.text.Spannable
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import androidx.core.text.HtmlCompat
import java.util.regex.Matcher
import java.util.regex.Pattern

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
    fun highlight(source: String, theme: Theme = Theme()): Colored {
        //order matter
        val output = source.italic(Rex.lists)
            //color punctuations
            .color(Rex.punctuations, theme.keyword)
            .color2(Rex.variables, theme.variable)
            .color(Rex.keywords, theme.keyword)
            .color(Rex.strings, theme.string)
            .color(Rex.numbers, theme.number)
            //.replace(Rex.properties, Color.property)
            //.color(Rex.methods, Color.method)
            .color(Rex.comments, theme.comment)
            //resolve /* for multiline comment
            .replace("/<font color=${theme.keyword}>*</font>", "/*")
            .replace("<font color=${theme.keyword}>*</font>/", "*/")
            //highlight multiline comment
            .color(Rex.documentations, theme.comment)
            .replace("  ", "&nbsp;&nbsp;")
            .replace("\n", "<br>")

        val spanned = HtmlCompat.fromHtml(output, HtmlCompat.FROM_HTML_MODE_COMPACT)
        return Colored(spanned, output)
    }
}
