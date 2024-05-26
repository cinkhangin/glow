package com.naulian.glow_compose.atx

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
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
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.naulian.anhance.copyString
import com.naulian.anhance.logDebug
import com.naulian.glow.CodeTheme
import com.naulian.glow.Theme
import com.naulian.glow_compose.Glow
import com.naulian.glow_compose.font
import com.naulian.glow_core.atx.AtxKind
import com.naulian.glow_core.atx.AtxNode
import com.naulian.glow_core.atx.AtxParser
import com.naulian.glow_core.atx.AtxToken
import com.naulian.glow_core.atx.AtxType
import com.naulian.glow_core.atx.SAMPLE
import android.graphics.Color as LegacyColor

@Composable
fun AtxBlock(modifier: Modifier = Modifier, source: String) {
    var nodes by remember {
        mutableStateOf(emptyList<AtxNode>())
    }

    LaunchedEffect(key1 = Unit) {
        nodes = AtxParser(source).parse()
    }

    Column(modifier = modifier.fillMaxWidth()) {
        nodes.forEach {
            when (it.kind) {
                AtxKind.TEXT -> TextComponent(it)
                AtxKind.LINK -> LinkComponent(it)
                AtxKind.OTHER -> OtherComponent(it)
            }
        }
    }
}

fun AnnotatedString.Builder.appendWithStyle(text: String, style: SpanStyle) {
    withStyle(style = style) {
        append(text)
    }
}

@Composable
fun TextComponent(node: AtxNode) {
    if (node.children.size == 1 && node.children[0].text == "\n") {
        return
    }

    val content = buildAnnotatedString {
        node.children.trim().forEach {
            when (it.type) {
                AtxType.BOLD -> appendWithStyle(
                    it.text,
                    style = SpanStyle(fontWeight = FontWeight.Bold)
                )

                AtxType.ITALIC -> appendWithStyle(
                    it.text,
                    style = SpanStyle(fontStyle = FontStyle.Italic)
                )

                AtxType.BOLD_ITALIC -> appendWithStyle(
                    it.text,
                    style = SpanStyle(fontWeight = FontWeight.Bold, fontStyle = FontStyle.Italic)
                )

                AtxType.UNDERLINE -> appendWithStyle(
                    it.text,
                    style = SpanStyle(textDecoration = TextDecoration.Underline)
                )

                AtxType.STRIKE -> appendWithStyle(
                    it.text,
                    style = SpanStyle(textDecoration = TextDecoration.LineThrough)
                )

                AtxType.CODE_BLOCK -> {
                    appendWithStyle(
                        it.text,
                        style = SpanStyle(background = Color.Gray)
                    )
                }

                AtxType.TEXT -> append(it.text)
                AtxType.COLORED -> {
                    val color = it.text.split(" ").firstOrNull() ?: ""
                    if (color.contains("#")) {
                        val hexColor = color.trim()
                        val intColor = LegacyColor.parseColor(hexColor)
                        val composeColor = Color(intColor)
                        appendWithStyle(
                            it.text.replace(color, ""),
                            style = SpanStyle(color = composeColor)
                        )
                    } else appendWithStyle(
                        it.text,
                        style = SpanStyle(color = Color.Green)
                    )
                }

                AtxType.JOIN -> append(it.text)
                AtxType.NEWLINE -> append(it.text)
                else -> append("")
            }
        }
    }

    Text(text = content)
}


@Composable
fun LinkComponent(node: AtxNode) {
    val content = buildAnnotatedString {
        append(node.children.joinToString(""))
    }

    Text(text = content)
}

fun List<AtxToken>.trim(): List<AtxToken> {
    var result = toMutableList()
    if (isNotEmpty()) {
        if (first().type == AtxType.NEWLINE) {
            result = result.drop(1).toMutableList()
        }
    }

    if (result.isNotEmpty()) {
        if (last().type == AtxType.NEWLINE) {
            result = result.dropLast(1).toMutableList()
        }
    }

    return result.toList()
}

val atxSupportedLang = listOf(
    "java",
    "kotlin", "kt",
    "javascript", "js",
    "python", "py",
    "text", "txt"
)

