package com.naulian.glow_compose.atx

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.naulian.glow_compose.Preview
import com.naulian.glow_core.atx.AtxToken
import com.naulian.glow_core.atx.AtxType
import com.naulian.glow_core.atx.SAMPLE_KT

@Composable
fun AtxCodeBlock(modifier: Modifier = Modifier, token: AtxToken) {
    val language = token.argument.ifEmpty { "txt" }
    when (language) {
        "comment" -> {}
        else -> CodeSnippet(
            modifier = modifier
                .fillMaxWidth()
                .background(
                    color = MaterialTheme.colorScheme.surface,
                    shape = MaterialTheme.shapes.small
                )
                .clip(MaterialTheme.shapes.small),
            source = token.value,
            language = language
        )
    }
}

@Preview
@Composable
private fun AtxCodeBlockPreview() {
    Preview {
        val token = AtxToken(
            type = AtxType.FUN,
            value = SAMPLE_KT,
            argument = "kotlin"
        )
        AtxCodeBlock(
            modifier = Modifier.padding(16.dp),
            token = token
        )
    }
}