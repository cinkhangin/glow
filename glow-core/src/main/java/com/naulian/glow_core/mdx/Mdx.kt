package com.naulian.glow_core.mdx

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

const val mdxNewline = "mdx.newline"
fun CharSequence.str(): String = toString().trim()
fun CharSequence.str(textToReplace: String) = str().replace(textToReplace, "")

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
        return "${value}$mdxNewline"
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
        return input.subSequence(start, cursor).toString()
    }

    @Suppress("SameParameterValue")
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

enum class MdxType {
    //Headers
    H1, H2, H3, H4, H5, H6,
    TEXT, BOLD, ITALIC, UNDERLINE, STRIKE,
    QUOTE, LINK, HYPER_LINK, CODE, DATETIME,
    TABLE, ELEMENT, ESCAPE, COLORED,
    IMAGE, VIDEO, YOUTUBE,
    WHITESPACE, DIVIDER, EOF,
}

data class MdxToken(
    val type: MdxType,
    val text: String,
) {
    companion object {
        val EOF = MdxToken(MdxType.EOF, "")
    }

    fun isBlackText() = type == MdxType.TEXT && text.isBlank()
    fun getHyperLink(): Pair<String, String> {
        if (text.contains("@")) {
            val index = text.indexOf("@")
            val hyper = text.take(index)
            val link = text.replace("$hyper@", "")

            return hyper to link
        }
        return "" to text
    }

    fun getTableItemPairs(): Pair<List<String>, List<List<String>>> {
        val lines = text.split("\n")

        if (lines.isEmpty()) {
            return emptyList<String>() to emptyList()
        }

        var columns = emptyList<String>()
        if (lines.first().isNotBlank()) {
            columns = lines.first().split("|")
                .map { it.trim() }
        }

        if (lines.size > 1) {
            val rows = lines.drop(1)
            return columns to rows.map { row ->
                row.split("|").map { it.trim() }
            }
        }
        return columns to emptyList()
    }

    fun getTextColorPair(): Pair<String, String> {
        if (text.contains("#")) {
            val index = text.indexOf("#")
            val value = text.take(index)
            val hexColor = text.drop(index).trim()
            return value to hexColor
        }
        return text to "#222222"
    }

    fun getLangCodePair(): Pair<String, String> {
        if (text.contains("\n")) {
            val index = text.indexOf("\n")
            val lang = text.take(index)
            val code = text.drop(index).trim()

            if (lang.contains('.')) {
                return lang.replace(".", "") to code
            }
        }

        return "txt" to text
    }
}


object MdxTokenizer {

    fun tokenize(input: String): List<MdxToken> {
        val preprocess = MdxPreProcessor(input).process()
        val tokens = mutableListOf<MdxToken>()

        preprocess.forEach {
            val lexer = MdxLexer(it)
            var current = lexer.next()
            while (current.type != MdxType.EOF) {
                tokens.add(current)
                current = lexer.next()
            }
        }

        return tokens
    }
}

class MdxLexer(input: String) {
    private var cursor = 0
    private val source = input.replace(" ~\n", " ")
    private val endChar = Char.MIN_VALUE
    private val isNotEndChar get() = char() != endChar
    private val isNotNewLine get() = char() != '\n'
    private val symbolChars = "\"</>&{%}[~]\\(-)_\n"
    private val charIsNotSymbol get() = char() !in symbolChars
    private fun char() = source.getOrElse(cursor) { Char.MIN_VALUE }

    private fun skipWhiteSpace() {
        while (char().isWhitespace()) advance()
    }

    private fun skipNewline() {
        while (char() == '\n') advance()
    }

    private fun advance(amount: Int = 1) {
        cursor += amount
    }

    fun next(): MdxToken {
        skipNewline()
        return when (val char = char()) {
            '&' -> createBlockToken(MdxType.BOLD, char)
            '/' -> createBlockToken(MdxType.ITALIC, char)
            '_' -> createBlockToken(MdxType.UNDERLINE, char)
            '"' -> createBlockToken(MdxType.QUOTE, char)
            '=' -> createBlockToken(MdxType.DIVIDER, char)
            '`' -> createBlockToken(MdxType.ESCAPE, char)
            '~' -> createBlockToken(MdxType.STRIKE, char)
            '%' -> createBlockToken(MdxType.DATETIME, char)
            '[' -> createTableToken()
            '<' -> createColoredToken()
            '#' -> createHeaderToken()
            '{' -> createCodeToken()
            '*' -> createElementToken()
            '(' -> createLinkToken()
            '\\' -> createEscapedToken()
            in symbolChars -> {
                advance()
                MdxToken(MdxType.TEXT, char.toString())
            }

            Char.MIN_VALUE -> MdxToken.EOF
            else -> createTextToken()
        }
    }

