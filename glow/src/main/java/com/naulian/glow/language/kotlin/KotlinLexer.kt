package com.naulian.glow.language.kotlin

import com.naulian.glow.Lexer
import com.naulian.glow.Token
import com.naulian.glow.Type
import kotlin.math.min

internal class KotlinLexer(private val input: String) : Lexer {
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

    override fun nextToken(): Token {
        return when (val char = currentChar()) {
            ' ' -> whitespaceToken()
            '\n' -> createToken(Type.NEWLINE, "\n")
            '*' -> createToken(Type.STAR, char.toString())
            '.' -> createToken(Type.DOT, char.toString())
            '-' -> createToken(Type.DASH, char.toString())
            '@' -> createToken(Type.AT, char.toString())
            '#' -> createToken(Type.HASH, char.toString())
            '$' -> createToken(Type.DOLLAR, char.toString())
            '%' -> createToken(Type.MODULO, char.toString())
            '^' -> createToken(Type.POW, char.toString())
            '&' -> createToken(Type.AND, char.toString())
            '?' -> createToken(Type.Q_MARK, char.toString())
            '|' -> createToken(Type.OR, char.toString())
            '\\' -> createToken(Type.B_SCAPE, char.toString())
            '!' -> createToken(Type.BANG, char.toString())
            '{' -> createToken(Type.L_BRACE, char.toString())
            '}' -> createToken(Type.R_BRACE, char.toString())
            '(' -> createToken(Type.L_PAREN, char.toString())
            ')' -> createToken(Type.R_PAREN, char.toString())
            ',' -> createToken(Type.COMMA, char.toString())
            ':' -> createToken(Type.COLON, char.toString())
            '>' -> createToken(Type.GT, char.toString())
            '<' -> createToken(Type.LT, char.toString())
            ';' -> createToken(Type.S_COLON, char.toString())
            '+' -> createToken(Type.PLUS, char.toString())
            '=' -> createToken(Type.EQUAL_TO, char.toString())
            '[' -> createToken(Type.L_BRACKET, char.toString())
            ']' -> createToken(Type.R_BRACKET, char.toString())
            '/' -> {
                when (input[position + 1]) {
                    '/' -> lexSingleLineComment()
                    '*' -> lexMultiLineComment()
                    else -> createToken(Type.F_SLASH, char.toString())
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