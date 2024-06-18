package com.naulian.glow_core.mdx

class MdxPreProcessor(source: String) {
    private val endChar = Char.MIN_VALUE
    private val isNotEndChar get() = char() != endChar
    private val isNotNewLine get() = char() != '\n'

    private val input = source.replace("\n\n", "\n==\n")

    private var cursor = 0
    private fun char() = input.getOrElse(cursor) { endChar }
    private fun advance() = cursor++
    private fun skipWhiteSpace() {
        while (char().isWhitespace()) advance()
    }

    fun process(): List<String> {
        val blockList = mutableListOf<String>()
        while (char() != endChar) {
            skipWhiteSpace()
            val block = when (val char = char()) {
                '"' -> consumeBlock(char)
                '=' -> consumeBlock(char)
                '`' -> consumeBlock(char)
                '[' -> consumeContainer(char, ']')
                '{' -> consumeContainer(char, '}')
                else -> consumeLine()
            }
            blockList.add(block)
        }

        return blockList
    }

    private fun consumeLine(): String {
        val start = cursor
        while (isNotNewLine && isNotEndChar) {
            advance()
        }
        val value = input.subSequence(start, cursor).toString()
        return "${value}$mdxNl"
    }

    private fun consumeBlock(containerChar: Char): String {
        val start = cursor
        var prevChar = char()
        advance()
        while (isNotEndChar) {
            if (char() == containerChar && prevChar != '\\') {
                advance()
                break
            }
            prevChar = char()
            advance()
        }

        val blockValue = input.subSequence(start, cursor)
        if (blockValue.startsWith("`") && blockValue.endsWith("`")) {
            return blockValue.toString().replace("\n==\n", "\n\n")
        }

        return blockValue.toString()
    }

    private fun consumeContainer(openChar: Char, closeChar: Char): String {
        val start = cursor
        var level = 0
        advance()
        while (isNotEndChar) {
            if (char() == openChar) level++
            if (char() == closeChar) {
                if (level == 0) {
                    advance()
                    break
                } else level--
            }
            advance()
        }
        return input.subSequence(start, cursor).toString()
    }
}