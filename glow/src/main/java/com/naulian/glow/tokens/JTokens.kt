package com.naulian.glow.tokens

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

            if (token.type == Type.WHITE_SPACE) {
                tokens.add(token)
                token = lexer.nextToken()
                continue
            }

            //based on previous
            val modified = when (prevToken.type) {
                Type.ASSIGNMENT -> numberToken(token)
                Type.LEFT_PARENTHESES -> argumentToken(token)
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

                Type.LEFT_PARENTHESES -> {
                    if (prevToken.type == Type.VAR_NAME) {
                        tokens[prevIndex] = prevToken.copy(type = Type.FUNC_NAME)
                    }

                    tokens.getOrNull(prevIndex - 1)?.let {
                        if (it.type == Type.DOT) {
                            tokens[prevIndex] = prevToken.copy(type = Type.FUNC_CALL)
                        }
                    }
                }

                Type.RIGHT_BRACKET -> {
                    if (prevToken.type == Type.VAR_NAME) {
                        tokens[prevIndex] = prevToken.copy(type = Type.LEFT_BRACKET)
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

    private val char = if (position < input.length) input[position] else Char.MIN_VALUE

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
            '?' -> createToken(Type.QUESTION_MARK, c.toString())
            '|' -> createToken(Type.OR, c.toString())
            '\\' -> createToken(Type.ESCAPE, c.toString())
            '!' -> createToken(Type.BANG, c.toString())
            '{' -> createToken(Type.LEFT_BRACE, c.toString())
            '}' -> createToken(Type.RIGHT_BRACE, c.toString())
            '(' -> createToken(Type.LEFT_PARENTHESES, c.toString())
            ')' -> createToken(Type.RIGHT_PARENTHESES, c.toString())
            ',' -> createToken(Type.COMMA, c.toString())
            ':' -> createToken(Type.COLON, c.toString())
            '>' -> createToken(Type.GT, "&gt")
            '<' -> createToken(Type.LT, "&lt")
            ';' -> createToken(Type.SEMICOLON, c.toString())
            '+' -> createToken(Type.PLUS, c.toString())
            '=' -> createToken(Type.ASSIGNMENT, c.toString())
            '[' -> createToken(Type.LEFT_BRACKET, c.toString())
            ']' -> createToken(Type.RIGHT_BRACKET, c.toString())
            '/' -> {
                when (input[position + 1]) {
                    '/' -> lexSingleLineComment()
                    '*' -> lexMultiLineComment()
                    else -> createToken(Type.SLASH_FORWARD, c.toString())
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
        return Token(Type.COMMENT_SINGLE, identifier)
    }

    private fun lexMultiLineComment(): Token {
        val start = position
        do {
            position++
        } while (char != '/' && char != Char.MIN_VALUE)
        position++

        val identifier = input.substring(start, position)
        return Token(Type.COMMENT_MULTI, identifier)
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
        return Token(Type.WHITE_SPACE, indentifier)
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