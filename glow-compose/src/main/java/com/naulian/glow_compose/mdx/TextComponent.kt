package com.naulian.glow_compose.mdx

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.naulian.glow_compose.Preview
import com.naulian.glow_compose.hexToColor
import com.naulian.glow_core.mdx.MdxToken
import com.naulian.glow_core.mdx.MdxType

fun AnnotatedString.Builder.appendWithStyle(text: String, style: SpanStyle) {
    withStyle(style = style) {
        append(text)
    }
}

@Composable
fun TextComponent(
    tokens: List<MdxToken>,
    components: MdxComponents,
    onClickLink: (String) -> Unit
) {
    if (tokens.size == 1 && tokens.first().type == MdxType.WHITESPACE) {
        return
    }

    val linkMap = hashMapOf<String, String>()
    var linkIndex = 0

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

                MdxType.ELEMENT -> {
                    when {
                        token.text.startsWith("o ") -> {
                            val text = token.text.removePrefix("o ")
                            append("\u2610 $text")
                        }

                        token.text.startsWith("x ") -> {
                            val text = token.text.removePrefix("x ")
                            append("\u2611 $text")
                        }

                        else -> append("\u25CF ${token.text}")
                    }
                }

                MdxType.LINK -> {
                    val tag = "link$linkIndex"
                    pushStringAnnotation(tag, "link")
                    appendWithStyle(
                        token.text,
                        style = SpanStyle(color = Color.Blue)
                    )
                    linkMap[tag] = token.text
                    linkIndex++
                    pop()
                }

                MdxType.HYPER_LINK -> {
                    val tag = "link$linkIndex"
                    val (hyper, link) = token.getHyperLink()
                    pushStringAnnotation(tag, "link")
                    appendWithStyle(
                        hyper.ifEmpty { link },
                        style = SpanStyle(color = Color.Blue)
                    )
                    linkMap[tag] = link
                    linkIndex++
                    pop()
                }

                MdxType.COLORED -> {
                    val (text, hexColor) = token.getTextColorPair()
                    val color = hexColor.hexToColor()
                    appendWithStyle(
                        text,
                        style = SpanStyle(color = color)
                    )
                }

                else -> append(token.text)
            }
        }
    }

    components.text(content, linkMap, onClickLink)
}

@Preview
@Composable
private fun MdxTextTestPreview() {
    MdxBlock(
        source = """
        hello
        
        hello
    """.trimIndent()
    )
}

@Preview
@Composable
private fun NormalTextTestPreview() {
    Text(
        modifier = Modifier.fillMaxWidth(),
        text = """
        hello
        
        hello
    """.trimIndent()
    )
}

@Preview
@Composable
private fun TextPreview() {
    Preview {
        Column {
            Text(text = "Hello", fontSize = 16.sp)
            Spacer(modifier = Modifier.height(20.dp))
            Text(text = "Hello")
            Text(text = "Hello\n\nHello", lineHeight = 24.sp)
        }
    }
}