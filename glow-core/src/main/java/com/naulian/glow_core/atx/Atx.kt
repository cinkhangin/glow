package com.naulian.glow_core.atx

import android.util.Log

class BaseLexer(private val source: CharSequence) {
    private var cursor: Int = 0
    private fun char() = source.getOrElse(cursor) { Char.MIN_VALUE }
    private val tokens = mutableListOf<String>()

    fun tokenize(): List<String> {
        cursor = 0
        while (char() != Char.MIN_VALUE) {
            val t = advance()
            tokens.add(t)
        }
        return tokens
    }

    private fun advance(): String {
        return when (val c = char()) {
            '@' -> createWordToken()
            in 'a'..'z' -> createWordToken()
            in 'A'..'Z' -> createWordToken()
            in '0'..'9' -> createWordToken()
            else -> createToken(c)
        }
    }

    private fun createToken(char: Char): String {
        cursor++
        return char.toString()
    }

    private fun createWordToken(): String {
        val start = cursor
        var c = char()
        while (c != ' ' && c != '\n' && c != Char.MIN_VALUE) {
            cursor++
            c = char()
        }
        val word = source.subSequence(start, cursor)
        return word.toString()
    }
}

class AtxLexer(source: String) {
    private val baseLexer = BaseLexer(source)
    private val words = baseLexer.tokenize()
    private var cursor: Int = 0
    private fun current() = words.getOrElse(cursor) { "@eof" }
    private fun log(any: Any) {
        Log.i("AtxLexer", any.toString())
    }

    private fun skipBreak() {
        var symbol = current()
        while (symbol == "@br" || symbol == " " || symbol == "\n") {
            cursor++
            symbol = current()
        }
    }

    fun advance(): AtxToken {
        skipBreak()
        val token = when (val symbol = current()) {
            "@header" -> createToken(AtxType.HEAD, symbol, "@size")
            "@text" -> createToken(AtxType.TEXT, symbol, "@style")
            "@link" -> createToken(AtxType.LINK, symbol, "@hyper")
            "@code" -> createToken(AtxType.CODE, symbol, "@lang")
            "@quote" -> createToken(AtxType.QUOTE, symbol, "@author")
            "@space" -> createToken(AtxType.SPACE, symbol, "@type")
            "@table" -> createToken(AtxType.TABLE, symbol, "@items")
            "@list" -> createToken(AtxType.LIST, symbol, "@type")
            "@image" -> createToken(AtxType.IMAGE, symbol)
            "@video" -> createToken(AtxType.VIDEO, symbol)
            "@divider" -> createToken(AtxType.DIVIDER, symbol)
            else -> AtxToken(AtxType.EOF, symbol)
        }

        return token
    }

    private fun createToken(type: AtxType, symbol: String): AtxToken {
        val start = cursor
        while (current() != "@br" && current() != "@eof") {
            cursor++
        }

        val symbols = words.subList(start, cursor)
        val value = symbols.joinToString(" ")
            .removePrefix(symbol)
            .trim()
        return AtxToken(type, value)
    }

    private fun createToken(type: AtxType, symbol: String, key: String): AtxToken {
        val start = cursor
        while (current() != "@br" && current() != "@eof") {
            cursor++
        }

        val symbols = words.subList(start, cursor)
        val value = symbols.joinToString(" ")
            .removePrefix(symbol)
            .trim()

        if (value.contains(key)) {
            val split = value.split(key)
            if (split.size >= 2) {
                val text = split[0].trim()
                val param = split[1].trim()
                return AtxToken(type, text, param)
            }
        }

        return AtxToken(type, value)
    }
}

data class AtxToken(val type: AtxType, val text: String, val param: String = "")

enum class AtxType {
    HEAD, TEXT, QUOTE,
    CODE, LINK, SPACE,
    IMAGE, VIDEO, TABLE,
    LIST, DIVIDER, EOF
}

// sample.atx
val SAMPLE = """
    @header this is heading @size 1 @br
    @header this is heading @size 2 @br
    @header this is heading @size 3 @br
    @header this is heading @size 4 @br

    @text this is bold text @style bold @br
    @text this is italic text @style italic @br
    @text this is underline text @style underline @br
    @text this is strikethrough text @style strikethrough @br
    
    @quote this is quote text @author name @br
    
    @code
    fun main(args: Array<String>) {
        println("Hello World!")
    }
    @lang kotlin
    @br
   
    @link http://www.google.com 
    @hyper Google 
    @br
    
    @space 20 @type vertical @br
    
    @image http://www.google.com/images/srpr/logo3w.png @br
    @video https://www.youtube.com/watch?v=dQw4w9WgXcQ @br
    
    @table 
    a, b, c
    @items
    1, 2, 3,
    4, 5, 6,
    7, 8, 9,
    @br
    
    @divider 1 @br
    
    @list
    item 1,
    item 2,
    item 3,
    @type number
    @br
""".trimIndent()