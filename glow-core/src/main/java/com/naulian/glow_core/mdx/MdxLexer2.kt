package com.naulian.glow_core.mdx

object MdxType2 {
    const val HEADER = "header"
    const val BLOCK_SYMBOL = "block_symbol"
    const val COLOR_START = "color_start"
    const val COLOR_END = "color_end"
    const val COLOR_HEX = "color_hex"
    const val ELEMENT_BULLET = "element_bullet"
    const val ELEMENT_UNCHECKED = "element_unchecked"
    const val ELEMENT_CHECKED = "element_checked"
    const val ELEMENT = "element"
    const val TABLE_END = "table_end"
    const val TABLE_START = "table_start"
    const val PIPE = "pipe"
    const val TABLE = "table"
    const val TABLE_COLOMN = "table_column"
    const val BOLD = "bold"
    const val ITALIC = "italic"
    const val UNDERLINE = "underline"
    const val STRIKE = "strike"
    const val TEXT = "text"
    const val PARAGRAPH = "paragraph"
    const val COMPONENT = "component"
    const val IMAGE = "image"
    const val VIDEO = "video"
    const val YOUTUBE = "youtube"
    const val LINK = "link"
    const val HYPER_LINK = "hyper_link"
    const val WHITESPACE = "whitespace"
    const val NEWLINE = "newline"
    const val QUOTATION = "quotation"
    const val CODE = "code"
    const val DATETIME = "datetime"
    const val ESCAPE = "escape"
    const val IGNORE = "ignore"
    const val COLORED = "colored"
    const val DIVIDER = "divider"
    const val ILLEGAL = "illegal"
    const val EOF = "eof"
    const val ROOT = "root"
    const val H1 = "h1"
    const val H2 = "h2"
    const val H3 = "h3"
    const val H4 = "h4"
    const val H5 = "h5"
    const val H6 = "h6"
}

