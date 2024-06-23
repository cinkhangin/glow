package com.naulian.glow_compose.mdx

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.naulian.glow_compose.Preview
import com.naulian.glow_core.mdx.MdxNode
import com.naulian.glow_core.mdx.MdxType

@Composable
fun HeaderBlock(
    modifier: Modifier = Modifier,
    token: MdxNode,
    fontFamily: FontFamily = FontFamily.Default
) {

}


@Composable
fun MdxElement(
    modifier: Modifier = Modifier,
    tokens: List<MdxNode>,
    fontFamily: FontFamily = FontFamily.Default
) {

}

@Composable
fun MdxElementText(
    modifier: Modifier = Modifier,
    bullet: String,
    text: String,
    fontFamily: FontFamily
) {
    Row(modifier = modifier.fillMaxWidth()) {
        Text(text = bullet)
        Spacer(modifier = Modifier.width(12.dp))
        Text(text = text, fontFamily = fontFamily)
    }
}

@Preview
@Composable
private fun MdxElementTextPreview() {
    Preview {
        MdxElementText(bullet = "\ud81a\uddf9", text = "hello", fontFamily = FontFamily.Default)
    }
}

@Composable
fun MdxTable(
    modifier: Modifier = Modifier,
    token: MdxNode,
    fontFamily: FontFamily = FontFamily.Default
) {

}

@Composable
fun MdxDivider(token: MdxNode, customMap: Map<String, @Composable () -> Unit> = emptyMap()) {

}

@Preview
@Composable
private fun MdxDividerPreview() {
    Preview {
        Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center) {
            MdxDivider(token = MdxNode(MdxType.DIVIDER, "line"))
            MdxDivider(token = MdxNode(MdxType.DIVIDER, "20"))
            MdxDivider(token = MdxNode(MdxType.DIVIDER, "dash"))
            MdxDivider(
                token = MdxNode(MdxType.DIVIDER, "text"),
                customMap = mapOf("text" to { Text(text = "oooooooooooooo") })
            )
        }
    }
}

@Composable
fun HorizontalDashDivider(
    modifier: Modifier = Modifier,
    thickness: Dp = DividerDefaults.Thickness,
    color: Color = DividerDefaults.color,
    intervals: FloatArray = floatArrayOf(20f, 20f),
    phase: Float = 10f,
) {
    Canvas(
        modifier
            .fillMaxWidth()
            .height(thickness)
    ) {
        drawLine(
            color = color,
            pathEffect = PathEffect.dashPathEffect(intervals, phase),
            strokeWidth = thickness.toPx(),
            start = Offset(0f, thickness.toPx() / 2),
            end = Offset(size.width, thickness.toPx() / 2),
        )
    }
}

@Composable
fun VerticalDashDivider(
    modifier: Modifier = Modifier,
    thickness: Dp = 1.dp,
    color: Color = MaterialTheme.colorScheme.outline,
    intervals: FloatArray = floatArrayOf(20f, 20f),
    phase: Float = 10f,
) {
    Canvas(
        modifier.width(thickness)
    ) {
        drawLine(
            color = color,
            pathEffect = PathEffect.dashPathEffect(intervals, phase),
            strokeWidth = thickness.toPx(),
            start = Offset(thickness.toPx() / 2, 0f),
            end = Offset(thickness.toPx() / 2, size.height),
        )
    }
}