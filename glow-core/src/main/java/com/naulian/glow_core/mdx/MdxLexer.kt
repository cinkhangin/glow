package com.naulian.glow_core.mdx

class MdxLexer(input: String) {
    private var cursor = 0
    private val source = input
    private val endChar = Char.MIN_VALUE
    private val isNotEndChar get() = char() != endChar
    private val isNotNewLine get() = char() != '\n'
    private val symbolChars = "\"</>&`{%}[~]\\(-)_\n"
    private val charIsNotSymbol get() = char() !in symbolChars
    private fun char() = source.getOrElse(cursor) { Char.MIN_VALUE }

    private fun skipWhiteSpace() {
        while (char().isWhitespace()) advance()
    }

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
        return when (val char = char()) {
            '&' -> createBlockToken(MdxType.BOLD, char)
            '/' -> createBlockToken(MdxType.ITALIC, char)
            '_' -> createBlockToken(MdxType.UNDERLINE, char)
            '"' -> createBlockToken(MdxType.QUOTE, char)
            '=' -> createBlockToken(MdxType.DIVIDER, char)
            '`' -> createBlockToken(MdxType.ESCAPE, char)
            '~' -> createBlockToken(MdxType.STRIKE, char)
            '%' -> createBlockToken(MdxType.DATETIME, char)
            '[' -> createTableToken()
            '<' -> createColoredToken()
            '#' -> createHeaderToken()
            '{' -> createCodeToken()
            '*' -> createElementToken()
            '(' -> createLinkToken()
            '\\' -> createEscapedToken()
            '\n' -> {
                advance()
                MdxNode(MdxType.WHITESPACE, "\n")
            }

            in symbolChars -> {
                advance()
                MdxNode(MdxType.TEXT, char.toString())
            }

            Char.MIN_VALUE -> MdxNode.EOF
            else -> createTextToken()
        }
    }

    private fun createEscapedToken(): MdxNode {
        advance()
        val escapedChar = char()
        return if (escapedChar in symbolChars) {
            advance()
            MdxNode(MdxType.TEXT, escapedChar.toString())
        } else createTextToken()
    }

    private fun createCodeToken(): MdxNode {
        advance() //skip opening bracket
        val start = cursor
        var level = 0
        while (isNotEndChar) {
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
        while (char() != ')' && isNotEndChar) {
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
        skipWhiteSpace()
        val start = cursor
        while (charIsNotSymbol && isNotEndChar) {
            advance()
        }
        val text = source.subSequence(start, cursor).toString()

        return MdxNode(MdxType.ELEMENT, text)
    }

    private fun createTextToken(): MdxNode {
        val start = cursor
        while (charIsNotSymbol && isNotEndChar) {
            advance()
        }
        val value = source.subSequence(start, cursor).toString()

        var text = value
        mdxAdhocMap.forEach {
            text = text.replace(it.key, it.value)
        }
        return MdxNode(MdxType.TEXT, text)
    }

    private fun createBlockToken(type: MdxType, char: Char): MdxNode {
        advance() //skip the opening char
        val start = cursor
        var prevChar = char()
        while (!(char() == char && prevChar != '\\') && isNotEndChar) {
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

    private fun createHeaderToken(): MdxNode {
        advance()
        val type = when (char()) {
            '1' -> MdxType.H1
            '2' -> MdxType.H2
            '3' -> MdxType.H3
            '4' -> MdxType.H4
            '5' -> MdxType.H5
            '6' -> MdxType.H6
            Char.MIN_VALUE -> MdxType.EOF
            else -> MdxType.TEXT
        }
        advance()
        skipWhiteSpace()
        val start = cursor
        while (isNotNewLine && isNotEndChar) {
            advance()
        }
        val text = source.subSequence(start, cursor)
        return MdxNode(type, text.str())
    }

    private fun createTableToken(): MdxNode {
        advance() //skip opening bracket
        val start = cursor
        while (char() != ']' && isNotEndChar) {
            advance()
        }
        val end = cursor
        advance() //skip closing bracket
        val value = source.subSequence(start, end)
        return MdxNode(MdxType.TABLE, value.str())
    }

    private fun createColoredToken(): MdxNode {
        advance() //skip opening bracket
        val start = cursor
        while (char() != '>' && isNotEndChar) {
            advance()
        }
        val end = cursor
        advance() //skip closing bracket
        val value = source.subSequence(start, end).str()
        return MdxNode(MdxType.COLORED, value)
    }
}