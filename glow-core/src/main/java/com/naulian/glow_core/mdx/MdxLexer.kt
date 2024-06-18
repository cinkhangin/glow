package com.naulian.glow_core.mdx

class MdxLexer(input: String) {
    private var cursor = 0
    private val source = input
    private val endChar = Char.MIN_VALUE
    private val isNotEndChar get() = char() != endChar
    private val isNotNewLine get() = char() != '\n'
    private val symbolChars = "\"</>&{%}[~]\\(-)_\n"
    private val charIsNotSymbol get() = char() !in symbolChars
    private fun char() = source.getOrElse(cursor) { Char.MIN_VALUE }

    private fun skipWhiteSpace() {
        while (char().isWhitespace()) advance()
    }

    fun tokenize(): List<MdxToken> {
        val tokens = mutableListOf<MdxToken>()
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

    fun next(): MdxToken {
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
                MdxToken(MdxType.WHITESPACE, "\n")
            }

            in symbolChars -> {
                advance()
                MdxToken(MdxType.TEXT, char.toString())
            }

            Char.MIN_VALUE -> MdxToken.EOF
            else -> createTextToken()
        }
    }

    private fun createEscapedToken(): MdxToken {
        advance()
        val escapedChar = char()
        return if (escapedChar in symbolChars) {
            advance()
            MdxToken(MdxType.TEXT, escapedChar.toString())
        } else createTextToken()
    }

    private fun createCodeToken(): MdxToken {
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
        return MdxToken(MdxType.CODE, code)
    }

    private fun createLinkToken(): MdxToken {
        advance() //skip opening parenthesis
        val start = cursor
        var level = 0
        while (isNotEndChar) {
            if (char() == '(') {
                level++
            }
            if (char() == ')') {
                if (level == 0) {
                    break
                } else level--
            }
            advance()
        }

        val value = source.subSequence(start, cursor).str()
        advance() //skip closing parenthesis

        if (!value.contains("http")) {
            return MdxToken(MdxType.TEXT, "($value)")
        }

        if (value.contains("@")) {
            val index = value.indexOf("@")
            val hyper = value.take(index)
            val link = value.str().replace("$hyper@", "")

            return when (hyper) {
                "img" -> MdxToken(MdxType.IMAGE, link)
                "ytb" -> MdxToken(MdxType.YOUTUBE, link)
                "vid" -> MdxToken(MdxType.VIDEO, link)
                else -> MdxToken(MdxType.HYPER_LINK, value)
            }
        }

        return MdxToken(MdxType.LINK, value)
    }

    private fun createElementToken(): MdxToken {
        advance()
        skipWhiteSpace()
        val start = cursor
        while (charIsNotSymbol && isNotEndChar) {
            advance()
        }
        val text = source.subSequence(start, cursor).toString()

        return MdxToken(MdxType.ELEMENT, text)
    }

    private fun createTextToken(): MdxToken {
        val start = cursor
        while (charIsNotSymbol && isNotEndChar) {
            advance()
        }
        val value = source.subSequence(start, cursor).toString()

        var text = value
        mdxAdhocMap.forEach {
            text = text.replace(it.key, it.value)
        }
        return MdxToken(MdxType.TEXT, text)
    }

    private fun createBlockToken(type: MdxType, char: Char): MdxToken {
        advance() //skip the opening char
        val start = cursor
        var prevChar = char()
        while (!(char() == char && prevChar != '\\') && isNotEndChar) {
            prevChar = char()
            advance()
        }

        val blockValue = source.subSequence(start, cursor).str()
        advance() //skip the closing char

        if (type == MdxType.ESCAPE) {
            val escapedValue = blockValue.replace("\n==\n", "\n\n")
            return MdxToken(type, escapedValue)
        }

        return when (type) {
            MdxType.DATETIME -> {
                val value = formattedDateTime(blockValue)
                MdxToken(type, value)
            }

            MdxType.DIVIDER -> {
                if (blockValue.isEmpty()) {
                    MdxToken(MdxType.WHITESPACE, "\n")
                } else MdxToken(type, blockValue)
            }

            else -> MdxToken(type, blockValue)
        }
    }

    private fun createHeaderToken(): MdxToken {
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
        return MdxToken(type, text.str())
    }

    private fun createTableToken(): MdxToken {
        advance() //skip opening bracket
        val start = cursor
        var level = 0
        while (isNotEndChar) {
            if (char() == '[') {
                level++
            }
            if (char() == ']') {
                if (level == 0) {
                    break
                } else level--
            }
            advance()
        }
        val end = cursor
        advance() //skip closing bracket
        val value = source.subSequence(start, end)
        return MdxToken(MdxType.TABLE, value.str())
    }

    private fun createColoredToken(): MdxToken {
        advance() //skip opening bracket
        val start = cursor
        var level = 0
        while (isNotEndChar) {
            if (char() == '<') {
                level++
            }
            if (char() == '>') {
                if (level == 0) {
                    break
                } else level--
            }
            advance()
        }
        val end = cursor
        advance() //skip closing bracket
        val value = source.subSequence(start, end).str()
        return MdxToken(MdxType.COLORED, value)
    }
}