package com.naulian.glow.tokens

import com.naulian.anhance.logDebug
import com.naulian.glow_core.Token
import com.naulian.glow_core.Type
import kotlin.math.min

object JTokens {

    @Suppress("unused")
    private val TAG = JTokens::class.java.simpleName

    fun tokenize(input: String): List<Token> {
        val lexer = JLexer(input)
        val tokens = mutableListOf<Token>()
        var token = lexer.nextToken()
        var prevToken = Token(Type.NONE)
        var prevIndex = 0

        while (token.type != Type.EOF && token.type != Type.ILLEGAL) {
            //logDebug(TAG, token)

            if (token.type == Type.SPACE) {
                tokens.add(token)
                token = lexer.nextToken()
                continue
            }

            //based on previous
            val modified = when (prevToken.type) {
                Type.ASSIGNMENT -> numberToken(token)
                Type.LPAREN -> argumentToken(token)
                Type.FUNCTION -> token.copy(type = Type.FUNC_NAME)
                Type.CLASS -> token.copy(type = Type.CLASS_NAME)
                Type.COLON -> token.copy(type = Type.DATA_TYPE)
                Type.VARIABLE -> token.copy(type = Type.VAR_NAME)
                Type.DOT -> token.copy(type = Type.PROPERTY)
                else -> token
            }

            //based on next
            when (modified.type) {
                Type.COLON -> {
                    if (prevToken.type == Type.ARGUMENT) {
                        tokens[prevIndex] = prevToken.copy(type = Type.PARAM)
                    }
                }

                Type.LPAREN -> {
                    if (prevToken.type == Type.VAR_NAME) {
                        tokens[prevIndex] = prevToken.copy(type = Type.FUNC_NAME)
                    }

                    tokens.getOrNull(prevIndex - 1)?.let {
                        if (it.type == Type.DOT) {
                            tokens[prevIndex] = prevToken.copy(type = Type.FUNC_CALL)
                        }
                    }
                }

                Type.RBRACKET -> {
                    if (prevToken.type == Type.VAR_NAME) {
                        tokens[prevIndex] = prevToken.copy(type = Type.LBRACK)
                    }
                }

                else -> Unit
            }

            //tracking
            prevIndex = tokens.size
            prevToken = modified

            tokens.add(modified)
            token = lexer.nextToken()
        }

        return tokens
    }

    private fun argumentToken(token: Token): Token {
        return if (token.type != Type.IDENTIFIER) token
        else token.copy(type = Type.ARGUMENT)
    }

    private fun numberToken(token: Token): Token {
        if (token.type != Type.NUMBER) return token
        return if (token.value.contains("L")) token.copy(type = Type.VALUE_LONG)
        else if (token.value.contains("f")) token.copy(type = Type.VALUE_FLOAT)
        else token.copy(type = Type.VALUE_INT)
    }
}

private class JLexer(private val input: String) {
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

    fun nextToken(): Token {
        if (char.isWhitespace()) {
            return whitespaceToken()
        }

        return when (val c = char) {
            '*' -> createToken(Type.ASTERISK, c.toString())
            '.' -> createToken(Type.DOT, c.toString())
            '-' -> createToken(Type.DASH, c.toString())
            '@' -> createToken(Type.AT, c.toString())
            '#' -> createToken(Type.HASH, c.toString())
            '$' -> createToken(Type.DOLLAR, c.toString())
            '%' -> createToken(Type.MODULO, c.toString())
            '^' -> createToken(Type.POW, c.toString())
            '&' -> createToken(Type.AND, c.toString())
            '?' -> createToken(Type.QMARK, c.toString())
            '|' -> createToken(Type.OR, c.toString())
            '\\' -> createToken(Type.ESCAPE, c.toString())
            '!' -> createToken(Type.BANG, c.toString())
            '{' -> createToken(Type.LBRACE, c.toString())
            '}' -> createToken(Type.RBRACE, c.toString())
            '(' -> createToken(Type.LPAREN, c.toString())
            ')' -> createToken(Type.RPAREN, c.toString())
            ',' -> createToken(Type.COMMA, c.toString())
            ':' -> createToken(Type.COLON, c.toString())
            '>' -> createToken(Type.GT, "&gt")
            '<' -> createToken(Type.LT, "&lt")
            ';' -> createToken(Type.SEMICOLON, c.toString())
            '+' -> createToken(Type.PLUS, c.toString())
            '=' -> createToken(Type.ASSIGNMENT, c.toString())
            '[' -> createToken(Type.LBRACK, c.toString())
            ']' -> createToken(Type.RBRACKET, c.toString())
            '/' -> {
                when (input[position + 1]) {
                    '/' -> lexSingleLineComment()
                    '*' -> lexMultiLineComment()
                    else -> createToken(Type.FSLASH, c.toString())
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
        return Token(Type.SCOMMENT, identifier)
    }

    private fun lexMultiLineComment(): Token {
        val start = position
        do {
            position++
        } while (char != '/' && char != Char.MIN_VALUE)

        position++

        val identifier = input.substring(start, position)
        logDebug(identifier)
        return Token(Type.MCOMMENT, identifier)
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