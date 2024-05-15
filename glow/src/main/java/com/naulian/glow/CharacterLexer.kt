package com.naulian.glow

import com.naulian.glow_core.Token
import com.naulian.glow_core.Type

abstract class CharacterLexer(private val input: String) {
    private var position: Int = 0
    private fun currentChar() = if (position < input.length) input[position] else Char.MIN_VALUE

    abstract fun onCreateToken(tokens: List<Token>): List<Token>

    fun createToken(): List<Token> {
        val tokens = mutableListOf<Token>()
        var token = nextToken()

        while (token.type != Type.EOF && token.type != Type.ILLEGAL) {
            tokens.add(token)
            token = nextToken()
        }

        return onCreateToken(tokens)
    }

    private fun nextToken(): Token {
        return when (val char = currentChar()) {
            ' ' -> whitespaceToken()
            '\n' -> createToken(Type.NEWLINE, "\n")
            '*' -> createToken(Type.STAR, char)
            '.' -> createToken(Type.DOT, char)
            '-' -> createToken(Type.DASH, char)
            '@' -> createToken(Type.AT, char)
            '#' -> createToken(Type.HASH, char)
            '$' -> createToken(Type.DOLLAR, char)
            '%' -> createToken(Type.MODULO, char)
            '^' -> createToken(Type.POW, char)
            '&' -> createToken(Type.AND, char)
            '?' -> createToken(Type.Q_MARK, char)
            '|' -> createToken(Type.OR, char)
            '\\' -> createToken(Type.F_SLASH, char)
            '!' -> createToken(Type.BANG, char)
            '{' -> createToken(Type.L_BRACE, char)
            '}' -> createToken(Type.R_BRACE, char)
            '(' -> createToken(Type.L_PAREN, char)
            ')' -> createToken(Type.R_PAREN, char)
            ',' -> createToken(Type.COMMA, char)
            ':' -> createToken(Type.COLON, char)
            '>' -> createToken(Type.GT, char)
            '<' -> createToken(Type.LT, char)
            ';' -> createToken(Type.S_COLON, char)
            '+' -> createToken(Type.PLUS, char)
            '=' -> createToken(Type.EQUAL_TO, char)
            '[' -> createToken(Type.L_BRACKET, char)
            ']' -> createToken(Type.R_BRACKET, char)
            '/' -> createToken(Type.B_SLASH, char)
            '\'' -> createToken(Type.S_QUOTE, char)
            '\"' -> createToken(Type.D_QUOTE, char)
            in 'a'..'z', in 'A'..'Z', '_' -> readIdentifier()
            in '0'..'9' -> readNumber()
            Char.MIN_VALUE -> createToken(Type.EOF, char)
            else -> createToken(Type.ILLEGAL, char)
        }
    }

    private fun createToken(type: Type, value: String): Token {
        position++
        return Token(type, value)
    }

    private fun createToken(type: Type, char: Char): Token {
        return createToken(type, char.toString())
    }

    private fun whitespaceToken(): Token {
        val start = position
        while (currentChar() == ' ') {
            position++
        }

        val identifier = input.substring(start, position)
        return Token(Type.SPACE, identifier)
    }

    private fun readIdentifier(): Token {
        val start = position
        while (currentChar().isLetter() || currentChar() == '_' || currentChar().isDigit()) {
            position++
        }
        val identifier = input.substring(start, position)
        return Token(Type.WORD, identifier)
    }


    private fun readNumber(): Token {
        val start = position

        var char = currentChar()
        while (
            char.isDigit() || char == '_' ||
            char == 'L' || char == 'f' ||
            char == 'F' || char == '.'
        ) {
            position++
            char = currentChar()
        }

        val number = input.substring(start, position)
        return Token(Type.NUMBER, number)
    }
}