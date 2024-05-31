package com.naulian.glow_compose.mdx

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.naulian.glow_compose.Preview
import com.naulian.glow_compose.R
import com.naulian.glow_core.mdx.MdxToken
import com.naulian.glow_core.mdx.MdxType

@Composable
fun MdxImageBlock(modifier: Modifier = Modifier, token: MdxToken) {
    var isError by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        isError = false
    }

    if (!isError) {
        AsyncImage(
            model = token.text,
            modifier = modifier
                .fillMaxWidth()
                .clip(MaterialTheme.shapes.small),
            contentDescription = "Atx Picture",
            contentScale = ContentScale.FillWidth,
            placeholder = painterResource(R.drawable.img_placeholder),
            onError = { isError = true },
        )
    }
}

@Preview
@Composable
private fun AtxImageBlockPreview() {
    Preview {
        val token = MdxToken(
            type = MdxType.IMAGE,
            text = "https://picsum.photos/id/67/300/200",
        )
        MdxImageBlock(
            modifier = Modifier.padding(16.dp),
            token = token
        )
    }
}