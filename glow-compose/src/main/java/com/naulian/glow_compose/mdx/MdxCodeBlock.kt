package com.naulian.glow_compose.mdx

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
import com.naulian.glow_core.atx.SAMPLE_KT
import com.naulian.glow_core.mdx.MdxToken
import com.naulian.glow_core.mdx.MdxType

@Composable
fun MdxCodeBlock(
    modifier: Modifier = Modifier,
    token: MdxToken,
) {
    val (lang, code) = token.getLangCodePair()
    when (lang) {
        "comment" -> {}
        else -> CodeSnippet(
            modifier = modifier
                .fillMaxWidth()
                .background(
                    color = MaterialTheme.colorScheme.surface,
                    shape = MaterialTheme.shapes.small
                )
                .clip(MaterialTheme.shapes.small),
            source = code,
            language = lang
        )
    }
}

@Preview
@Composable
private fun AtxCodeBlockPreview() {
    Preview {
        val token = MdxToken(
            type = MdxType.CODE,
            text = ".kt\n$SAMPLE_KT",
        )
        MdxCodeBlock(
            modifier = Modifier.padding(16.dp),
            token = token
        )
    }
}