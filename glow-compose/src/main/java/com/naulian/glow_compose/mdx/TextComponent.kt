package com.naulian.glow_compose.mdx

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import com.naulian.glow_core.mdx.MdxToken
import com.naulian.glow_core.mdx.MdxType
import com.naulian.glow_core.mdx.mdxAdhocMap

fun AnnotatedString.Builder.appendWithStyle(text: String, style: SpanStyle) {
    withStyle(style = style) {
        append(text)
    }
}

@Composable
fun TextComponent(tokens: List<MdxToken>) {
    if (tokens.size == 1 && tokens.first().type == MdxType.WHITESPACE) {
        return
    }

    val content = buildAnnotatedString {
        tokens.forEach { token ->
            when (token.type) {
                MdxType.BOLD -> appendWithStyle(
                    token.text,
                    style = SpanStyle(fontWeight = FontWeight.Bold)
                )

                MdxType.ITALIC -> appendWithStyle(
                    token.text,
                    style = SpanStyle(fontStyle = FontStyle.Italic)
                )

                MdxType.UNDERLINE -> appendWithStyle(
                    token.text,
                    style = SpanStyle(textDecoration = TextDecoration.Underline)
                )

                MdxType.STRIKE -> appendWithStyle(
                    token.text,
                    style = SpanStyle(textDecoration = TextDecoration.LineThrough)
                )

                MdxType.TEXT -> append(token.text)
                MdxType.ELEMENT -> append(token.text)
                MdxType.WHITESPACE -> append(token.text)
                MdxType.ADHOC -> {
                    val content = mdxAdhocMap[token.text] ?: ""
                    append(content)
                }

                else -> append("")
            }
        }
    }

    Text(text = content)
}