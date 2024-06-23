package com.naulian.glow_compose.mdx

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.naulian.glow_compose.Preview
import com.naulian.glow_core.atx.SAMPLE_KT
import com.naulian.glow_core.mdx.MdxNode
import com.naulian.glow_core.mdx.MdxType

@Composable
fun MdxCodeBlock(
    modifier: Modifier = Modifier,
    token: MdxNode,
) {
}

@Preview
@Composable
private fun AtxCodeBlockPreview() {
    Preview {
        val token = MdxNode(
            type = MdxType.CODE,
            literal = ".kt\n$SAMPLE_KT",
        )
        MdxCodeBlock(
            modifier = Modifier.padding(16.dp),
            token = token
        )
    }
}