    private fun createEscapedToken(): MdxToken {
        advance()
        val escapedChar = char()
        return if (escapedChar in symbolChars) {
            advance()
            MdxToken(MdxType.TEXT, escapedChar.toString())
        } else createTextToken()
    }

    private fun createCodeToken(): MdxToken {
        advance() //skip opening bracket
        val start = cursor
        var level = 0
        while (isNotEndChar) {
            if (char() == '{') {
                level++
            }
            if (char() == '}') {
                if (level == 0) {
                    break
                } else level--
            }
            advance()
        }
        val end = cursor
        advance() //skip closing bracket
        val value = source.subSequence(start, end)

        var code = value.str()
        mdxAdhocMap.forEach {
            code = code.replace(it.key, it.value)
        }
        return MdxToken(MdxType.CODE, code)
    }

    private fun createLinkToken(): MdxToken {
        advance() //skip opening parenthesis
        val start = cursor
        var level = 0
        while (isNotEndChar) {
            if (char() == '(') {
                level++
            }
            if (char() == ')') {
                if (level == 0) {
                    break
                } else level--
            }
            advance()
        }

        val value = source.subSequence(start, cursor)
        advance() //skip closing parenthesis

        if (value.contains("@")) {
            val index = value.indexOf("@")
            val hyper = value.take(index)
            val link = value.str().replace("$hyper@", "")

            return when (hyper) {
                "img" -> MdxToken(MdxType.IMAGE, link)
                "ytb" -> MdxToken(MdxType.YOUTUBE, link)
                "vid" -> MdxToken(MdxType.VIDEO, link)
                else -> MdxToken(MdxType.HYPER_LINK, value.str())
            }
        }

        return MdxToken(MdxType.LINK, value.str())
    }

    private fun createElementToken(): MdxToken {
        advance()
        skipWhiteSpace()
        val start = cursor
        while (charIsNotSymbol && isNotEndChar) {
            advance()
        }
        val text = source.subSequence(start, cursor).toString()
            .replace(mdxNewline, "\n")
        return MdxToken(MdxType.ELEMENT, text)
    }

    private fun createTextToken(): MdxToken {
        val start = cursor
        while (charIsNotSymbol && isNotEndChar) {
            advance()
        }
        val value = source.subSequence(start, cursor).toString()
            .replace(mdxNewline, "\n")

        var text = value
        mdxAdhocMap.forEach {
            text = text.replace(it.key, it.value)
        }
        return MdxToken(MdxType.TEXT, text)
    }

    private fun createBlockToken(type: MdxType, char: Char): MdxToken {
        advance() //skip the opening char
        val start = cursor
        var prevChar = char()
        while (!(char() == char && prevChar != '\\') && isNotEndChar) {
            prevChar = char()
            advance()
        }

        val valueText = source.subSequence(start, cursor).str()
        advance() //skip the closing char

        return when (type) {
            MdxType.DATETIME -> {
                val value = formattedDateTime(valueText)
                MdxToken(type, value)
            }

            else -> MdxToken(type, valueText)
        }
    }

    private fun createHeaderToken(): MdxToken {
        advance()
        val type = when (char()) {
            '1' -> MdxType.H1
            '2' -> MdxType.H2
            '3' -> MdxType.H3
            '4' -> MdxType.H4
            '5' -> MdxType.H5
            '6' -> MdxType.H6
            Char.MIN_VALUE -> MdxType.EOF
            else -> MdxType.TEXT
        }
        advance()
        skipWhiteSpace()
        val start = cursor
        while (isNotNewLine && isNotEndChar) {
            advance()
        }
        val text = source.subSequence(start, cursor)
        return MdxToken(type, text.str(mdxNewline))
    }

