package com.naulian.glow_compose.atx

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.naulian.glow_compose.Preview
import com.naulian.glow_compose.R
import com.naulian.glow_core.atx.AtxToken
import com.naulian.glow_core.atx.AtxType

@Composable
fun AtxImageBlock(modifier: Modifier = Modifier, token: AtxToken) {
    AsyncImage(
        model = token.value,
        modifier = modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.small),
        contentDescription = "Picture",
        contentScale = ContentScale.FillWidth,
        placeholder = painterResource(id = R.drawable.img_placeholder)
    )
}

@Preview
@Composable
private fun AtxImageBlockPreview() {
    Preview {
        val token = AtxToken(
            type = AtxType.PICTURE,
            value = "https://picsum.photos/id/67/300/200",
            argument = ""
        )
        AtxImageBlock(
            modifier = Modifier.padding(16.dp),
            token = token
        )
    }
}