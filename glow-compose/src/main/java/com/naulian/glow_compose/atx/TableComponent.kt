package com.naulian.glow_compose.atx

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.naulian.glow_core.atx.AtxToken
import com.naulian.modify.table.Table


@Composable
fun TableComponent(tokens: List<AtxToken>) {
    tokens.forEach { token ->
        val columns = token.argument.split(",").map { it.trim() }
        val cellCol = if (columns.isEmpty()) 1 else columns.size
        val rows = token.value.split(",")
            .map { it.trim() }.chunked(cellCol)
        Table(modifier = Modifier.padding(12.dp), data = listOf(columns)) {
            Text(modifier = Modifier.padding(horizontal = 10.dp), text = it)
        }
        Table(modifier = Modifier.padding(12.dp), data = rows) {
            Text(modifier = Modifier.padding(horizontal = 10.dp), text = it)
        }
    }
}