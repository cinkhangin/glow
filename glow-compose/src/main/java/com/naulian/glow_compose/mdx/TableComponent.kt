package com.naulian.glow_compose.mdx

import androidx.compose.runtime.Composable
import com.naulian.glow_core.mdx.MdxToken
import com.naulian.modify.table.Table
import com.naulian.modify.table.TableHeader
import com.naulian.modify.table.TableItems


@Composable
fun TableComponent(tokens: List<MdxToken>) {

    tokens.forEach {
        val (cols, rows) = it.getTableItemPairs()

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
}