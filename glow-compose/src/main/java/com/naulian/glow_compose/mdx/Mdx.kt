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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.naulian.glow_core.mdx.MDX_SAMPLE
import com.naulian.glow_core.mdx.MdxComponentGroup
import com.naulian.glow_core.mdx.MdxComponentType
import com.naulian.glow_core.mdx.MdxParser
import com.naulian.glow_core.mdx.MdxToken

@Composable
fun MdxBlock(modifier: Modifier = Modifier, source: String) {
    var nodes by remember {
        mutableStateOf(emptyList<MdxComponentGroup>())
    }

    LaunchedEffect(key1 = Unit) {
        nodes = MdxParser(source).parse()
        println(nodes)
    }

    if (nodes.isNotEmpty()) {
        Column(
            modifier = modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            nodes.forEach {
                when (it.type) {
                    MdxComponentType.TEXT -> TextComponent(tokens = it.children)
                    MdxComponentType.OTHER -> OtherComponent(tokens = it.children)
                    MdxComponentType.TABLE -> TableComponent(tokens = it.children)
                    MdxComponentType.LINK -> LinkComponent(tokens = it.children)
                }
            }
        }
    }
}

@Composable
fun LinkComponent(tokens: List<MdxToken>) {
    tokens.forEach {
        val (hyper, link) = it.getHyperLink()
        Text(text = hyper.ifEmpty { link }, color = Color.Blue)
    }
}

@Preview
@Composable
private fun MdxBlockPreview() {
    MaterialTheme {
        Surface(color = Color.LightGray) {
            MdxBlock(modifier = Modifier.padding(16.dp), source = MDX_SAMPLE)
        }
    }
}