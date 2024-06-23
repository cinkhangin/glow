package com.naulian.glow_core.mdx

enum class MdxType {
    //Headers
    H1, H2, H3, H4, H5, H6,
    TEXT, BOLD, ITALIC, UNDERLINE, STRIKE,
    QUOTE, LINK, HYPER_LINK, CODE, DATETIME,
    TABLE, ELEMENT, ESCAPE, COLORED,
    IMAGE, VIDEO, YOUTUBE,
    WHITESPACE, DIVIDER, EOF,
}

data class MdxNode(
    val type: MdxType,
    val literal: String,
    val children: List<MdxNode> = emptyList(),
) {
    companion object {
        val EOF = MdxNode(MdxType.EOF, "")
    }

    fun isBlankText() = type == MdxType.TEXT && literal.isBlank()
    fun getHyperLink(): Pair<String, String> {
        if (literal.contains("@")) {
            val index = literal.indexOf("@")
            val hyper = literal.take(index)
            val link = literal.replace("$hyper@", "")

            return hyper to link
        }
        return "" to literal
    }

    fun getTableItemPairs(): Pair<List<String>, List<List<String>>> {
        val lines = literal.split("\n")

        if (lines.isEmpty()) {
            return emptyList<String>() to emptyList()
        }

        var columns = emptyList<String>()
        if (lines.first().isNotBlank()) {
            columns = lines.first().split("|")
                .map { it.trim() }
        }

        if (lines.size > 1) {
            val rows = lines.drop(1)
            return columns to rows.map { row ->
                row.split("|").map { it.trim() }
            }
        }
        return columns to emptyList()
    }

    fun getTextColorPair(): Pair<String, String> {
        if (literal.contains("#")) {
            val index = literal.indexOf("#")
            val value = literal.take(index)
            val hexColor = literal.drop(index).trim()
            return value to hexColor
        }
        return literal to "#222222"
    }

    fun getLangCodePair(): Pair<String, String> {
        if (literal.contains("\n")) {
            val index = literal.indexOf("\n")
            val lang = literal.take(index)
            val code = literal.drop(index).trim()

            if (lang.contains('.')) {
                return lang.replace(".", "") to code
            }
        }

        return "txt" to literal
    }
}