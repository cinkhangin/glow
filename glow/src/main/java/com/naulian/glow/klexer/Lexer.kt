package com.naulian.glow.klexer

class Lexer(private val input: String) {

    private var position: Int = 0
    private val keywords = listOf(
        "abstract", "annotation", "as", "break", "by", "catch", "class", "companion",
        "const", "constructor", "continue", "crossinline", "data", "do", "else",
        "enum", "external", "false", "final", "finally", "for", "fun", "if", "in",
        "infix", "init", "inline", "inner", "interface", "internal", "is", "it",
        "lateinit", "noinline", "null", "object", "open", "operator", "out", "import",
        "override", "package", "private", "protected", "public", "reified",
        "return", "sealed", "super", "suspend", "this", "throw", "to", "true",
        "try", "typealias", "typeof", "val", "var", "when", "where", "while"
    )

    private fun currentChar() = if (position < input.length) input[position] else Char.MIN_VALUE

    fun nextToken(): Token {
        if (currentChar().isWhitespace()) {
            return whitespaceToken()
        }

        return when (val char = currentChar()) {
            '*' -> createToken(TokenType.ASTERISK, char.toString())
            '.' -> createToken(TokenType.DOT, char.toString())
            '-' -> createToken(TokenType.DASH, char.toString())
            '@' -> createToken(TokenType.AT, char.toString())
            '#' -> createToken(TokenType.HASH, char.toString())
            '$' -> createToken(TokenType.DOLLAR, char.toString())
            '%' -> createToken(TokenType.MODULO, char.toString())
            '^' -> createToken(TokenType.POW, char.toString())
            '&' -> createToken(TokenType.AND, char.toString())
            '|' -> createToken(TokenType.OR, char.toString())
            '\\' -> createToken(TokenType.ESCAPE, char.toString())
            '!' -> createToken(TokenType.BANG, char.toString())
            '{' -> createToken(TokenType.LEFT_BRACE, char.toString())
            '}' -> createToken(TokenType.RIGHT_BRACE, char.toString())
            '(' -> createToken(TokenType.LEFT_PARENTHESES, char.toString())
            ')' -> createToken(TokenType.RIGHT_PARENTHESES, char.toString())
            ',' -> createToken(TokenType.COMMA, char.toString())
            ':' -> createToken(TokenType.COLON, char.toString())
            '>' -> createToken(TokenType.GT, char.toString())
            '<' -> createToken(TokenType.LT, char.toString())
            ';' -> createToken(TokenType.SEMICOLON, char.toString())
            '+' -> createToken(TokenType.PLUS, char.toString())
            '=' -> createToken(TokenType.ASSIGNMENT, char.toString())
            '[' -> createToken(TokenType.LEFT_BRACKET, char.toString())
            ']' -> createToken(TokenType.RIGHT_BRACKET, char.toString())
            '/' -> {
                when (input[position + 1]) {
                    '/' -> lexSingleLineComment()
                    '*' -> lexMultiLineComment()
                    else -> createToken(TokenType.SLASH_FORWARD, char.toString())
                }
            }
            '\'' -> readChar()
            '\"' -> readString()
            in 'a'..'z', in 'A'..'Z', '_' -> readIdentifier()
            in '0'..'9' -> readNumber()
            Char.MIN_VALUE -> createToken(TokenType.EOF, char.toString())
            else -> createToken(TokenType.ILLEGAL, char.toString())
        }
    }

    private fun lexSingleLineComment(): Token {
        val start = position
        do {
            position++
        } while (currentChar() != '\n')

        val identifier = input.substring(start, position)
        return Token(TokenType.COMMENT_SINGLE, identifier)
    }

    private fun lexMultiLineComment(): Token {
        val start = position
        do {
            position++
        } while (currentChar() != '/')
        position++

        val identifier = input.substring(start, position)
        return Token(TokenType.COMMENT_MULTI, identifier)
    }


    private fun createToken(type: TokenType, value: String): Token {
        position++
        return Token(type, value)
    }

    private fun whitespaceToken(): Token {
        val start = position
        while (currentChar().isWhitespace()) {
            position++
        }

        val indentifier = input.substring(start, position)
        return Token(TokenType.WHITE_SPACE, indentifier)
    }

    private fun readIdentifier(): Token {
        val start = position
        while (currentChar().isLetter() || currentChar() == '_' || currentChar().isDigit()) {
            position++
        }

        return when (val identifier = input.substring(start, position)) {
            "var" -> Token(TokenType.VAR, identifier)
            "val" -> Token(TokenType.VAL, identifier)
            "fun" -> Token(TokenType.FUNCTION, identifier)
            "class" -> Token(TokenType.CLASS, identifier)
            in keywords -> Token(TokenType.KEYWORD, identifier)
            else -> Token(TokenType.IDENTIFIER, identifier)
        }
    }

    private fun readString(): Token {
        val start = position
        position++
        while (currentChar() != '\"') {
            position++
        }
        position++
        val identifier = input.substring(start, position)
        return Token(TokenType.STRING, identifier)
    }

    private fun readChar(): Token {
        val start = position
        position++
        while (currentChar() != '\'') {
            position++
        }
        position++
        val identifier = input.substring(start, position)
        return Token(TokenType.CHAR, identifier)
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
        return Token(TokenType.NUMBER, input.substring(start, position))
    }
}