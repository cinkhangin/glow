package com.naulian.glow_compose.atx


val SAMPLE2 = """
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
    
    @k 
    k -> for comment aka key
    j -> for colored text aka
    m -> for bold italic aka mark
    g -> for hex color default is gray
    @k
    
    @j colored text @g #222222
    
    @q this is quote text
    
    @f kotlin
    fun main(varargs args: String) {
        println("Hello World!")
    }
    @f
   
    @a http://www.google.com @h Google 
    
    @n
    
    @p http://www.google.com/images/srpr/logo3w.png
    @v https://www.youtube.com/watch?v=dQw4w9WgXcQ
    
    @t
    a, b, c
    @r
    1, 2, 3,
    4, 5, 6,
    7, 8, 9,
    
    @k default is line @k
    @d -
    
    @o item 1,
    @o item 2,
    @e numbered item 1,
    @e numbered item 2,
""".trimIndent()

enum class Atx2Type(val code: String) {
    HEADER("w"),
    SUB_HEADER("x"),
    TITLE("y"),
    SUB_TITLE("z"),

    BOLD("b"),
    ITALIC("i"),
    BOLD_ITALIC("m"),
    UNDERLINE("u"),
    STRIKE("s"),

    QUOTE("q"),
    CODE("f"),
    CODE_BLOCK("c"),
    LINK("a"),
    HYPER("h"),
    PICTURE("p"),
    VIDEO("v"),
    TEXT(""),
    TABLE("t"),
    ROW("r"),
    ELEMENT("e"),
    DIVIDER("d"),
    NEWLINE("n"),
    END("")
}

data class Atx2Token(
    val type: Atx2Type,
    val text: String
) {
    companion object {
        val END = Atx2Token(Atx2Type.END, "")
    }
}

class Atx2Lexer(private val source: CharSequence) {
    private var cursor = 0
    private val char get() = source.getOrElse(cursor) { Char.MIN_VALUE }
    private val peek get() = source.getOrElse(cursor + 1) { Char.MIN_VALUE }
    private fun advance() {
        cursor++
    }

    private fun skipSpace() {
        while (char.isWhitespace()) advance()
    }


    fun next(): Atx2Token {
        skipSpace()
        return when (char) {
            Char.MIN_VALUE -> Atx2Token.END
            '@' -> lexAtx()
            else -> lexText()
        }
    }

    private fun lexAtx(): Atx2Token {
        advance()
        return when (val c = char) {
            'a' -> lexLink()
            'b' -> lexBlock(Atx2Type.BOLD)
            'c' -> lexCode()
            'd' -> lexDivider()
            'e' -> lexDivider() //element
            'i' -> lexBlock(Atx2Type.ITALIC)
            'u' -> lexBlock(Atx2Type.UNDERLINE)
            's' -> lexBlock(Atx2Type.STRIKE)
            else -> {
                advance()
                Atx2Token(Atx2Type.TEXT, "@h$c")
            }
        }
    }

    private fun lexLink(): Atx2Token {
        skipSpace()
        val start = cursor
        var c = char
        while (c != '@' && c != '\n' && c != ' ' && c != Char.MIN_VALUE) {
            advance()
            c = char
        }
        val end = cursor
        val text = source.subSequence(start, end)
        return Atx2Token(Atx2Type.LINK, text.toString())
    }

    private fun lexHeading(type: Atx2Type): Atx2Token {
        skipSpace()
        val start = cursor
        while (char != '\n' && char != Char.MIN_VALUE) {
            advance()
        }
        val end = cursor
        val text = source.subSequence(start, end)
        return Atx2Token(type, text.toString())
    }

    private fun lexText(): Atx2Token {
        val start = cursor
        while (char != '\n' && char != '@' && char != Char.MIN_VALUE) {
            advance()
        }
        val end = cursor
        val text = source.subSequence(start, end)
        return Atx2Token(Atx2Type.TEXT, text.toString())
    }

    private fun lexBlock(type: Atx2Type): Atx2Token {
        skipSpace()
        val start = cursor
        while (char != '@' && char != '\n' && char != Char.MIN_VALUE) {
            advance()
        }

        if (peek.toString() == type.code) {
            advance()
        }

        val end = cursor
        val text = source.subSequence(start, end)
        return Atx2Token(type, text.toString())
    }

    private fun lexCode(): Atx2Token {
        val start1 = cursor
        while (char != '\n' && char != Char.MIN_VALUE) {
            advance()
        }
        val end1 = cursor
        val language = source.subSequence(start1, end1)
        skipSpace()
        val start2 = cursor
        while (char != '@' && char != Char.MIN_VALUE) {
            advance()
        }
        val end2 = cursor
        val code = source.subSequence(start2, end2)
        val text = "$code@separator$language"
        return Atx2Token(Atx2Type.CODE, text)
    }

    private fun lexDivider(): Atx2Token {
        skipSpace()
        val start = cursor
        while (char != '\n' && char != Char.MIN_VALUE) {
            advance()
        }
        val end = cursor
        val text = source.subSequence(start, end)
        return Atx2Token(Atx2Type.DIVIDER, text.toString())
    }
}