    private fun createTableToken(): MdxToken {
        advance() //skip opening bracket
        val start = cursor
        var level = 0
        while (isNotEndChar) {
            if (char() == '[') {
                level++
            }
            if (char() == ']') {
                if (level == 0) {
                    break
                } else level--
            }
            advance()
        }
        val end = cursor
        advance() //skip closing bracket
        val value = source.subSequence(start, end)
        return MdxToken(MdxType.TABLE, value.str())
    }

    private fun createColoredToken(): MdxToken {
        advance() //skip opening bracket
        val start = cursor
        var level = 0
        while (isNotEndChar) {
            if (char() == '<') {
                level++
            }
            if (char() == '>') {
                if (level == 0) {
                    break
                } else level--
            }
            advance()
        }
        val end = cursor
        advance() //skip closing bracket
        val value = source.subSequence(start, end).str()
        return MdxToken(MdxType.COLORED, value)
    }
}

data class MdxComponentGroup(
    val type: MdxComponentType,
    val children: List<MdxToken>,
)

enum class MdxComponentType {
    TEXT, OTHER,
}

object MdxParser {

    fun parse(source: String): List<MdxComponentGroup> {
        val generatedTokens = MdxTokenizer.tokenize(source)
        val tokenGroups = mutableListOf<MdxComponentGroup>()
        var currentGroup = mutableListOf<MdxToken>()

        for (token in generatedTokens) {
            val lastType = currentGroup.lastOrNull()?.getComponentType() ?: MdxComponentType.OTHER
            val type = token.getComponentType()

            if (currentGroup.isEmpty() || lastType == type) {
                currentGroup.add(token)
            } else {
                if (currentGroup.last().isBlackText()) {
                    currentGroup.removeLast()
                }

                val handled = handleTokens(currentGroup)
                if (handled.isNotEmpty()) {
                    val atxGroup = MdxComponentGroup(lastType, handled)
                    tokenGroups.add(atxGroup)
                }

                currentGroup = mutableListOf(token)
            }
        }

        //add the last group
        val type = currentGroup.getComponentType()
        val handled = handleTokens(currentGroup)
        if (handled.isNotEmpty()) {
            val atxGroup = MdxComponentGroup(type, handled)
            tokenGroups.add(atxGroup)
        }
        return tokenGroups
    }

    private fun handleTokens(group: List<MdxToken>): List<MdxToken> {
        if (group.isEmpty()) return emptyList()

        val currentGroup = group.toMutableList()
        if (currentGroup.size == 1) {
            val last = currentGroup.removeLast()
            val updatedLast = last.copy(text = last.text.trim())
            currentGroup.add(updatedLast)
        } else {
            val last = currentGroup.removeLast()
            val updatedLast = last.copy(text = last.text.trimEnd())
            currentGroup.add(updatedLast)
        }

        if (currentGroup.first().isBlackText()) {
            currentGroup.removeFirst()
        }

        return currentGroup
    }

    private fun List<MdxToken>.getComponentType(): MdxComponentType {
        return if (isEmpty()) MdxComponentType.OTHER else last().getComponentType()
    }

    private fun MdxToken.getComponentType(): MdxComponentType {
        return when (type) {
            MdxType.TEXT,
            MdxType.BOLD,
            MdxType.ITALIC,
            MdxType.UNDERLINE,
            MdxType.WHITESPACE,
            MdxType.STRIKE,
            MdxType.HYPER_LINK,
            MdxType.LINK,
            MdxType.ESCAPE,
            MdxType.ELEMENT,
            MdxType.DATETIME,
            MdxType.COLORED -> MdxComponentType.TEXT

            else -> MdxComponentType.OTHER
        }
    }

}

const val dollarSign = "$"
const val mdxMillis = "mdx.millis"
const val mdxSecond = "mdx.second"
const val mdxMinute = "mdx.minute"
const val mdxHour = "mdx.hour"
const val mdxToday = "mdx.day"
const val mdxMonth = "mdx.month"
const val mdxYear = "mdx.year"
const val mdxDate = "mdx.date"
const val mdxDateTime = "mdx.datetime"
const val mdxTime = "mdx.time"

val currentMillis = System.currentTimeMillis()

