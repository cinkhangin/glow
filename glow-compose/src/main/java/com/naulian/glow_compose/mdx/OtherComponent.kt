package com.naulian.glow_compose.mdx

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.text.isDigitsOnly
import com.naulian.glow_compose.Preview
import com.naulian.glow_core.mdx.MdxToken
import com.naulian.glow_core.mdx.MdxType
import com.naulian.modify.table.Table
import com.naulian.modify.table.TableHeader
import com.naulian.modify.table.TableItems

@Composable
fun HeaderBlock(
    modifier: Modifier = Modifier,
    token: MdxToken,
    fontFamily: FontFamily = FontFamily.Default
) {
    val sizePair = when (token.type) {
        MdxType.H1 -> 32.sp to 40.sp
        MdxType.H2 -> 28.sp to 34.sp
        MdxType.H3 -> 24.sp to 30.sp
        MdxType.H4 -> 20.sp to 26.sp
        MdxType.H5 -> 18.sp to 22.sp
        MdxType.H6 -> 16.sp to 20.sp
        else -> 24.sp to 32.sp
    }

    Text(
        modifier = modifier,
        text = token.text,
        fontFamily = fontFamily,
        fontSize = sizePair.first,
        fontWeight = FontWeight.Bold,
        lineHeight = sizePair.second
    )
}

@Composable
fun OtherComponent(tokens: List<MdxToken>, components: MdxComponents) {
    tokens.forEach { token ->
        when (token.type) {
            MdxType.H1,
            MdxType.H2,
            MdxType.H3,
            MdxType.H4,
            MdxType.H5,
            MdxType.H6 -> components.header(token)

            MdxType.QUOTE -> components.quote(token)
            MdxType.CODE -> components.codeBlock(token)
            MdxType.IMAGE -> components.image(token)
            MdxType.VIDEO -> components.video(token)
            MdxType.YOUTUBE -> components.youtube(token)
            MdxType.DIVIDER -> components.divider(token)
            MdxType.TABLE -> components.table(token)
            else -> {}
        }
    }
}

@Composable
fun MdxTable(
    modifier: Modifier = Modifier,
    token: MdxToken,
    fontFamily: FontFamily = FontFamily.Default
) {
    val (cols, rows) = token.getTableItemPairs()

    Table(
        modifier = modifier,
        header = {
            if (cols.isNotEmpty()) {
                if (cols.size == 1) {
                    TableHeader(title = cols.first())
                } else TableHeader(items = cols)
            }
        },
        content = {
            if (rows.isNotEmpty()) {
                TableItems(items = rows)
            }
        }
    )
}

@Composable
fun MdxDivider(token: MdxToken, customMap: Map<String, @Composable () -> Unit> = emptyMap()) {
    when (token.text) {
        "line" -> HorizontalDivider()
        "dash" -> HorizontalDashDivider()
        "br" -> Spacer(modifier = Modifier.height(2.dp))
        "" -> {}
        else -> {
            if (token.text.isDigitsOnly()) {
                val sizeInt = token.text.toInt()
                Spacer(modifier = Modifier.height(sizeInt.dp))
            }

            customMap[token.text]?.invoke()
        }
    }
}

@Preview
@Composable
private fun MdxDividerPreview() {
    Preview {
        Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center) {
            MdxDivider(token = MdxToken(MdxType.DIVIDER, "line"))
            MdxDivider(token = MdxToken(MdxType.DIVIDER, "20"))
            MdxDivider(token = MdxToken(MdxType.DIVIDER, "dash"))
            MdxDivider(token = MdxToken(MdxType.DIVIDER, "br"))
            MdxDivider(
                token = MdxToken(MdxType.DIVIDER, "text"),
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