package com.naulian.glow_core.mdx

object MdxType {
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

data class MdxNode(
    val type: String = MdxType.ROOT,
    val literal: String = "",
    val children: List<MdxNode> = emptyList()
) {
    companion object {
        val EOF = MdxNode(MdxType.EOF, "")

        fun create(type: String, literal: CharSequence) = MdxNode(type, literal.toString())
    }

    fun getHyperLink(): Pair<String, String> {
        if (literal.contains("@")) {
            val index = literal.indexOf("@")
            val hyper = literal.take(index)
            val link = literal.replace("$hyper@", "")

            return hyper to link
        }
        return "" to literal
    }

    fun getTableData(): List<List<MdxNode>> {
        return children.map { col ->
            col.children.filterNot { it.type == MdxType.PIPE }
        }
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
private const val MDX_SYMBOL_CHARS = "\"</>&|#{%}[~]\\`(*)_\n"
private const val MDX_WHITESPACES = " \n"

class MdxLexer(input: String) {
    private var cursor = 0
    private val source = input

    private val Char.isNotSpaceChar get() = this != ' '
    private val Char.isNotEndChar get() = this != MDX_END_CHAR
    private val Char.isNotSymbols get() = this !in MDX_SYMBOL_CHARS

    private val charNotEndChar get() = char() != MDX_END_CHAR

    private fun char() = source.getOrElse(cursor) { Char.MIN_VALUE }

    fun tokenize(): List<MdxNode> {
        val tokens = mutableListOf<MdxNode>()
        var current = next()
        while (current.type != MdxType.EOF) {
            tokens.add(current)
            current = next()
        }
        return tokens
    }

    private fun advance(amount: Int = 1) {
        cursor += amount
    }

    fun next(): MdxNode {
        println(char())
        return when (val char = char()) {
            in MDX_WHITESPACES -> createWhiteSpaceToken()
            '&' -> createSymbolToken(MdxType.BLOCK_SYMBOL)
            '/' -> createSymbolToken(MdxType.BLOCK_SYMBOL)
            '_' -> createSymbolToken(MdxType.BLOCK_SYMBOL)
            '"' -> createSymbolToken(MdxType.BLOCK_SYMBOL)
            '~' -> createSymbolToken(MdxType.BLOCK_SYMBOL)

            '[' -> createSymbolToken(MdxType.TABLE_START)
            ']' -> createSymbolToken(MdxType.TABLE_END)
            '|' -> createSymbolToken(MdxType.PIPE)

            '<' -> createSymbolToken(MdxType.COLOR_START)
            '>' -> createSymbolToken(MdxType.COLOR_END)

            '=' -> createBlockToken(MdxType.DIVIDER, char)
            '`' -> {
                advance()
                val start = cursor
                while (char() != '`' && charNotEndChar) advance()
                val end = cursor
                advance()
                val value = source.subSequence(start, end)
                MdxNode(MdxType.IGNORE, value.toString())
            }

            '%' -> createBlockToken(MdxType.DATETIME, char)
            '(' -> createLinkToken()
            '{' -> createCodeToken()
            '#' -> createHeaderToken()
            '*' -> createElementToken()
            '\\' -> createEscapedToken()
            in MDX_SYMBOL_CHARS -> createSymbolToken(MdxType.TEXT) //prevent memory leak
            Char.MIN_VALUE -> MdxNode.EOF
            else -> createTextToken()
        }
    }

    private fun createWhiteSpaceToken(): MdxNode {
        val start = cursor
        while (char().isWhitespace()) {
            advance()
        }
        val end = cursor
        val literal = source.subSequence(start, end)
        return when {
            literal.contains("\n") -> MdxNode.create(MdxType.NEWLINE, literal)
            else -> MdxNode.create(MdxType.WHITESPACE, literal)
        }
    }

    private fun createHeaderToken(): MdxNode {
        advance()
        if (char() == MDX_END_CHAR) {
            // early return
            return MdxNode(MdxType.TEXT, "#")
        }

        val start = cursor
        advanceWhile { it.isNotSpaceChar && it.isNotEndChar && it.isNotSymbols }
        return when (val value = source.subSequence(start, cursor)) {
            in "123456" -> MdxNode(MdxType.HEADER, "#$value")
            else -> MdxNode(MdxType.COLOR_HEX, "#$value")
        }
    }

    private fun createEscapedToken(): MdxNode {
        advance()
        if (char() == MDX_END_CHAR) {
            return MdxNode.EOF
        }
        val literal = char().toString()
        advance()
        return MdxNode(MdxType.ESCAPE, literal)
    }

    private fun createCodeToken(): MdxNode {
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
        return MdxNode(MdxType.CODE, code)
    }

    private fun createLinkToken(): MdxNode {
        advance() //skip opening parenthesis
        val start = cursor
        while (char() != ')' && charNotEndChar) {
            advance()
        }

        val value = source.subSequence(start, cursor).str()
        advance() //skip closing parenthesis

        if (!value.contains("http")) {
            return MdxNode(MdxType.TEXT, "($value)")
        }

        if (value.contains("@")) {
            val index = value.indexOf("@")
            val hyper = value.take(index)
            val link = value.str().replace("$hyper@", "")

            return when (hyper) {
                "img" -> MdxNode(MdxType.IMAGE, link)
                "ytb" -> MdxNode(MdxType.YOUTUBE, link)
                "vid" -> MdxNode(MdxType.VIDEO, link)
                else -> MdxNode(MdxType.HYPER_LINK, value)
            }
        }

        return MdxNode(MdxType.LINK, value)
    }

    private fun createElementToken(): MdxNode {
        advance()
        if (char() == MDX_END_CHAR) {
            // early return
            return MdxNode(MdxType.TEXT, "*")
        }

        val start = cursor
        advanceWhile { it.isNotSpaceChar && it.isNotEndChar && it.isNotSymbols }
        val value = source.subSequence(start, cursor)
        return MdxNode(MdxType.ELEMENT, "*$value")
    }

    private fun createTextToken(): MdxNode {
        val start = cursor
        advanceWhile { it.isNotSymbols && it.isNotEndChar }
        val literal = source.subSequence(start, cursor).toString()
        return MdxNode.create(MdxType.TEXT, literal)
    }

    private fun createSymbolToken(type: String): MdxNode {
        val value = char().toString()
        advance()
        return MdxNode(type, value)
    }

    private fun createBlockToken(type: String, char: Char): MdxNode {
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
            MdxType.DATETIME -> {
                try {
                    val value = formattedDateTime(blockValue)
                    MdxNode(type, value)
                } catch (e: Exception) {
                    MdxNode(MdxType.TEXT, blockValue)
                }
            }

            else -> MdxNode(type, blockValue)
        }
    }

    private fun advanceWhile(condition: (Char) -> Boolean) {
        while (condition(char())) advance()
    }
}