val mdxAdhocMap = hashMapOf(
    mdxMillis to currentMillis.toString(),
    mdxSecond to formattedDateTime("ss"),
    mdxMinute to formattedDateTime("mm"),
    mdxHour to formattedDateTime("hh"),
    mdxToday to formattedDateTime("dd"),
    mdxMonth to formattedDateTime("MM"),
    mdxYear to formattedDateTime("yyyy"),
    mdxDate to formattedDateTime("dd/MM/yyyy"),
    mdxDateTime to formattedDateTime("dd/MM/yyyy hh:mm:ss a"),
    mdxTime to formattedDateTime("hh:mm:ss a")
)

val MDX_TEST = """
    #1 heading
    * item
    
    some text
    
    (img@https://picsum.photos/id/67/300/200)
    
    some more text
""".trimIndent()

val MDX_SAMPLE = """
    #1 this is heading 1
    #2 this is heading 2
    #3 this is heading 3
    #4 this is heading 4
    #5 this is heading 5
    #6 this is heading 6
   
    =line=
    
    `ignore ~syntax~ here`
    
    Lorem ipsum dolor sit amet, consectetur adipiscing elit. 
    
    Lorem ipsum dolor sit amet, consectetur adipiscing elit.
    
    <color this text#FF0000>
    
    this is &bold& text
    this is /italic/ text
    this is _underline_ text
    this is ~strikethrough~ text.
    date: %dd/MM/yyyy%
    
    Current time : mdx.time
    
    "this is quote text -author"
    
    {
    .kt
    fun main(varargs args: String) {
        println("Hello World!")
        val millis = System.currentTimeMillis()
        println("Current time in millis: ${dollarSign}millis")
        // output : mdx.millis
    }
    }
    
    \"this should not show quote\"
    
    {
    .py
    def main():
        print("Hello World!")
        
    
    if __name__ == '__main__':
        main()
    }
    
    Search (here@http://www.google.com) for anything.
    
    (img@https://picsum.photos/id/67/300/200)
    (ytb@https://www.youtube.com/watch?v=dQw4w9WgXcQ)
    
    image space bug test
    
    [
    a    |b    |result
    true |true |true  
    true |false|false 
    false|false|false 
    ]
    
    * unordered item
    * unordered item
    
    *o uncheck item
    *x checked item
""".trimIndent()

fun List<MdxComponentGroup>.getFormattedString(): String {
    val strBuilder = StringBuilder()
    forEach { group ->
        when (group.type) {
            MdxComponentType.TEXT -> {
                group.children.forEach { token ->
                    when (token.type) {
                        MdxType.COLORED -> {
                            val (value, hexColor) = token.getTextColorPair()
                            strBuilder.append("$value: $hexColor")
                        }

                        MdxType.LINK,
                        MdxType.HYPER_LINK -> {
                            val (hyper, link) = token.getHyperLink()
                            strBuilder.append("$hyper: $link")
                        }

                        MdxType.ELEMENT -> {
                            when {
                                token.text.startsWith("o ") -> {
                                    val text = token.text.replace("o ", "")
                                    strBuilder.append("\u2610 $text")
                                }

                                token.text.startsWith("x ") -> {
                                    val text = token.text.replace("x ", "")
                                    strBuilder.append("\u2611 $text")
                                }

                                else -> strBuilder.append("\u25CF ${token.text}")
                            }
                        }

                        else -> strBuilder.append(token.text)
                    }
                }
                strBuilder.append("\n")
            }

            MdxComponentType.OTHER -> {
                group.children.forEach { token ->
                    when (token.type) {
                        MdxType.CODE -> {
                            val (lang, code) = token.getLangCodePair()
                            strBuilder.append("$lang: $code")
                        }

                        MdxType.DIVIDER -> {
                            strBuilder.append("---")
                        }

                        MdxType.TABLE -> {
                            val rows = token.getTableItemPairs()
                            strBuilder.append(rows)
                        }

                        else -> strBuilder.append(token.text)
                    }

                    strBuilder.append("\n")
                }
            }
        }
    }

    return strBuilder.toString()
}

fun formattedDateTime(pattern: String): String {
    val localDateTime = LocalDateTime.now()
    val formatter = DateTimeFormatter.ofPattern(pattern)
    return localDateTime.format(formatter)
}

fun main() {
    val text = MdxParser.parse(MDX_SAMPLE).getFormattedString()
    println(text)
}