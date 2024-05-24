package com.naulian.glow_compose.atx

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.core.text.isDigitsOnly
import com.naulian.anhance.copyString
import com.naulian.anhance.logDebug
import com.naulian.glow.CodeTheme
import com.naulian.glow.Theme
import com.naulian.glow_compose.Glow
import com.naulian.glow_compose.font
import com.naulian.glow_core.atx.AtxLexer
import com.naulian.glow_core.atx.AtxType
import com.naulian.glow_core.atx.SAMPLE

sealed interface AtxData {
    data object EOF : AtxData
    data class Image(val url: String) : AtxData
    data class Video(val url: String) : AtxData
    data class Divider(val size: Dp) : AtxData
    data class Head(val text: String, val size: TextUnit) : AtxData
    data class Text(val text: String, val style: TextStyle) : AtxData
    data class Link(val url: String, val hyper: String) : AtxData
    data class Code(val source: String, val language: String) : AtxData
    data class Quote(val text: String, val author: String) : AtxData
    data class Space(val size: Dp, val isVertical: Boolean) : AtxData
    data class Table(val columns: List<String>, val items: List<List<String>>) : AtxData
    data class ListItem(
        val items: List<String>,
        val isNumbered: Boolean,
        val bullet: String = "-"
    ) : AtxData
}

object AtxParser {
    fun parse(source: String): List<AtxData> {
        val tokens = AtxLexer(source).tokenize()
        val atxData = mutableListOf<AtxData>()

        tokens.forEach {
            val data: AtxData = when (it.type) {
                AtxType.HEAD -> {
                    val size = if (it.param.isDigitsOnly()) it.param.toInt() else 14
                    AtxData.Head(it.value, size.sp)
                }

                AtxType.TEXT -> {
                    val style = when (it.param) {
                        "bold" -> TextStyle(fontWeight = FontWeight.Bold)
                        "italic" -> TextStyle(fontWeight = FontWeight.Light)
                        "underline" -> TextStyle(textDecoration = TextDecoration.Underline)
                        "strikethrough" -> TextStyle(textDecoration = TextDecoration.LineThrough)
                        else -> TextStyle()
                    }
                    AtxData.Text(it.value, style)
                }

                AtxType.QUOTE -> {
                    AtxData.Quote(it.value, it.param)
                }

                AtxType.CODE -> {
                    AtxData.Code(it.value, it.param)
                }

                AtxType.LINK -> {
                    AtxData.Link(it.value, it.param)
                }

                AtxType.SPACE -> {
                    val size = if (it.value.isDigitsOnly()) it.value.toInt() else 12
                    AtxData.Space(size.dp, it.param == "vertical")
                }

                AtxType.IMAGE -> {
                    AtxData.Image(it.value)
                }

                AtxType.VIDEO -> {
                    AtxData.Video(it.value)
                }

                AtxType.TABLE -> {
                    val columns = it.value.split(",")
                    val items = it.param.split(",")
                        .chunked(columns.size)
                    AtxData.Table(columns, items)
                }

                AtxType.LIST -> {
                    val items = it.value.split(",")
                    AtxData.ListItem(items, it.param == "number")
                }

                AtxType.DIVIDER -> {
                    val size = if (it.value.isDigitsOnly()) it.value.toInt() else 1
                    AtxData.Divider(size.dp)
                }

                AtxType.EOF -> AtxData.EOF
            }

            atxData.add(data)
        }

        return atxData
    }
}

@Composable
fun AtxBlock(modifier: Modifier = Modifier, source: String) {
    var data by remember {
        mutableStateOf(emptyList<AtxData>())
    }

    LaunchedEffect(key1 = Unit) {
        data = AtxParser.parse(source)
    }

    Column(modifier = modifier.fillMaxWidth()) {
        data.forEach {
            when (it) {
                is AtxData.Code -> CodeBlock(source = it.source, language = it.language)


                is AtxData.Divider -> {
                    HorizontalDivider(thickness = it.size)
                }

                AtxData.EOF -> {}
                is AtxData.Head -> {
                    val style = when (it.size) {
                        1.sp -> MaterialTheme.typography.headlineLarge.copy(fontSize = 32.sp)
                        2.sp -> MaterialTheme.typography.headlineMedium.copy(fontSize = 24.sp)
                        3.sp -> MaterialTheme.typography.headlineSmall.copy(fontSize = 20.sp)
                        4.sp -> MaterialTheme.typography.titleLarge.copy(fontSize = 16.sp)
                        else -> MaterialTheme.typography.headlineSmall.copy(fontSize = it.size)
                    }
                    Text(text = it.text, style = style)
                }

                is AtxData.Image -> {
                    Text(text = it.url)
                }

                is AtxData.Link -> {
                    Text(text = it.url)
                }

                is AtxData.ListItem -> {
                    Text(text = it.items.joinToString())
                }

                is AtxData.Quote -> QuoteBlock(quote = it.text, author = it.author)

                is AtxData.Space -> {
                    if (it.isVertical) Spacer(modifier = Modifier.height(it.size))
                    else Spacer(modifier = Modifier.width(it.size))
                }

                is AtxData.Table -> {
                    Text(text = it.columns.joinToString())
                    Text(text = it.items.joinToString())
                }

                is AtxData.Text -> {
                    Text(text = it.text, style = it.style)
                }

                is AtxData.Video -> {
                    Text(text = it.url)
                }
            }
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
fun QuoteBlock(modifier: Modifier = Modifier, quote: String, author: String = "") {
    val content by remember {
        mutableStateOf(
            buildAnnotatedString {
                append(quote)
                if (author.isNotEmpty()) {
                    append("\n")
                    append("- $author")
                }
            }
        )
    }
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
                    width = Dimension.fillToConstraints
                }
        ) {
            Text(
                text = content,
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