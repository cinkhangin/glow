package com.naulian.glow.tokens

import com.naulian.anhance.logDebug

object PTokens {
    private val TAG = PTokens::class.java.simpleName

    fun tokenize(input: String): List<Token> {
        val lexer = PLexer(input)
        val tokens = mutableListOf<Token>()
        var token = lexer.nextToken()
        while (token.type != Type.EOF && token.type != Type.ILLEGAL) {
            logDebug(TAG, token)
            tokens.add(token)
            token = lexer.nextToken()
        }

        return audit1(tokens)
    }

    private fun audit1(list: List<Token>): List<Token> {
        if (list.size < 2) return list

        val tokens = mutableListOf(
            list[0],
            list[1]
        )

        for (index in 2 until list.size) {
            val prev = list[index - 2]
            val token = list[index]

            val modified = when (prev.type) {
                Type.ASSIGNMENT -> numberToken(token)
                Type.LEFT_PARENTHESES -> argumentToken(token)
                Type.FUNCTION -> token.copy(type = Type.FUNC_NAME)
                Type.CLASS -> token.copy(type = Type.CLASS_NAME)
                Type.COLON -> token.copy(type = Type.DATA_TYPE)
                Type.VARIABLE -> token.copy(type = Type.VAR_NAME)
                else -> token
            }

            tokens.add(modified)
        }
        return audit2(tokens)
    }

    private fun argumentToken(token: Token): Token {
        return if(token.type != Type.IDENTIFIER)  token
        else token.copy(type = Type.ARGUMENT)
    }

    private fun numberToken(token: Token): Token {
        if (token.type != Type.NUMBER) return token
        return if (token.value.contains("L")) token.copy(type = Type.VALUE_LONG)
        else if (token.value.contains("f")) token.copy(type = Type.VALUE_FLOAT)
        else token.copy(type = Type.VALUE_INT)
    }

    private fun audit2(listRaw: List<Token>): List<Token> {
        val list = listRaw.reversed()
        if (list.size < 2) return list

        val tokens = mutableListOf(
            list[0],
            list[1]
        )

        for (index in 2 until list.size) {
            val prev = list[index - 2]
            val token = list[index]

            val modified = when (prev.type) {
                Type.COLON -> if (token.type == Type.ARGUMENT) token.copy(
                    type = Type.PARAM
                )
                else token

                else -> token
            }
            tokens.add(modified)
        }
        return tokens.reversed()
    }
}

private class PLexer(private val input: String) {
    private var position: Int = 0
    private val keywords = listOf(
        "and", "as", "assert", "async", "await", "break", "class", "continue", "def", "del", "elif",
        "else", "except", "False", "finally", "for", "from", "global", "if", "import", "in", "is",
        "lambda", "None", "nonlocal", "not", "or", "pass", "raise", "return", "True", "try",
        "while", "with", "yield"
    )

    private fun currentChar() = if (position < input.length) input[position] else Char.MIN_VALUE

    fun nextToken(): Token {
        if (currentChar().isWhitespace()) {
            return whitespaceToken()
        }

        return when (val char = currentChar()) {
            '*' -> createToken(Type.ASTERISK, char.toString())
            '.' -> createToken(Type.DOT, char.toString())
            '-' -> createToken(Type.DASH, char.toString())
            '@' -> createToken(Type.AT, char.toString())
            '$' -> createToken(Type.DOLLAR, char.toString())
            '%' -> createToken(Type.MODULO, char.toString())
            '^' -> createToken(Type.POW, char.toString())
            '&' -> createToken(Type.AND, char.toString())
            '?' -> createToken(Type.QUESTION_MARK, char.toString())
            '|' -> createToken(Type.OR, char.toString())
            '\\' -> createToken(Type.ESCAPE, char.toString())
            '!' -> createToken(Type.BANG, char.toString())
            '{' -> createToken(Type.LEFT_BRACE, char.toString())
            '}' -> createToken(Type.RIGHT_BRACE, char.toString())
            '(' -> createToken(Type.LEFT_PARENTHESES, char.toString())
            ')' -> createToken(Type.RIGHT_PARENTHESES, char.toString())
            ',' -> createToken(Type.COMMA, char.toString())
            ':' -> createToken(Type.COLON, char.toString())
            '>' -> createToken(Type.GT, char.toString())
            '<' -> createToken(Type.LT, char.toString())
            ';' -> createToken(Type.SEMICOLON, char.toString())
            '+' -> createToken(Type.PLUS, char.toString())
            '=' -> createToken(Type.ASSIGNMENT, char.toString())
            '[' -> createToken(Type.LEFT_BRACKET, char.toString())
            ']' -> createToken(Type.RIGHT_BRACKET, char.toString())
            '#' ->  readComments()
            '\'' -> readSingleQString()
            '\"' -> readDoubleQString()
            in 'a'..'z', in 'A'..'Z', '_' -> readIdentifier()
            in '0'..'9' -> readNumber()
            Char.MIN_VALUE -> createToken(Type.EOF, char.toString())
            else -> createToken(Type.ILLEGAL, char.toString())
        }
    }

    private fun readComments(): Token {
        val start = position
        do {
            position++
        } while (currentChar() != '\n')

        val identifier = input.substring(start, position)
        return Token(Type.COMMENT_SINGLE, identifier)
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
        return Token(Type.WHITE_SPACE, indentifier)
    }

    private fun readIdentifier(): Token {
        val start = position
        while (currentChar().isLetter() || currentChar() == '_' || currentChar().isDigit()) {
            position++
        }

        return when (val identifier = input.substring(start, position)) {
            "def" -> Token(Type.FUNCTION, identifier)
            "class" -> Token(Type.CLASS, identifier)
            in keywords -> Token(Type.KEYWORD, identifier)
            else -> Token(Type.IDENTIFIER, identifier)
        }
    }

    private fun readDoubleQString(): Token {
        val start = position
        position++
        while (currentChar() != '\"') {
            position++
        }
        position++
        val identifier = input.substring(start, position)
        return Token(Type.STRING, identifier)
    }

    private fun readSingleQString(): Token {
        val start = position
        position++
        while (currentChar() != '\'') {
            position++
        }
        position++
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