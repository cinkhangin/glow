package com.naulian.glow_core.lang.kotlin

import com.naulian.glow_core.Token
import com.naulian.glow_core.Type
import kotlin.math.min

class KotlinLexer(private val source: CharSequence) {
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

    private fun currentChar() = if (position < source.length) source[position] else Char.MIN_VALUE

    fun nextToken(): Token {
        return when (val char = currentChar()) {
            ' ' -> whitespaceToken()
            '\n' -> createToken(Type.NEWLINE, "\n")
            '*' -> createToken(Type.ASTERISK, char.toString())
            '.' -> createToken(Type.DOT, char.toString())
            '-' -> createToken(Type.DASH, char.toString())
            '@' -> createToken(Type.AT, char.toString())
            '#' -> createToken(Type.HASH, char.toString())
            '$' -> createToken(Type.DOLLAR, char.toString())
            '%' -> createToken(Type.MODULO, char.toString())
            '^' -> createToken(Type.POW, char.toString())
            '&' -> createToken(Type.AND, char.toString())
            '?' -> createToken(Type.QMARK, char.toString())
            '|' -> createToken(Type.OR, char.toString())
            '\\' -> createToken(Type.ESCAPE, char.toString())
            '!' -> createToken(Type.BANG, char.toString())
            '{' -> createToken(Type.LBRACE, char.toString())
            '}' -> createToken(Type.RBRACE, char.toString())
            '(' -> createToken(Type.LPAREN, char.toString())
            ')' -> createToken(Type.RPAREN, char.toString())
            ',' -> createToken(Type.COMMA, char.toString())
            ':' -> createToken(Type.COLON, char.toString())
            '>' -> createToken(Type.GT, "&gt")
            '<' -> createToken(Type.LT, "&lt")
            ';' -> createToken(Type.SEMICOLON, char.toString())
            '+' -> createToken(Type.PLUS, char.toString())
            '=' -> createToken(Type.ASSIGNMENT, char.toString())
            '[' -> createToken(Type.LBRACK, char.toString())
            ']' -> createToken(Type.RBRACKET, char.toString())
            '/' -> {
                when (source[position + 1]) {
                    '/' -> lexSingleLineComment()
                    '*' -> lexMultiLineComment()
                    else -> createToken(Type.FSLASH, char.toString())
                }
            }

            '\'' -> readChar()
            '\"' -> readString()
            in 'a'..'z', in 'A'..'Z', '_' -> readIdentifier()
            in '0'..'9' -> readNumber()
            Char.MIN_VALUE -> createToken(Type.EOF, char.toString())
            else -> createToken(Type.ILLEGAL, char.toString())
        }
    }

    private fun lexSingleLineComment(): Token {
        val start = position
        do {
            position++
        } while (currentChar() != '\n' && currentChar() != Char.MIN_VALUE)

        val identifier = source.substring(start, position)
        return Token(Type.SCOMMENT, identifier)
    }

    private fun lexMultiLineComment(): Token {
        val start = position
        do {
            position++
        } while (currentChar() != '/' && currentChar() != Char.MIN_VALUE)
        position++

        val identifier = source.substring(start, position)
        return Token(Type.MCOMMENT, identifier)
    }


    private fun createToken(type: Type, value: String): Token {
        position++
        return Token(type, value)
    }

    private fun whitespaceToken(): Token {
        val start = position
        while (currentChar() == ' ') {
            position++
        }

        val identifier = source.substring(start, position)
        return Token(Type.SPACE, identifier)
    }

    private fun readIdentifier(): Token {
        val start = position
        while (currentChar().isLetter() || currentChar() == '_' || currentChar().isDigit()) {
            position++
        }

        return when (val identifier = source.substring(start, position)) {
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

        position = min(position, source.length)
        val identifier = source.substring(start, position)
        return Token(Type.STRING, identifier)
    }

    private fun readChar(): Token {
        val start = position
        position++
        while (currentChar() != '\'' && currentChar() != Char.MIN_VALUE) {
            position++
        }
        position++

        position = min(position, source.length)
        val identifier = source.substring(start, position)
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
        return Token(Type.NUMBER, source.substring(start, position))
    }
}