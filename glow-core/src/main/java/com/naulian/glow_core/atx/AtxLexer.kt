package com.naulian.glow_core.atx

class BaseLexer(input: String) {
    private var cursor = 0
    private val source = input.replace(" @j\n", " ")
    private val char get() = source.getOrElse(cursor) { Char.MIN_VALUE }

    private fun advance(amount: Int = 1) {
        cursor += amount
    }

    fun tokenize(): List<BaseToken> {
        val tokens = mutableListOf<BaseToken>()
        var current = next()
        while (current.type != BaseType.EOF) {
            tokens.add(current)
            current = next()
        }
        //tokens.forEach(::println)
        return tokens
    }

    fun next(): BaseToken {
        while (char == '\n') advance()

        return when (char) {
            Char.MIN_VALUE -> BaseToken.EOF
            '@' -> lexKeyword()
            '(' -> lexValue()
            '[' -> lexArgument()
            /*'\n' -> {
                advance()
                BaseToken(BaseType.NEWLINE, "\n")
            }*/

            else -> lexText()
        }
    }

    private fun lexValue(): BaseToken {
        advance() //skip opening parenthesis
        val start = cursor
        var level = 0
        while (char != Char.MIN_VALUE) {
            if (char == '(') {
                level++
            }
            if (char == ')') {
                if (level == 0) {
                    break
                } else level--
            }
            advance()
        }
        val end = cursor
        advance() //skip closing parenthesis
        val value = source.subSequence(start, end)
        return BaseToken(BaseType.VALUE, value.str())
    }

    private fun lexArgument(): BaseToken {
        advance() //skip opening bracket
        val start = cursor
        var level = 0
        while (char != Char.MIN_VALUE) {
            if (char == '[') {
                level++
            }
            if (char == ']') {
                if (level == 0) {
                    break
                } else level--
            }
            advance()
        }
        val end = cursor
        advance() //skip closing bracket
        val value = source.subSequence(start, end)
        return BaseToken(BaseType.ARGUMENT, value.str())
    }

    private fun lexKeyword(): BaseToken {
        advance() //skip the symbol
        return when (val c = char) {
            in 'a'..'z' -> {
                advance() //skip the keyword
                BaseToken(BaseType.KEYWORD, "@$c")
            }

            else -> {
                advance() //skip the keyword
                BaseToken(BaseType.TEXT, "$c")
            }
        }
    }

    private fun lexText(): BaseToken {
        val start = cursor
        while (char != '@' && char != '\n' && char != Char.MIN_VALUE) {
            advance()
        }
        val end = cursor
        val text = source.subSequence(start, end)
        return BaseToken(BaseType.TEXT, text.toString())
    }
}

class AtxLexer(source: String) {
    private val baseLexer = BaseLexer(source)
    private val baseTokens = baseLexer.tokenize()

    private var cursor = 0
    private fun token() = baseTokens.getOrElse(cursor) { BaseToken.EOF }
    private fun advance(amount: Int = 1) {
        cursor += amount
    }

    fun tokenize(): List<AtxToken> {
        val tokens = mutableListOf<AtxToken>()
        var current = next()
        while (current.type != AtxType.EOF) {
            tokens.add(current)
            current = next()
        }
        return tokens
    }

    fun next(): AtxToken {
        val token = token()
        return when (token.type) {
            BaseType.KEYWORD -> lex(token.text)
            BaseType.TEXT -> {
                advance()
                AtxToken(AtxType.TEXT, token.text)
            }

            BaseType.NEWLINE -> {
                advance()
                AtxToken.NEWLINE
            }

            BaseType.EOF -> AtxToken.EOF
            else -> {
                advance()
                AtxToken.OTHER
            }
        }
    }

    private fun lex(value: String): AtxToken {
        return when (value) {
            "@h" -> lexVA(AtxType.HEADER)
            "@b" -> lexV(AtxType.BOLD)
            "@i" -> lexV(AtxType.ITALIC)
            "@u" -> lexV(AtxType.UNDERLINE)
            "@s" -> lexV(AtxType.STRIKE)
            "@c" -> lexVA(AtxType.COLORED)
            "@f" -> lexVA(AtxType.FUN)
            "@q" -> lexVA(AtxType.QUOTE)
            "@l" -> lexVA(AtxType.LINK)
            "@p" -> lexV(AtxType.PICTURE)
            "@y" -> lexV(AtxType.YOUTUBE)
            "@m" -> lexV(AtxType.MEDIA)
            "@v" -> lexV(AtxType.VIDEO)
            "@t" -> lexVA(AtxType.TABLE)
            "@d" -> lexV(AtxType.DIVIDER)
            "@e" -> lexVA(AtxType.ELEMENT)
            "@n" -> {
                advance()
                AtxToken.NEWLINE
            }

            else -> AtxToken.EOF
        }
    }

    private fun lexVA(type: AtxType): AtxToken {
        advance()
        val value = token()

        if (value.type == BaseType.EOF) {
            return AtxToken.EOF
        }

        return if (value.type == BaseType.VALUE) {
            advance()
            val argument = token()
            if (argument.type == BaseType.ARGUMENT) {
                advance()
                AtxToken(type, value.text, argument.text)
            } else AtxToken(type, value.text)
        } else AtxToken.OTHER
    }


    private fun lexV(type: AtxType): AtxToken {
        advance() //skip symbol
        val token = token()
        if (token.type == BaseType.EOF) {
            return AtxToken.EOF
        }

        return if (token.type == BaseType.VALUE) {
            advance()
            AtxToken(type, token.text)
        } else AtxToken.OTHER
    }

}