package com.naulian.glow_core

private val symbols = listOf(
    '*', '.', '-', '@', '#', '$', '%', '^', '&', '?',
    '|', '\\', '!', '{', '}', '(', ')', ',', ':', '>',
    '<', ';', '+', '=', '[', ']', '/', '\'', '\"',
)

abstract class BaseLexer {
    abstract fun tokenize(): List<Token>
    abstract fun next(): Token
}

class Lexer(private val source: CharSequence) {
    private var cursor: Int = 0
    private fun char() = if (cursor < source.length) source[cursor] else Char.MIN_VALUE

    fun tokenize(): List<Token> {
        val tokens = mutableListOf<Token>()
        var token = nextToken()

        while (token.type != Type.EOF && token.type != Type.ILLEGAL) {
            tokens.add(token)
            token = nextToken()
        }

        return tokens
    }

    fun nextToken(): Token {
        return when (val c = char()) {
            ' ' -> createSpaceToken()
            '\n' -> createToken(Type.NEWLINE, c)
            '*' -> createToken(Type.STAR, c)
            '.' -> createToken(Type.DOT, c)
            '-' -> createToken(Type.DASH, c)
            '@' -> createToken(Type.AT, c)
            '#' -> createToken(Type.HASH, c)
            '$' -> createToken(Type.DOLLAR, c)
            '%' -> createToken(Type.MODULO, c)
            '^' -> createToken(Type.POW, c)
            '&' -> createToken(Type.AND, c)
            '?' -> createToken(Type.Q_MARK, c)
            '|' -> createToken(Type.OR, c)
            '\\' -> createToken(Type.B_SLASH, c)
            '!' -> createToken(Type.BANG, c)
            '{' -> createToken(Type.L_BRACE, c)
            '}' -> createToken(Type.R_BRACE, c)
            '(' -> createToken(Type.L_PAREN, c)
            ')' -> createToken(Type.R_PAREN, c)
            ',' -> createToken(Type.COMMA, c)
            ':' -> createToken(Type.COLON, c)
            '>' -> createToken(Type.GT, c)
            '<' -> createToken(Type.LT, c)
            ';' -> createToken(Type.S_COLON, c)
            '+' -> createToken(Type.PLUS, c)
            '=' -> createToken(Type.EQUAL_TO, c)
            '[' -> createToken(Type.L_BRACKET, c)
            ']' -> createToken(Type.R_BRACKET, c)
            '/' -> createToken(Type.F_SLASH, c)
            '\'' -> createToken(Type.S_QUOTE, c)
            '\"' -> createToken(Type.D_QUOTE, c)
            in '0'..'9' -> createNumberToken()
            in 'a'..'z',
            in 'A'..'Z',
            '_' -> createWordToken()

            Char.MIN_VALUE -> createToken(Type.EOF, c)
            else -> createToken(Type.ILLEGAL, c)
        }
    }

    private fun peek(offset: Int = 1): Char {
        val position = cursor + offset
        return if (position < source.length) source[position] else Char.MIN_VALUE
    }

    private fun createToken(type: Type, value: Char): Token {
        cursor++
        return Token(type, value.toString())
    }

    private fun createSpaceToken(): Token {
        val start = cursor
        while (char() == ' ') {
            cursor++
        }

        val identifier = source.substring(start, cursor)
        return Token(Type.SPACE, identifier)
    }

    private fun createWordToken(): Token {
        val start = cursor
        while (char().isLetter() || char() == '_' || char().isDigit()) {
            cursor++
        }

        val identifier = source.substring(start, cursor)
        return Token(Type.WORD, identifier)
    }

    private fun createNumberToken(): Token {
        val start = cursor

        while (
            char().isDigit() || char() == '_' ||
            char() == 'L' || char() == 'f' ||
            char() == '.'
        ) {
            cursor++
        }
        return Token(Type.NUMBER, source.substring(start, cursor))
    }
}