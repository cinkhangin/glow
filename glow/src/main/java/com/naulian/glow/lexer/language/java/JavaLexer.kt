package com.naulian.glow.lexer.language.java

import com.naulian.glow.lexer.Lexer
import com.naulian.glow.lexer.Token
import com.naulian.glow.lexer.Type
import kotlin.math.min

class JavaLexer(private val input : String) : Lexer {
    private var position: Int = 0
    private val keywords = listOf(
        "abstract", "assert", "boolean", "break", "byte", "case", "catch", "char", "class", "const",
        "continue", "default", "do", "double", "else", "enum", "extends", "final", "finally",
        "float", "for", "goto", "if", "implements", "import", "instanceof", "int", "interface",
        "long", "native", "new", "package", "private", "protected", "public", "return", "short",
        "static", "strictfp", "super", "switch", "synchronized", "this", "throw", "throws",
        "transient", "try", "void", "volatile", "while"
    )

    private val char get() = if (position < input.length) input[position] else Char.MIN_VALUE



    override fun nextToken(): Token {
        if (char.isWhitespace()) {
            return whitespaceToken()
        }

        return when (val c = char) {
            '*' -> createToken(Type.STAR, c.toString())
            '.' -> createToken(Type.DOT, c.toString())
            '-' -> createToken(Type.DASH, c.toString())
            '@' -> createToken(Type.AT, c.toString())
            '#' -> createToken(Type.HASH, c.toString())
            '$' -> createToken(Type.DOLLAR, c.toString())
            '%' -> createToken(Type.MODULO, c.toString())
            '^' -> createToken(Type.POW, c.toString())
            '&' -> createToken(Type.AND, c.toString())
            '?' -> createToken(Type.Q_MARK, c.toString())
            '|' -> createToken(Type.OR, c.toString())
            '\\' -> createToken(Type.B_SCAPE, c.toString())
            '!' -> createToken(Type.BANG, c.toString())
            '{' -> createToken(Type.L_BRACE, c.toString())
            '}' -> createToken(Type.R_BRACE, c.toString())
            '(' -> createToken(Type.L_PAREN, c.toString())
            ')' -> createToken(Type.R_PAREN, c.toString())
            ',' -> createToken(Type.COMMA, c.toString())
            ':' -> createToken(Type.COLON, c.toString())
            '>' -> createToken(Type.GT, c.toString())
            '<' -> createToken(Type.LT, c.toString())
            ';' -> createToken(Type.S_COLON, c.toString())
            '+' -> createToken(Type.PLUS, c.toString())
            '=' -> createToken(Type.EQUAL_TO, c.toString())
            '[' -> createToken(Type.L_BRACKET, c.toString())
            ']' -> createToken(Type.R_BRACKET, c.toString())
            '/' -> {
                when (input[position + 1]) {
                    '/' -> lexSingleLineComment()
                    '*' -> lexMultiLineComment()
                    else -> createToken(Type.F_SLASH, c.toString())
                }
            }

            '\'' -> readChar()
            '\"' -> readString()
            in 'a'..'z', in 'A'..'Z', '_' -> readIdentifier()
            in '0'..'9' -> readNumber()
            Char.MIN_VALUE -> createToken(Type.EOF, c.toString())
            else -> createToken(Type.ILLEGAL, c.toString())
        }
    }

    private fun lexSingleLineComment(): Token {
        val start = position
        do {
            position++
        } while (char != '\n' && char != Char.MIN_VALUE)

        val identifier = input.substring(start, position)
        return Token(Type.S_COMMENT, identifier)
    }

    private fun lexMultiLineComment(): Token {
        val start = position
        do {
            position++
        } while (char != '/' && char != Char.MIN_VALUE)

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
        while (char.isWhitespace()) {
            position++
        }

        val indentifier = input.substring(start, position)
        return Token(Type.SPACE, indentifier)
    }

    private fun readIdentifier(): Token {
        val start = position
        while (char.isLetter() || char == '_' || char.isDigit()) {
            position++
        }

        return when (val identifier = input.substring(start, position)) {
            "int" -> Token(Type.VARIABLE, identifier)
            "string" -> Token(Type.VARIABLE, identifier)
            "long" -> Token(Type.VARIABLE, identifier)
            "boolean" -> Token(Type.VARIABLE, identifier)
            "char" -> Token(Type.VARIABLE, identifier)
            "byte" -> Token(Type.VARIABLE, identifier)
            "float" -> Token(Type.VARIABLE, identifier)
            "short" -> Token(Type.VARIABLE, identifier)
            "class" -> Token(Type.CLASS, identifier)
            in keywords -> Token(Type.KEYWORD, identifier)
            else -> Token(Type.IDENTIFIER, identifier)
        }
    }

    private fun readString(): Token {
        val start = position
        position++
        while (char != '\"' && char != Char.MIN_VALUE) {
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
        while (char != '\'') {
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
            char.isDigit() || char == '_' ||
            char == 'L' || char == 'f' ||
            char == '.'
        ) {
            position++
        }
        return Token(Type.NUMBER, input.substring(start, position))
    }
}