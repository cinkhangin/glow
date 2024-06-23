package com.naulian.glow_compose.mdx

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.naulian.glow_core.mdx.MDX_TEST
import com.naulian.glow_core.mdx.MdxComponentGroup
import com.naulian.glow_core.mdx.MdxComponentType
import com.naulian.glow_core.mdx.MdxNode
import com.naulian.glow_core.mdx.MdxParser

@Composable
fun MdxBlock(
    modifier: Modifier = Modifier,
    source: String,
    onClickLink: (String) -> Unit = {},
    components: MdxComponents = mdxComponents(),
    contentSpacing: Dp = 12.dp
) {
    var nodes by remember {
        mutableStateOf(emptyList<MdxComponentGroup>())
    }

    LaunchedEffect(key1 = source) {
        nodes = MdxParser.parse(source)
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(contentSpacing)
    ) {
        nodes.forEach {
            when (it.type) {
                MdxComponentType.TEXT -> TextComponent(
                    tokens = it.children,
                    components = components,
                    onClickLink = onClickLink
                )

                MdxComponentType.OTHER -> OtherComponent(
                    tokens = it.children,
                    components = components
                )

                MdxComponentType.ELEMENT -> components.elements(it.children)
            }
        }
    }
}

data class MdxComponents(
    val fontFamily: FontFamily,
    val text: @Composable (AnnotatedString, linkMap: Map<String, String>, onClickLink: (String) -> Unit) -> Unit,
    val header: @Composable (MdxNode) -> Unit,
    val quote: @Composable (MdxNode) -> Unit,
    val codeBlock: @Composable (MdxNode) -> Unit,
    val image: @Composable (MdxNode) -> Unit,
    val youtube: @Composable (MdxNode) -> Unit,
    val video: @Composable (MdxNode) -> Unit,
    val divider: @Composable (MdxNode) -> Unit,
    val table: @Composable (MdxNode) -> Unit,
    val elements: @Composable (List<MdxNode>) -> Unit,
)

fun mdxComponents(
    fontFamily: FontFamily = FontFamily.Default,
    text: @Composable (
        content: AnnotatedString,
        linkMap: Map<String, String>,
        onClickLink: (String) -> Unit
    ) -> Unit = { content, linkMap, onClickLink ->
        ClickableText(
            text = content,
            style = TextStyle(fontFamily = fontFamily)
        ) { offset ->
            content.getStringAnnotations(start = offset, end = offset)
                .firstOrNull()?.let { linkMap[it.tag]?.let(onClickLink) }
        }
    },
    header: @Composable (MdxNode) -> Unit = { HeaderBlock(token = it, fontFamily = fontFamily) },
    quote: @Composable (MdxNode) -> Unit = {
        QuoteBlock(
            quote = it.literal,
            fontFamily = fontFamily
        )
    },
    codeBlock: @Composable (MdxNode) -> Unit = { MdxCodeBlock(token = it) },
    image: @Composable (MdxNode) -> Unit = { MdxImageBlock(token = it) },
    youtube: @Composable (MdxNode) -> Unit = { Text(text = it.literal) },
    video: @Composable (MdxNode) -> Unit = { Text(text = it.literal) },
    divider: @Composable (MdxNode) -> Unit = { MdxDivider(token = it) },
    table: @Composable (MdxNode) -> Unit = { MdxTable(token = it, fontFamily = fontFamily) },
    elements: @Composable (List<MdxNode>) -> Unit = {
        MdxElement(
            tokens = it,
            fontFamily = fontFamily
        )
    }
) = MdxComponents(
    fontFamily = fontFamily,
    text = text,
    header = header,
    quote = quote,
    codeBlock = codeBlock,
    image = image,
    youtube = youtube,
    video = video,
    divider = divider,
    table = table,
    elements = elements
)


@Preview(heightDp = 1500)
@Composable
private fun MdxBlockPreview() {
    MaterialTheme {
        Surface(color = Color.LightGray) {
            MdxBlock(modifier = Modifier.padding(16.dp), source = MDX_TEST)
        }
    }
}