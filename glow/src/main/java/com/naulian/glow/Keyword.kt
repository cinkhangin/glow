package com.naulian.glow

object Keyword {
    val kotlin = listOf(
        "fun", "val", "var",
        "if", "else", "for",
        "in", "while",
        "return", //"const"
        "import", "package",
        "class", //"override",
        "object", //"data",
        "private", "public",
    )

    val lists = listOf(
        "listOf\\(",
        "arrayListOf\\(",
        "sortedMapOf\\(",
        "hashMapOf\\(",
        "mapOf\\(",
        "arrayOf\\(",
        "emptyList\\(",
        "emptyArray\\("
    )
}