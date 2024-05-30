package com.naulian.glow_core.mdx

import com.naulian.glow_core.atx.ATX_SAMPLE
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

const val mdxVirtualNewline = "mdx.virtual.newline"
fun CharSequence.str(): String = toString().trim()
fun CharSequence.str(textToReplace: String) = str().replace(textToReplace, "")

class MdxPreProcessor(private val input: String) {
    private val endChar = Char.MIN_VALUE
    private val isNotEndChar get() = char() != endChar
    private val isNotNewLine get() = char() != '\n'


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
                '<' -> consumeContainer(char, '>')
                '[' -> consumeContainer(char, ']')
                else -> consumeLine()
            }

            blockList.add(block)
        }

        //blockList.forEach(::println)
        return blockList
    }

    private fun consumeLine(): String {
        val start = cursor
        while (isNotNewLine && isNotEndChar) {
            advance()
        }
        val value = input.subSequence(start, cursor).toString()
        return "${value}$mdxVirtualNewline"
    }

    private fun consumeBlock(containerChar: Char): String {
        val start = cursor
        advance()
        while (isNotEndChar) {
            if (char() == containerChar) {
                advance()
                break
            }
            advance()
        }
        return input.subSequence(start, cursor).toString()
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

enum class MdxType {
    //Headers
    H1, H2, H3, H4, H5, H6,
    TEXT, BOLD, ITALIC, UNDERLINE, STRIKE,
    QUOTE, LINK, HYPER_LINK, IMAGE, CODE,
    TABLE, ELEMENT, ADHOC,
    WHITESPACE, DIVIDER, EOF,
}

data class MdxToken(
    val type: MdxType,
    val text: String,
) {
    companion object {
        val EOF = MdxToken(MdxType.EOF, "")
    }

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

                if (current.type == MdxType.TEXT && current.text.isBlank()) {
                    current = lexer.next()
                    continue
                }

                tokens.add(current)
                current = lexer.next()
            }
        }

        tokens.forEach(::println)
        return tokens
    }
}

class MdxLexer(input: String) {
    private var cursor = 0
    private val source = input.replace(" ~\n", " ")
    private val isNotEndChar get() = char() != Char.MIN_VALUE
    private val lowerAlphas = 'a'..'z'
    private val upperAlphas = 'A'..'Z'

