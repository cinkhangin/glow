package com.naulian.glow.lexer.language.javascript

import com.naulian.glow.lexer.Lexer
import com.naulian.glow.lexer.Token
import com.naulian.glow.lexer.Type
import kotlin.math.min

internal class JavascriptLexer(private val input: String)  : Lexer{
    private var position: Int = 0
    private val keywords = listOf(
        "await", "break", "case", "catch", "class", "const", "continue", "debugger", "default",
        "delete", "do", "else", "export", "extends", "false", "finally", "for", "function", "if",
        "import", "in", "instanceof", "new", "null", "return", "super", "switch", "this", "throw",
        "true", "try", "typeof", "var", "void", "while", "with", "yield"
    )

    private fun currentChar() = if (position < input.length) input[position] else Char.MIN_VALUE

    override fun nextToken(): Token {
        if (currentChar().isWhitespace()) {
            return whitespaceToken()
        }

        return when (val char = currentChar()) {
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

            '\'' -> readString1()
            '\"' -> readString2()
            '`' -> readString()
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
        while (currentChar().isWhitespace()) {
            position++
        }

        val indentifier = input.substring(start, position)
        return Token(Type.SPACE, indentifier)
    }

    private fun readIdentifier(): Token {
        val start = position
        while (currentChar().isLetter() || currentChar() == '_' || currentChar().isDigit()) {
            position++
        }

        return when (val identifier = input.substring(start, position)) {
            "var" -> Token(Type.VARIABLE, identifier)
            "const" -> Token(Type.VARIABLE, identifier)
            "let" -> Token(Type.VARIABLE, identifier)
            "function" -> Token(Type.FUNCTION, identifier)
            "class" -> Token(Type.CLASS, identifier)
            in keywords -> Token(Type.KEYWORD, identifier)
            else -> Token(Type.IDENTIFIER, identifier)
        }
    }

    private fun readString1(): Token {
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

    private fun readString2(): Token {
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

    private fun readString(): Token {
        val start = position
        position++
        while (currentChar() != '`' && currentChar() != Char.MIN_VALUE) {
            position++
        }
        position++

        position = min(position, input.length)
        val identifier = input.substring(start, position)
        return Token(Type.STRING, identifier)
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