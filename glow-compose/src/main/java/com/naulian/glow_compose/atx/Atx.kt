package com.naulian.glow_compose.atx

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
import com.naulian.glow_core.atx.ATX_SAMPLE
import com.naulian.glow_core.atx.AtxContentType
import com.naulian.glow_core.atx.AtxGroup
import com.naulian.glow_core.atx.AtxParser
import com.naulian.glow_core.atx.AtxToken

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

@Composable
fun LinkComponent(tokens: List<AtxToken>) {
    tokens.forEach {
        Text(text = it.argument.ifEmpty { it.value }, color = Color.Blue)
    }
}

@Preview(heightDp = 1200)
@Composable
private fun AtxBlockPreview() {
    MaterialTheme {
        Surface(color = Color.LightGray) {
            AtxBlock(modifier = Modifier.padding(16.dp), source = ATX_SAMPLE)
        }
    }
}