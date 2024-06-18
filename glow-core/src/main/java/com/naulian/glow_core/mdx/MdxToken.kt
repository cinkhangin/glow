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

data class MdxToken(
    val type: MdxType,
    val text: String,
) {
    companion object {
        val EOF = MdxToken(MdxType.EOF, "")
    }

    fun isBlankText() = type == MdxType.TEXT && text.isBlank()
    fun getHyperLink(): Pair<String, String> {
        if (text.contains("@")) {
            val index = text.indexOf("@")
            val hyper = text.take(index)
            val link = text.replace("$hyper@", "")

            return hyper to link
        }
        return "" to text
    }

    fun getTableItemPairs(): Pair<List<String>, List<List<String>>> {
        val lines = text.split("\n")

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
        if (text.contains("#")) {
            val index = text.indexOf("#")
            val value = text.take(index)
            val hexColor = text.drop(index).trim()
            return value to hexColor
        }
        return text to "#222222"
    }

    fun getLangCodePair(): Pair<String, String> {
        if (text.contains("\n")) {
            val index = text.indexOf("\n")
            val lang = text.take(index)
            val code = text.drop(index).trim()

            if (lang.contains('.')) {
                return lang.replace(".", "") to code
            }
        }

        return "txt" to text
    }
}