    //private val digitChars = '0'..'9'
    private val textSymbolChars = " ,.:;?!@"
    private val alphaChars = lowerAlphas + upperAlphas
    private val textChars = alphaChars + textSymbolChars
    private val charIsText get() = char() in textChars
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
            '-' -> createBlockToken(MdxType.STRIKE, char)
            '_' -> createBlockToken(MdxType.UNDERLINE, char)
            '"' -> createBlockToken(MdxType.QUOTE, char)
            '=' -> createBlockToken(MdxType.DIVIDER, char)
            '[' -> createTableToken()
            '{' -> createAdhocToken()
            '#' -> createHeaderToken()
            '<' -> createCodeToken()
            '*' -> createElementToken()
            '(' -> createLinkToken()
            in textChars -> createTextToken()
            Char.MIN_VALUE -> MdxToken.EOF
            else -> {
                advance()
                MdxToken(MdxType.TEXT, char.toString())
            }
        }
    }

    private fun createAdhocToken(): MdxToken {
        advance() //skip opening bracket
        val start = cursor
        var level = 0
        while (char() != Char.MIN_VALUE) {
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
        return MdxToken(MdxType.ADHOC, value.str())
    }

    private fun createLinkToken(): MdxToken {
        advance() //skip opening parenthesis
        val start = cursor
        var level = 0
        while (char() != Char.MIN_VALUE) {
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
            if (hyper == "img") {
                return MdxToken(MdxType.IMAGE, link)
            }

            return MdxToken(MdxType.HYPER_LINK, value.str())
        }
        return MdxToken(MdxType.LINK, value.str())
    }

    private fun createElementToken(): MdxToken {
        advance()
        skipWhiteSpace()
        val start = cursor
        while (char() != '\n' && isNotEndChar) {
            advance()
        }
        val text = source.subSequence(start, cursor)
        return MdxToken(MdxType.ELEMENT, text.str(mdxVirtualNewline))
    }

    private fun createTextToken(): MdxToken {
        val start = cursor
        while (charIsText && isNotEndChar) {
            advance()
        }
        val text = source.subSequence(start, cursor).toString()
            .replace(mdxVirtualNewline, "\n")
        return MdxToken(MdxType.TEXT, text)
    }

    private fun createBlockToken(type: MdxType, char: Char): MdxToken {
        advance()
        val start = cursor
        while (char() != char && isNotEndChar) {
            advance()
        }
        val valueText = source.subSequence(start, cursor)
        advance()
        return MdxToken(type, valueText.str())
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
        while (char() != '\n' && isNotEndChar) {
            advance()
        }
        val text = source.subSequence(start, cursor)
        return MdxToken(type, text.str(mdxVirtualNewline))
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

    private fun createCodeToken(): MdxToken {
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

        var code = value
        mdxAdhocMap.forEach {
            code = code.replace(it.key, it.value)
        }

        return MdxToken(MdxType.CODE, code)
    }
}

data class MdxComponentGroup(
    val type: MdxComponentType,
    val children: List<MdxToken>,
)

enum class MdxComponentType {
    TEXT, OTHER, LINK, TABLE
}

class MdxParser(source: String) {
    private val atxTokens = MdxTokenizer.tokenize(source)

    private var cursor = 0
    private fun token() = atxTokens.getOrElse(cursor) { MdxToken.EOF }
    private fun advance(amount: Int = 1) {
        cursor += amount
    }

    private fun next(): MdxToken {
        advance()
        return token()
    }

    fun parse(): List<MdxComponentGroup> {
        val atxGroups = mutableListOf<MdxComponentGroup>()
        var atxTokens = mutableListOf<MdxToken>()

        var element = token()
        while (element.type != MdxType.EOF) {
            val lastType = atxTokens.lastOrNull()?.getComponentType() ?: MdxComponentType.OTHER
            val type = element.getComponentType()

            if (atxTokens.isEmpty() || lastType == type) {
                atxTokens.add(element)
            } else {
                val atxGroup = MdxComponentGroup(lastType, atxTokens)
                atxGroups.add(atxGroup)
                atxTokens = mutableListOf(element)
            }
            element = next()
        }

        if (atxTokens.isNotEmpty()) {
            val type = atxTokens.last().getComponentType()
            val atxNode = MdxComponentGroup(type, atxTokens)
            atxGroups.add(atxNode)
        }

        return atxGroups
    }

    private fun MdxToken.getComponentType(): MdxComponentType {
        return when (type) {
            MdxType.TEXT,
            MdxType.BOLD,
            MdxType.ITALIC,
            MdxType.UNDERLINE,
            MdxType.WHITESPACE,
            MdxType.STRIKE,
            MdxType.ADHOC,
            -> MdxComponentType.TEXT

            MdxType.HYPER_LINK,
            MdxType.LINK,
            -> MdxComponentType.LINK

            MdxType.TABLE -> MdxComponentType.TABLE

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

val MDX_SAMPLE = """
    #1 this is heading 1
    #2 this is heading 2
    #3 this is heading 3
    #4 this is heading 4
    #5 this is heading 5
    #6 this is heading 6
   
    =line=
    
    this is &bold& text
    this is /italic/ text
    this is _underline_ text
    this is -strikethrough- text
    
    Current time : {mdx.time}
    
    "this is quote text -author"
    
    < 
    .kt
    fun main(varargs args: String) {
        println("Hello World!")
        val millis = System.currentTimeMillis()
        println("Current time in millis: ${dollarSign}millis")
        // output : mdx.millis
    }
    >
    
    < 
    .py
    def main():
        print("Hello World!")
        
    
    if __name__ == '__main__':
        main()
    >
    
    Search here (Google Website@http://www.google.com).
    
    (img@https://picsum.photos/id/67/300/200)
    
    [
    a    |b    |result
    true |true |true  
    true |false|false 
    false|false|false 
    ]
    
    * unordered item
    * unordered item
""".trimIndent()

object Mdx {
    fun parse(source: String): List<MdxComponentGroup> {
        return MdxParser(source).parse()
    }
}

object MdxTextPrinter {
    fun print(source: String) {
        Mdx.parse(source).forEach { group ->
            when (group.type) {
                MdxComponentType.TEXT -> {
                    group.children.forEach { token ->
                        kotlin.io.print(token.text)
                    }
                }

                MdxComponentType.OTHER -> {
                    group.children.forEach {
                        when (it.type) {
                            MdxType.CODE -> {
                                val (lang, code) = it.getLangCodePair()
                                println("$lang: $code")
                            }

                            MdxType.DIVIDER -> {
                                println("---")
                            }

                            MdxType.ADHOC -> {
                                val text = when (it.text) {
                                    in mdxAdhocMap -> mdxAdhocMap[it.text]
                                    else -> "Adhoc: ${it.text}"
                                }
                                println(text)
                            }

                            else -> println(it.text)
                        }
                    }
                }

                MdxComponentType.LINK -> {
                    group.children.forEach {
                        val (hyper, link) = it.getHyperLink()
                        println("$hyper: $link")
                    }
                }

                MdxComponentType.TABLE -> {
                    group.children.forEach {
                        val rows = it.getTableItemPairs()
                        println(rows)
                    }
                }
            }
        }
    }
}

fun formattedDateTime(pattern: String): String {
    val localDateTime = LocalDateTime.now()
    val formatter = DateTimeFormatter.ofPattern(pattern)
    return localDateTime.format(formatter)
}

fun main() {
    MdxTextPrinter.print(ATX_SAMPLE)
}