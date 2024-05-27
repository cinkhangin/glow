package com.naulian.glow_compose.atx

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import coil.compose.AsyncImage
import com.naulian.anhance.copyString
import com.naulian.glow.CodeTheme
import com.naulian.glow.Theme
import com.naulian.glow_compose.Glow
import com.naulian.glow_compose.font
import com.naulian.glow_core.atx.AtxContentType
import com.naulian.glow_core.atx.AtxGroup
import com.naulian.glow_core.atx.AtxParser
import com.naulian.glow_core.atx.AtxToken
import com.naulian.glow_core.atx.AtxType
import com.naulian.glow_core.atx.SAMPLE_ATX
import com.naulian.modify.table.Table
import com.naulian.modify.table.TableHeader
import com.naulian.modify.table.TableItems
import android.graphics.Color as LegacyColor

@Composable
fun AtxBlock(modifier: Modifier = Modifier, source: String) {
    var nodes by remember {
        mutableStateOf(emptyList<AtxGroup>())
    }

    LaunchedEffect(key1 = Unit) {
        nodes = AtxParser(source).parse()
    }

    if (nodes.isNotEmpty()) {
        Column(
            modifier = modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            nodes.forEach {
                when (it.type) {
                    AtxContentType.TEXT -> TextComponent(tokens = it.children)
                    AtxContentType.OTHER -> OtherComponent(tokens = it.children)
                    AtxContentType.TABLE -> TableComponent(tokens = it.children)
                    AtxContentType.LINK -> LinkComponent(tokens = it.children)
                }
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
fun TextComponent(tokens: List<AtxToken>) {
    val content = buildAnnotatedString {
        tokens.forEach {
            when (it.type) {
                AtxType.BOLD -> appendWithStyle(
                    it.value,
                    style = SpanStyle(fontWeight = FontWeight.Bold)
                )

                AtxType.ITALIC -> appendWithStyle(
                    it.value,
                    style = SpanStyle(fontStyle = FontStyle.Italic)
                )

                AtxType.UNDERLINE -> appendWithStyle(
                    it.value,
                    style = SpanStyle(textDecoration = TextDecoration.Underline)
                )

                AtxType.STRIKE -> appendWithStyle(
                    it.value,
                    style = SpanStyle(textDecoration = TextDecoration.LineThrough)
                )

                AtxType.TEXT -> append(it.value)
                AtxType.COLORED -> {
                    val color = it.argument
                    if (color.contains("#")) {
                        val hexColor = color.trim()
                        val intColor = LegacyColor.parseColor(hexColor)
                        val composeColor = Color(intColor)
                        appendWithStyle(
                            it.value,
                            style = SpanStyle(color = composeColor)
                        )
                    } else appendWithStyle(
                        it.value,
                        style = SpanStyle(color = Color.Green)
                    )
                }

                AtxType.NEWLINE -> append(it.value)
                else -> append("")
            }
        }
    }

    Text(text = content)
}


@Composable
fun LinkComponent(tokens: List<AtxToken>) {
    tokens.forEach {
        Text(text = it.argument.ifEmpty { it.value }, color = Color.Blue)
    }
}

val a2z = ('a'..'z').toList()
val A2Z = ('A'..'Z').toList()

@Composable
fun OtherComponent(tokens: List<AtxToken>) {
    tokens.forEach { token ->
        when (token.type) {
            AtxType.HEADER -> {
                val sizePair = when (token.argument) {
                    "1" -> 32.sp to 42.sp
                    "2" -> 28.sp to 36.sp
                    "3" -> 24.sp to 32.sp
                    "4" -> 20.sp to 28.sp
                    "5" -> 18.sp to 24.sp
                    "6" -> 16.sp to 22.sp
                    else -> 24.sp to 32.sp
                }
                Text(
                    text = token.value,
                    fontSize = sizePair.first,
                    fontWeight = FontWeight.Bold,
                    lineHeight = sizePair.second
                )
            }

            AtxType.QUOTE -> {
                val content = """
                    |${token.value}
                    |- ${token.argument.ifEmpty { "unknown" }}
                """.trimMargin()
                QuoteBlock(quote = content)
            }

            AtxType.FUN -> {
                val language = token.argument.ifEmpty { "txt" }
                when (language) {
                    "comment" -> {}
                    else -> CodeBlock(source = token.value, language = language)
                }
            }

            AtxType.PICTURE -> {
                AsyncImage(
                    model = token.value,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(MaterialTheme.shapes.small),
                    contentDescription = "Picture",
                    contentScale = ContentScale.FillWidth,
                )
            }

            AtxType.VIDEO -> {}

            AtxType.ELEMENT -> {
                val elements = token.value.split("\n")
                elements.forEachIndexed { i, s ->
                    when (token.argument) {
                        in "0".."9" -> {
                            val index = i + 1
                            Text(text = "$index. $s")
                        }

                        in "a".."z" -> {
                            val index = i % 26
                            val letter = a2z[index]
                            Text(text = "$letter. $s")
                        }

                        in "A".."Z" -> {
                            val index = i % 26
                            val letter = A2Z[index]
                            Text(text = "$letter. $s")
                        }

                        "" -> Text(text = "- $s")
                        else -> Text(text = "${token.argument} $s")
                    }
                }
            }

            AtxType.DIVIDER -> {
                if (token.value == "") {
                    HorizontalDivider()
                } else Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = token.value,
                    textAlign = TextAlign.Center
                )
            }

            else -> {}
        }
    }
}

@Composable
fun TableComponent(tokens: List<AtxToken>) {
    tokens.forEach { token ->
        val columns = token.argument.split(",").map { it.trim() }
        val cellCol = if (columns.isEmpty()) 1 else columns.size
        val rows = token.value.split(",")
            .map { it.trim() }.chunked(cellCol)

        Table(
            header = {
                if (columns.isNotEmpty()) {
                    if (columns.size == 1) {
                        TableHeader(title = columns.first())
                    } else TableHeader(items = columns)
                }
            },
            content = {
                if (rows.isNotEmpty()) {
                    TableItems(items = rows)
                }
            }
        )
    }
}

fun log(vararg input: Any) {
    val message = input.joinToString { it.toString() }
    println(message)
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
        Surface(color = Color.LightGray) {
            AtxBlock(modifier = Modifier.padding(16.dp), source = SAMPLE_ATX)
        }
    }
}