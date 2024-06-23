package com.naulian.glow_compose.mdx

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.naulian.glow_compose.Preview
import com.naulian.glow_core.mdx.MdxNode
import com.naulian.glow_core.mdx.MdxType

@Composable
fun MdxImageBlock(modifier: Modifier = Modifier, token: MdxNode) {

}

@Preview
@Composable
private fun AtxImageBlockPreview() {
    Preview {
        val token = MdxNode(
            type = MdxType.IMAGE,
            literal = "https://picsum.photos/id/67/300/200",
        )
        MdxImageBlock(
            modifier = Modifier.padding(16.dp),
            token = token
        )
    }
}