data class MdxNode2(
    val type: String = MdxType2.ROOT,
    val literal: String = "",
    val children: List<MdxNode2> = emptyList()
) {
    companion object {
        val EOF = MdxNode2(MdxType2.EOF, "")

        fun create(type: String, literal: CharSequence) = MdxNode2(type, literal.toString())
    }

    fun isBlankText() = type == MdxType2.TEXT && literal.isBlank()
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

private const val MDX_END_CHAR = Char.MIN_VALUE
private const val MDX_TEXT_CHARS =
    "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789- .,;:!?"
private const val MDX_SYMBOL_CHARS = "\"</>&`|{%}[~]\\(-)_\n"
private const val MDX_WHITESPACES = " \n"

class MdxLexer2(input: String) {
    private var cursor = 0
    private val source = input

    private val Char.isNotSpaceChar get() = this != ' '
    private val Char.isNotEndChar get() = this != MDX_END_CHAR
    private val Char.isNotSymbols get() = this !in MDX_SYMBOL_CHARS

    private val charNotEndChar get() = char() != MDX_END_CHAR

    private fun char() = source.getOrElse(cursor) { Char.MIN_VALUE }

    fun tokenize(): List<MdxNode2> {
        val tokens = mutableListOf<MdxNode2>()
        var current = next()
        while (current.type != MdxType2.EOF) {
            tokens.add(current)
            current = next()
        }
        return tokens
    }

    private fun advance(amount: Int = 1) {
        cursor += amount
    }

    fun next(): MdxNode2 {
        return when (val char = char()) {
            in MDX_WHITESPACES -> createWhiteSpaceToken()
            '&' -> createSymbolToken(MdxType2.BLOCK_SYMBOL)
            '/' -> createSymbolToken(MdxType2.BLOCK_SYMBOL)
            '_' -> createSymbolToken(MdxType2.BLOCK_SYMBOL)
            '"' -> createSymbolToken(MdxType2.BLOCK_SYMBOL)
            '~' -> createSymbolToken(MdxType2.BLOCK_SYMBOL)

            '[' -> createSymbolToken(MdxType2.TABLE_START)
            ']' -> createSymbolToken(MdxType2.TABLE_END)
            '|' -> createSymbolToken(MdxType2.PIPE)

            '<' -> createSymbolToken(MdxType2.COLOR_START)
            '>' -> createSymbolToken(MdxType2.COLOR_END)

            '=' -> createBlockToken(MdxType2.DIVIDER, char)
            '`' -> {
                advance()
                val start = cursor
                while (char() != '`' && charNotEndChar) advance()
                val end = cursor
                advance()
                val value = source.subSequence(start, end)
                MdxNode2(MdxType2.IGNORE, value.toString())
            }

            '%' -> createBlockToken(MdxType2.DATETIME, char)
            '(' -> createLinkToken()
            '{' -> createCodeToken()
            '#' -> createHeaderToken()
            '*' -> createElementToken()
            '\\' -> createEscapedToken()
            in MDX_TEXT_CHARS -> createTextToken()
            Char.MIN_VALUE -> MdxNode2.EOF
            else -> createSymbolToken(MdxType2.ILLEGAL)
        }
    }

    private fun createWhiteSpaceToken(): MdxNode2 {
        val start = cursor
        while (char().isWhitespace()) {
            advance()
        }
        val end = cursor
        return when (val literal = source.subSequence(start, end)) {
            "\n" -> MdxNode2.create(MdxType2.NEWLINE, literal)
            "\n\n" -> MdxNode2.create(MdxType2.NEWLINE, literal)
            else -> MdxNode2.create(MdxType2.WHITESPACE, literal)
        }
    }

    private fun createHeaderToken(): MdxNode2 {
        advance()
        if (char() == MDX_END_CHAR) {
            // early return
            return MdxNode2(MdxType2.TEXT, "#")
        }

        val start = cursor
        advanceWhile { it.isNotSpaceChar && it.isNotEndChar && it.isNotSymbols }
        return when (val value = source.subSequence(start, cursor)) {
            in "123456" -> MdxNode2(MdxType2.HEADER, "#$value")
            else -> MdxNode2(MdxType2.COLOR_HEX, "#$value")
        }
    }

    private fun createEscapedToken(): MdxNode2 {
        advance()
        if (char() == MDX_END_CHAR) {
            return MdxNode2.EOF
        }
        val literal = char().toString()
        advance()
        return MdxNode2(MdxType2.ESCAPE, literal)
    }

    private fun createCodeToken(): MdxNode2 {
        advance() //skip opening bracket
        val start = cursor
        var level = 0
        while (charNotEndChar) {
            if (char() == '{') {
                level++
            }
            if (char() == '}') {
                if (level == 0) {
                    break
                } else level--
            }
            advance()
        }
        val end = cursor
        advance() //skip closing bracket
        val value = source.subSequence(start, end)

        var code = value.str()
        mdxAdhocMap.forEach {
            code = code.replace(it.key, it.value)
        }
        return MdxNode2(MdxType2.CODE, code)
    }

    private fun createLinkToken(): MdxNode2 {
        advance() //skip opening parenthesis
        val start = cursor
        while (char() != ')' && charNotEndChar) {
            advance()
        }

        val value = source.subSequence(start, cursor).str()
        advance() //skip closing parenthesis

        if (!value.contains("http")) {
            return MdxNode2(MdxType2.TEXT, "($value)")
        }

        if (value.contains("@")) {
            val index = value.indexOf("@")
            val hyper = value.take(index)
            val link = value.str().replace("$hyper@", "")

            return when (hyper) {
                "img" -> MdxNode2(MdxType2.IMAGE, link)
                "ytb" -> MdxNode2(MdxType2.YOUTUBE, link)
                "vid" -> MdxNode2(MdxType2.VIDEO, link)
                else -> MdxNode2(MdxType2.HYPER_LINK, value)
            }
        }

        return MdxNode2(MdxType2.LINK, value)
    }

    private fun createElementToken(): MdxNode2 {
        advance()
        if (char() == MDX_END_CHAR) {
            // early return
            return MdxNode2(MdxType2.TEXT, "*")
        }

        val start = cursor
        advanceWhile { it.isNotSpaceChar && it.isNotEndChar && it.isNotSymbols }
        val value = source.subSequence(start, cursor)
        return MdxNode2(MdxType2.ELEMENT, "*$value")
    }

    private fun createTextToken(): MdxNode2 {
        val start = cursor
        advanceWhile { it in MDX_TEXT_CHARS }
        val literal = source.subSequence(start, cursor).toString()
        return MdxNode2.create(MdxType2.TEXT, literal)
    }

    private fun createSymbolToken(type: String): MdxNode2 {
        val value = char().toString()
        advance()
        return MdxNode2(type, value)
    }

    private fun createBlockToken(type: String, char: Char): MdxNode2 {
        advance() //skip the opening char
        val start = cursor
        var prevChar = char()
        while (!(char() == char && prevChar != '\\') && charNotEndChar) {
            prevChar = char()
            advance()
        }

        val blockValue = source.subSequence(start, cursor).str()
        advance() //skip the closing char

        return when (type) {
            MdxType2.DATETIME -> {
                try {
                    val value = formattedDateTime(blockValue)
                    MdxNode2(type, value)
                } catch (e: Exception) {
                    MdxNode2(MdxType2.TEXT, blockValue)
                }
            }

            else -> MdxNode2(type, blockValue)
        }
    }

    private fun advanceWhile(condition: (Char) -> Boolean) {
        while (condition(char())) {
            advance()
        }
    }
}