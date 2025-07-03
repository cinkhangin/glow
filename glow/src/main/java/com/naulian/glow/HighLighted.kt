@file:Suppress("unused")

package com.naulian.glow

import android.text.Html
import android.text.Spanned
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.fromHtml
import androidx.core.text.HtmlCompat

data class HighLighted(
    private val html: String,
) {
    val spanned: Spanned get() = Html.fromHtml(html, HtmlCompat.FROM_HTML_MODE_COMPACT)
    val annotatedString get() = AnnotatedString.fromHtml(html)
}
