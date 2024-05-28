package com.naulian.glow_compose.atx

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import com.naulian.glow_core.atx.AtxToken
import com.naulian.glow_core.atx.AtxType

fun AnnotatedString.Builder.appendWithStyle(text: String, style: SpanStyle) {
    withStyle(style = style) {
        append(text)
    }
}

@Composable
fun TextComponent(tokens: List<AtxToken>) {
    val content = buildAnnotatedString {
        tokens.forEach { token ->
            when (token.type) {
                AtxType.BOLD -> appendWithStyle(
                    token.value,
                    style = SpanStyle(fontWeight = FontWeight.Bold)
                )

                AtxType.ITALIC -> appendWithStyle(
                    token.value,
                    style = SpanStyle(fontStyle = FontStyle.Italic)
                )

                AtxType.UNDERLINE -> appendWithStyle(
                    token.value,
                    style = SpanStyle(textDecoration = TextDecoration.Underline)
                )

                AtxType.STRIKE -> appendWithStyle(
                    token.value,
                    style = SpanStyle(textDecoration = TextDecoration.LineThrough)
                )

                AtxType.TEXT -> append(token.value)
                AtxType.COLORED -> {
                    val color = token.argument
                    if (color.contains("#")) {
                        val hexColor = color.trim()
                        val intColor = android.graphics.Color.parseColor(hexColor)
                        val composeColor = Color(intColor)
                        appendWithStyle(
                            token.value,
                            style = SpanStyle(color = composeColor)
                        )
                    } else appendWithStyle(
                        token.value,
                        style = SpanStyle(color = Color.Green)
                    )
                }

                AtxType.NEWLINE -> append(token.value)
                AtxType.ELEMENT -> {
                    token.value.split("\n").forEachIndexed { i, s ->
                        when (token.argument) {
                            in "0".."9" -> {
                                val index = i + 1
                                append("$index. $s\n")
                            }

                            in "a".."z" -> {
                                val index = i % 26
                                val letter = a2z[index]
                                append("$letter. $s\n")
                            }

                            in "A".."Z" -> {
                                val index = i % 26
                                val letter = A2Z[index]
                                append("$letter. $s\n")
                            }

                            "" -> append("- $s\n")
                            else -> append("${token.argument} $s\n")
                        }
                    }
                }

                else -> append("")
            }
        }
    }

    Text(text = content)
}