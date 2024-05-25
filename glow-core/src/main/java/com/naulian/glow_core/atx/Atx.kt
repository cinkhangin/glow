package com.naulian.glow_core.atx


val SAMPLE = """
    @w this is heading 1
    @x this is heading 2
    @y this is heading 3
    @z this is heading 4

    this is @b bold @b text
    this is @i italic @i text
    this is @u underline @u text
    this is @s strikethrough @s text
    this is @c code @c text
    this is @m italic bold @m text
    
    @f comment
    j -> join (replace newline character with space)
    m -> for bold italic (aka mark)
    g -> for hex colored text (default is gray)
    k -> k is used for constant
         there are multiple types in constant
         example
         @k millis will be replaced with the current millis
         @k data will be replaced with the current date
         @k time will be replaced with the current time
         @k random will be replaced with a random number
    @f
    
    @g colored text #222222 @g
    
    @q 
    this is quote text
    @q
    
    @f kotlin
    fun main(varargs args: String) {
        println("Hello World!")
    }
    @f
   
    @a http://www.google.com @h Google 
    
    @n
    
    @p http://www.google.com/images/srpr/logo3w.png
    @v https://www.youtube.com/watch?v=dQw4w9WgXcQ
    
    @t a, b, c
    @r 1, 2, 3, 4, 5, 6, 7, 8, 9
    
    @d -
    
    @l a, b, c, d
    
    @e unordered element 1
    @e unordered element 2
    @o ordered element 1
    @o ordered element 2
""".trimIndent()

enum class AtxType(val code: Char) {
    HEADER('w'),
    SUB_HEADER('x'),
    TITLE('y'),
    SUB_TITLE('z'),

    BOLD('b'),
    ITALIC('i'),
    BOLD_ITALIC('m'),
    UNDERLINE('u'),
    STRIKE('s'),

    QUOTE('q'),

    CODE('f'),
    CODE_BLOCK('c'),

    LINK('a'),
    HYPER('h'),

    PICTURE('p'),
    VIDEO('v'),

    TEXT(' '),
    COLORED('g'),

    LIST('l'),
    TABLE('t'),
    ROW('r'),

    ELEMENT('e'),
    ORDERED_ELEMENT('o'),

    JOIN('j'),
    DIVIDER('d'),
    NEWLINE('n'),

    CONSTANT('k'),
    END(Char.MIN_VALUE)
}

data class AtxToken(
    val type: AtxType,
    val text: String
) {
    companion object {
        val END = AtxToken(AtxType.END, "")
    }
}

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


    fun next(): AtxToken {
        skipSpace()
        return when (char) {
            Char.MIN_VALUE -> AtxToken.END
            '@' -> lexAtx()
            else -> lexText()
        }
    }

    private fun lexAtx(): AtxToken {
        advance()
        return when (val c = char) {
            'a' -> lexLine(AtxType.LINK)
            'b' -> lexBlock(AtxType.BOLD)
            'c' -> lexBlock(AtxType.CODE_BLOCK)
            'd' -> lexLine(AtxType.DIVIDER)
            'e' -> lexLine(AtxType.ELEMENT)
            'f' -> lexCode()
            'g' -> lexBlock(AtxType.COLORED)
            'h' -> lexLine(AtxType.HYPER)
            'i' -> lexBlock(AtxType.ITALIC)
            'j' -> lexSpace(AtxType.JOIN, " ")
            'k' -> lexLine(AtxType.CONSTANT)

            'l' -> lexLine(AtxType.LIST)
            'm' -> lexBlock(AtxType.BOLD_ITALIC)
            'n' -> lexSpace(AtxType.NEWLINE, "\n")
            'o' -> lexLine(AtxType.ORDERED_ELEMENT)

            'p' -> lexLine(AtxType.PICTURE)
            'q' -> lexBlock(AtxType.QUOTE)

            'r' -> lexLine(AtxType.ROW)
            's' -> lexBlock(AtxType.STRIKE) // done
            't' -> lexLine(AtxType.TABLE)
            'u' -> lexBlock(AtxType.UNDERLINE)
            'v' -> lexLine(AtxType.VIDEO)

            'w' -> lexLine(AtxType.HEADER)
            'x' -> lexLine(AtxType.SUB_HEADER)
            'y' -> lexLine(AtxType.TITLE)
            'z' -> lexLine(AtxType.SUB_TITLE)
            else -> {
                advance()
                AtxToken(AtxType.TEXT, "$c")
            }
        }
    }

    private fun lexText(): AtxToken {
        val start = cursor
        while (char != '\n' && char != '@' && char != Char.MIN_VALUE) {
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
        return AtxToken(type, text.toString())
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
        return AtxToken(AtxType.CODE, text.toString())
    }

    private fun lexLine(type: AtxType): AtxToken {
        advance()
        skipSpace()
        val start = cursor
        while (char != '\n' && char != '@' && char != Char.MIN_VALUE) {
            advance()
        }

        val end = cursor
        val text = source.subSequence(start, end)
        return AtxToken(type, text.toString())
    }

    private fun lexSpace(type: AtxType, value: String): AtxToken {
        advance()
        skipSpace()
        return AtxToken(type, value)
    }
}

fun String.tokenizeAtx(): List<AtxToken> {
    val lexer = AtxLexer(this)
    val tokens = mutableListOf<AtxToken>()
    var current = lexer.next()
    while (current.type != AtxType.END) {
        tokens.add(current)
        current = lexer.next()
    }
    return tokens
}
