package com.naulian.glow_compose.atx

import androidx.compose.runtime.Composable
import com.naulian.glow_core.atx.AtxToken
import com.naulian.modify.table.Table
import com.naulian.modify.table.TableHeader
import com.naulian.modify.table.TableItems


@Composable
fun TableComponent(tokens: List<AtxToken>) {
    tokens.forEach { token ->
        val columns = token.argument.split(",").map { it.trim() }
        val cellCol = if (columns.isEmpty()) 1 else columns.size
        val rows = token.value.split(",")
            .map { it.trim() }.chunked(cellCol)

        Table(
            header = {
                if (columns.isNotEmpty()) {
                    if (columns.size == 1) {
                        TableHeader(title = columns.first())
                    } else TableHeader(items = columns)
                }
            },
            content = {
                if (rows.isNotEmpty()) {
                    TableItems(items = rows)
                }
            }
        )
    }
}