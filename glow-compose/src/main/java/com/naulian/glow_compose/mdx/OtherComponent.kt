package com.naulian.glow_compose.mdx

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.naulian.glow_core.mdx.MdxToken
import com.naulian.glow_core.mdx.MdxType
import com.naulian.modify.table.Table
import com.naulian.modify.table.TableHeader
import com.naulian.modify.table.TableItems

val a2z = ('a'..'z').toList()
val A2Z = ('A'..'Z').toList()

@Composable
fun HeaderBlock(modifier: Modifier = Modifier, token: MdxToken) {
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
        fontSize = sizePair.first,
        fontWeight = FontWeight.Bold,
        lineHeight = sizePair.second
    )
}

@Composable
fun OtherComponent(tokens: List<MdxToken>) {
    tokens.forEach { token ->
        when (token.type) {
            MdxType.H1,
            MdxType.H2,
            MdxType.H3,
            MdxType.H4,
            MdxType.H5,
            MdxType.H6 -> HeaderBlock(token = token)

            MdxType.QUOTE -> {
                QuoteBlock(quote = token.text)
            }

            MdxType.CODE -> MdxCodeBlock(token = token)

            MdxType.IMAGE -> MdxImageBlock(token = token)
            MdxType.DIVIDER -> {
                when (token.text) {
                    "line" -> HorizontalDivider()
                    "" -> {}
                    else -> Text(text = token.text, modifier = Modifier.fillMaxWidth())
                }
            }

            MdxType.TABLE -> {
                val (cols, rows) = token.getTableItemPairs()

                Table(
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

            else -> {}
        }
    }
}