package com.naulian.glow_compose.mdx

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.naulian.glow_core.mdx.MDX_TEST
import com.naulian.glow_core.mdx.MdxComponentGroup
import com.naulian.glow_core.mdx.MdxComponentType
import com.naulian.glow_core.mdx.MdxParser
import com.naulian.glow_core.mdx.MdxToken

@Composable
fun MdxBlock(
    modifier: Modifier = Modifier,
    source: String,
    components: MdxComponents = mdxComponents()
) {
    var nodes by remember {
        mutableStateOf(emptyList<MdxComponentGroup>())
    }

    LaunchedEffect(key1 = Unit) {
        nodes = MdxParser.parse(source)
    }

    if (nodes.isNotEmpty()) {
        Column(
            modifier = modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            nodes.forEach {
                when (it.type) {
                    MdxComponentType.TEXT -> TextComponent(
                        tokens = it.children,
                        components = components
                    )

                    MdxComponentType.OTHER -> OtherComponent(
                        tokens = it.children,
                        components = components
                    )
                }
            }
        }
    }
}

data class MdxComponents(
    val text: @Composable (AnnotatedString) -> Unit,
    val header: @Composable (MdxToken) -> Unit,
    val quote: @Composable (MdxToken) -> Unit,
    val codeBlock: @Composable (MdxToken) -> Unit,
    val image: @Composable (MdxToken) -> Unit,
    val divider: @Composable (MdxToken) -> Unit,
    val table: @Composable (MdxToken) -> Unit,
)

fun mdxComponents(
    text: @Composable (AnnotatedString) -> Unit = { Text(text = it) },
    header: @Composable (MdxToken) -> Unit = { HeaderBlock(token = it) },
    quote: @Composable (MdxToken) -> Unit = { QuoteBlock(quote = it.text) },
    codeBlock: @Composable (MdxToken) -> Unit = { MdxCodeBlock(token = it) },
    image: @Composable (MdxToken) -> Unit = { MdxImageBlock(token = it) },
    divider: @Composable (MdxToken) -> Unit = { MdxDivider(token = it) },
    table: @Composable (MdxToken) -> Unit = { MdxTable(token = it) },
) = MdxComponents(
    text = text,
    header = header,
    quote = quote,
    codeBlock = codeBlock,
    image = image,
    divider = divider,
    table = table,
)


@Preview(heightDp = 1200)
@Composable
private fun MdxBlockPreview() {
    MaterialTheme {
        Surface(color = Color.LightGray) {
            MdxBlock(modifier = Modifier.padding(16.dp), source = MDX_TEST)
        }
    }
}