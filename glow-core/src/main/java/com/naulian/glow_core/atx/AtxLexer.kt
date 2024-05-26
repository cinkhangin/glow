package com.naulian.glow_core.atx

class AtxLexer(private val source: CharSequence) {
    private var cursor = 0
    private val char get() = source.getOrElse(cursor) { Char.MIN_VALUE }
    private val peek get() = source.getOrElse(cursor + 1) { Char.MIN_VALUE }
    private fun advance(amount: Int = 1) {
        cursor += amount
    }

    private fun skipSpace() {
        while (char.isWhitespace()) advance()
    }

    private fun skipNl() {
        while (char == '\n') advance()
    }


    fun next(): AtxToken {
        skipNl()
        return when (char) {
            Char.MIN_VALUE -> AtxToken.END
            '@' -> lexAtx()
            '\n' -> {
                advance()
                AtxToken(AtxType.TEXT, "\n")
            }

            else -> lexText()
        }
    }

    private fun lexAtx(): AtxToken {
        advance()
        return when (val c = char) {
            'a' -> lexNormal(AtxType.LINK)
            'b' -> lexBlock(AtxType.BOLD)
            'c' -> lexBlock(AtxType.CODE_BLOCK)
            'd' -> lexNormal(AtxType.DIVIDER)
            'e' -> lexNormal(AtxType.ELEMENT)
            'f' -> lexCode()
            'g' -> lexBlock(AtxType.COLORED)
            'h' -> lexNormal(AtxType.HYPER)
            'i' -> lexBlock(AtxType.ITALIC)
            'j' -> {
                advance()
                skipSpace()
                return AtxToken(AtxType.JOIN, " ")
            }

            'k' -> lexNormal(AtxType.CONSTANT)

            'l' -> lexNormal(AtxType.LIST)
            'm' -> lexBlock(AtxType.BOLD_ITALIC)
            'n' -> {
                advance()
                skipSpace()
                AtxToken(AtxType.NEWLINE, "")
            }

            'o' -> lexNormal(AtxType.ORDERED_ELEMENT)

            'p' -> lexNormal(AtxType.PICTURE)
            'q' -> lexBlock(AtxType.QUOTE)

            'r' -> lexNormal(AtxType.ROW)
            's' -> lexBlock(AtxType.STRIKE) // done
            't' -> lexNormal(AtxType.TABLE)
            'u' -> lexBlock(AtxType.UNDERLINE)
            'v' -> lexNormal(AtxType.VIDEO)

            'w' -> lexNormal(AtxType.HEADER)
            'x' -> lexNormal(AtxType.SUB_HEADER)
            'y' -> lexNormal(AtxType.TITLE)
            'z' -> lexNormal(AtxType.SUB_TITLE)
            else -> {
                advance()
                AtxToken(AtxType.TEXT, "$c")
            }
        }
    }

    private fun lexText(): AtxToken {
        val start = cursor
        while (char != '@' && char != Char.MIN_VALUE) {
            advance()
        }
        val end = cursor
        val text = source.subSequence(start, end)
        return AtxToken(AtxType.TEXT, text.toString())
    }

    private fun lexBlock(type: AtxType): AtxToken {
        advance()
        skipSpace()
        val start = cursor
        while (char != '@' && char != Char.MIN_VALUE) {
            advance()
        }

        val end = cursor
        if (peek == type.code) {
            advance(2)
        }

        val text = source.subSequence(start, end)
        return AtxToken(type, text.trimStr())
    }

    private fun lexCode(): AtxToken {
        advance()
        skipSpace()
        val start = cursor

        var stop = false
        while (!stop && char != Char.MIN_VALUE) {
            advance()
            stop = char == '@' && peek == 'f'
        }

        val end = cursor
        advance(2)
        val text = source.subSequence(start, end)
        return AtxToken(AtxType.CODE, text.trimStr())
    }

    private fun lexNormal(type: AtxType): AtxToken {
        advance()
        skipSpace()
        val start = cursor
        while (char != '@' && char != Char.MIN_VALUE) {
            advance()
        }

        val end = cursor
        val text = source.subSequence(start, end)
        return AtxToken(type, text.trimStr())
    }
}