@Composable
fun OtherComponent(node: AtxNode) {
    node.children.trim().forEach {
        when (it.type) {
            AtxType.HEADER -> {
                Text(
                    text = it.text,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 36.sp
                )
            }

            AtxType.SUB_HEADER -> {
                Text(
                    text = it.text,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 28.sp
                )
            }

            AtxType.TITLE -> {
                Text(
                    text = it.text,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 24.sp
                )
            }

            AtxType.SUB_TITLE -> {
                Text(
                    text = it.text,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 20.sp
                )
            }

            AtxType.QUOTE -> {
                QuoteBlock(quote = it.text)
            }

            AtxType.CODE -> {
                val language = it.text.split("\n").firstOrNull() ?: ""
                when (language) {
                    "comment" -> {}
                    in atxSupportedLang -> CodeBlock(source = it.text, language = language)
                    else -> CodeBlock(source = it.text, language = "txt")
                }
            }

            AtxType.PICTURE -> {}
            AtxType.VIDEO -> {}

            AtxType.LIST -> {}
            AtxType.TABLE -> {}
            AtxType.ROW -> {}
            AtxType.ELEMENT -> {}
            AtxType.ORDERED_ELEMENT -> {}
            AtxType.DIVIDER -> {}

            AtxType.CONSTANT -> {}
            AtxType.END -> {}
            else -> {}
        }
    }
}


@Composable
fun CodeBlock(
    modifier: Modifier = Modifier,
    source: String,
    language: String = "txt",
    codeName: String = "",
    codeTheme: Theme = CodeTheme.defaultLight,
) {
    var glowCode by remember {
        mutableStateOf(buildAnnotatedString { })
    }

    val context = LocalContext.current
    LaunchedEffect(key1 = Unit) {
        logDebug(source)
        glowCode = Glow.highlight(source, language, codeTheme).value
    }

    Card(
        modifier = modifier
            .fillMaxWidth(),
        shape = MaterialTheme.shapes.small,
    ) {
        ConstraintLayout(modifier = Modifier.fillMaxWidth()) {
            val (name, action, code) = createRefs()

            Box(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.tertiary)
                    .constrainAs(name) {
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        width = Dimension.fillToConstraints
                    },
                contentAlignment = Alignment.CenterStart
            ) {
                Text(
                    modifier = Modifier.padding(12.dp),
                    text = codeName.ifEmpty { language },
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                )
            }

            IconButton(
                onClick = { context.copyString(source) },
                modifier = Modifier.constrainAs(action) {
                    end.linkTo(parent.end)
                    top.linkTo(name.top)
                    bottom.linkTo(name.bottom)
                }
            ) {
                Icon(
                    imageVector = Icons.Rounded.Add,
                    contentDescription = null
                )
            }

            LazyRow(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.surface)
                    .constrainAs(code) {
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        top.linkTo(name.bottom)
                        bottom.linkTo(parent.bottom)
                        width = Dimension.fillToConstraints
                    }
            ) {
                item {
                    Text(
                        text = glowCode,
                        fontFamily = font,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(12.dp),
                    )
                }
            }
        }
    }
}

@Composable
fun QuoteBlock(modifier: Modifier = Modifier, quote: String) {
    ConstraintLayout(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.surface,
                shape = MaterialTheme.shapes.small
            )
            .clip(MaterialTheme.shapes.small)
    ) {
        val (accent, text) = createRefs()
        Box(
            modifier = Modifier
                .width(8.dp)
                .background(MaterialTheme.colorScheme.primary)
                .constrainAs(accent) {
                    start.linkTo(parent.start)
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                    height = Dimension.fillToConstraints
                }
        )
        Box(
            modifier = Modifier
                .constrainAs(text) {
                    start.linkTo(accent.end)
                    end.linkTo(parent.end)
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                    width = Dimension.fillToConstraints
                }
        ) {
            Text(
                text = quote,
                modifier = Modifier.padding(12.dp),
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Preview
@Composable
private fun AtxBlockPreview() {
    MaterialTheme {
        Surface {
            AtxBlock(source = SAMPLE)
        }
    }
}