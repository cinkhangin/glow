package com.naulian.glow_core.atx


val SAMPLE = """
    @w this is heading 1
    @x this is heading 2
    @y this is heading 3
    @z this is heading 4
    
    @n
    
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
    
    @n
    
    this is a @g #FF0000 colored @g text
    
    @n
    
    @q 
    this is quote text
    - naulian
    @q
    
    @n
    
    @f kotlin
    fun main(varargs args: String) {
        println("Hello World!")
    }
    @f
    
    @f python
    def main():
        print("Hello World!")
        
    
    if __name__ == '__main__':
        main()
    @f
    
    @n
   
    @h Google @a http://www.google.com
    
    @n
    
    @p https://picsum.photos/id/67/300/200
    @v https://www.youtube.com/watch?v=dQw4w9WgXcQ
    
    @n
    
    @t a, b, c
    @r 1, 2, 3, 4, 5, 6, 7, 8, 9
    
    @d -
    
    @l a, b, c, d
    
    @n
    
    @e unordered element 1
    @e unordered element 2
    @o ordered element 1
    @o ordered element 2
""".trimIndent()

enum class AtxKind {
    TEXT,
    LINK,
    OTHER
}

enum class AtxType(val code: Char, val kind: AtxKind = AtxKind.TEXT) {
    HEADER('w', AtxKind.OTHER),
    SUB_HEADER('x', AtxKind.OTHER),
    TITLE('y', AtxKind.OTHER),
    SUB_TITLE('z', AtxKind.OTHER),

    BOLD('b', AtxKind.TEXT),
    ITALIC('i', AtxKind.TEXT),
    BOLD_ITALIC('m', AtxKind.TEXT),
    UNDERLINE('u', AtxKind.TEXT),
    STRIKE('s', AtxKind.TEXT),

    QUOTE('q', AtxKind.OTHER),

    CODE('f', AtxKind.OTHER),
    CODE_BLOCK('c', AtxKind.TEXT),

    LINK('a', AtxKind.LINK),
    HYPER('h', AtxKind.LINK),

    PICTURE('p', AtxKind.OTHER),
    VIDEO('v', AtxKind.OTHER),

    TEXT(' ', AtxKind.TEXT),
    COLORED('g', AtxKind.TEXT),

    LIST('l', AtxKind.OTHER),
    TABLE('t', AtxKind.OTHER),
    ROW('r', AtxKind.OTHER),

    ELEMENT('e', AtxKind.OTHER),
    ORDERED_ELEMENT('o', AtxKind.OTHER),

    JOIN('j', AtxKind.TEXT),
    DIVIDER('d', AtxKind.OTHER),
    NEWLINE('n', AtxKind.TEXT),

    CONSTANT('k', AtxKind.OTHER),
    END(Char.MIN_VALUE, AtxKind.OTHER)
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
        return when (char) {
            Char.MIN_VALUE -> AtxToken.END
            '@' -> lexAtx()
            '\n' -> lexSpace(AtxType.NEWLINE, "\n")
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
        return AtxToken(type, text.toString().trim())
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
        return AtxToken(AtxType.CODE, text.toString().trim())
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

data class AtxNode(
    val kind: AtxKind,
    val children: List<AtxToken>
)

class AtxParser(source: String) {
    private val baseTokens = source.tokenizeAtx()

    private var cursor = 0
    private val token get() = baseTokens.getOrElse(cursor) { AtxToken.END }
    private fun advance(amount: Int = 1) {
        cursor += amount
    }

    private fun next(): AtxToken {
        advance()
        return token
    }

    fun parse(): List<AtxNode> {
        val result = mutableListOf<AtxNode>()
        var currentGroup = mutableListOf<AtxToken>()

        var element = token
        while (element.type != AtxType.END) {
            val lastKind = currentGroup.lastOrNull()?.type?.kind ?: AtxKind.OTHER
            if (currentGroup.isEmpty() || lastKind == element.type.kind) {
                currentGroup.add(element)
            } else {
                val atxNode = AtxNode(lastKind, currentGroup)
                result.add(atxNode)
                currentGroup = mutableListOf(element)
            }
            element = next()
        }

        if (currentGroup.isNotEmpty()) {
            val groupKind = currentGroup.last().type.kind
            val atxNode = AtxNode(groupKind, currentGroup)
            result.add(atxNode)
        }

        return result
    }

}
