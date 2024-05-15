package com.naulian.glow_compose.kotlin

import com.naulian.glow_core.Token
import com.naulian.glow_core.Type
import kotlin.math.min

class KtLexer(private val input: String) {
    private var position: Int = 0
    private val keywords = listOf(
        "abstract", "annotation", "as", "break", "by", "catch", "class", "companion", "const",
        "constructor", "continue", "crossinline", "data", "do", "else", "enum", "external", "false",
        "final", "finally", "for", "fun", "if", "in", "infix", "init", "inline", "inner",
        "interface", "internal", "is", "it", "lateinit", "noinline", "null", "object", "open",
        "operator", "out", "import", "override", "package", "private", "protected", "public",
        "reified", "return", "sealed", "super", "suspend", "this", "throw", "to", "true", "try",
        "typealias", "typeof", "val", "var", "when", "where", "while"
    )

    private fun currentChar() = if (position < input.length) input[position] else Char.MIN_VALUE

    fun nextToken(): Token {
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
            '\\' -> createToken(Type.B_SCAPE, char)
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
            '/' -> {
                when (input[position + 1]) {
                    '/' -> lexSingleLineComment()
                    '*' -> lexMultiLineComment()
                    else -> createToken(Type.F_SLASH, char)
                }
            }

            '\'' -> readChar()
            '\"' -> readString()
            in 'a'..'z', in 'A'..'Z', '_' -> readIdentifier()
            in '0'..'9' -> readNumber()
            Char.MIN_VALUE -> createToken(Type.EOF, char)
            else -> createToken(Type.ILLEGAL, char)
        }
    }

    private fun lexSingleLineComment(): Token {
        val start = position
        do {
            position++
        } while (currentChar() != '\n' && currentChar() != Char.MIN_VALUE)

        val identifier = input.substring(start, position)
        return Token(Type.S_COMMENT, identifier)
    }

    private fun lexMultiLineComment(): Token {
        val start = position
        do {
            position++
        } while (currentChar() != '/' && currentChar() != Char.MIN_VALUE)
        position++

        val identifier = input.substring(start, position)
        return Token(Type.M_COMMENT, identifier)
    }

    private fun createToken(type: Type, char: Char) = createToken(type, char.toString())

    private fun createToken(type: Type, value: String): Token {
        position++
        return Token(type, value)
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

        return when (val identifier = input.substring(start, position)) {
            "var", "val" -> Token(Type.VARIABLE, identifier)
            "fun" -> Token(Type.FUNCTION, identifier)
            "class" -> Token(Type.CLASS, identifier)
            in keywords -> Token(Type.KEYWORD, identifier)
            else -> Token(Type.IDENTIFIER, identifier)
        }
    }

    private fun readString(): Token {
        val start = position
        position++
        while (currentChar() != '\"' && currentChar() != Char.MIN_VALUE) {
            position++
        }
        position++

        position = min(position, input.length)
        val identifier = input.substring(start, position)
        return Token(Type.STRING, identifier)
    }

    private fun readChar(): Token {
        val start = position
        position++
        while (currentChar() != '\'' && currentChar() != Char.MIN_VALUE) {
            position++
        }
        position++

        position = min(position, input.length)
        val identifier = input.substring(start, position)
        return Token(Type.CHAR, identifier)
    }

    private fun readNumber(): Token {
        val start = position

        while (
            currentChar().isDigit() || currentChar() == '_' ||
            currentChar() == 'L' || currentChar() == 'f' ||
            currentChar() == '.'
        ) {
            position++
        }
        return Token(Type.NUMBER, input.substring(start, position))
    }
}