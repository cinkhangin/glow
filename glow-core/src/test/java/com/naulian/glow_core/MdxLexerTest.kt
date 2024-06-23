package com.naulian.glow_core

import com.naulian.glow_core.mdx.MdxLexer
import com.naulian.glow_core.mdx.MdxNode
import com.naulian.glow_core.mdx.MdxType
import org.junit.Assert.assertEquals
import org.junit.Test

class MdxLexerTest {

    @Test
    fun plainTextTest() {
        val source = """
            Lorem ipsum dolor sit amet, consectetur adipiscing elit.
    
            Lorem ipsum dolor sit amet, consectetur adipiscing elit.
        """.trimIndent()
        val actual = MdxLexer(source).tokenize()
        val expected = listOf(
            MdxNode(
                type = MdxType.TEXT,
                literal = "Lorem ipsum dolor sit amet, consectetur adipiscing elit."
            ),
            MdxNode(type = MdxType.NEWLINE, literal = "\n\n"),
            MdxNode(
                type = MdxType.TEXT,
                literal = "Lorem ipsum dolor sit amet, consectetur adipiscing elit."
            )
        )
        assertEquals(expected, actual)
    }

    @Test
    fun styledTextTest() {
        val source = """
            this is &bold& text
            this is /italic/ text
            this is _underline_ text
            this is ~strikethrough~ text
        """.trimIndent()
        val actual = MdxLexer(source).tokenize()
        val expected = listOf(
            MdxNode(type = MdxType.TEXT, literal = "this is "),
            MdxNode(type = MdxType.BLOCK_SYMBOL, literal = "&"),
            MdxNode(type = MdxType.TEXT, literal = "bold"),
            MdxNode(type = MdxType.BLOCK_SYMBOL, literal = "&"),
            MdxNode(type = MdxType.WHITESPACE, literal = " "),
            MdxNode(type = MdxType.TEXT, literal = "text"),
            MdxNode(type = MdxType.NEWLINE, literal = "\n"),

            MdxNode(type = MdxType.TEXT, literal = "this is "),
            MdxNode(type = MdxType.BLOCK_SYMBOL, literal = "/"),
            MdxNode(type = MdxType.TEXT, literal = "italic"),
            MdxNode(type = MdxType.BLOCK_SYMBOL, literal = "/"),
            MdxNode(type = MdxType.WHITESPACE, literal = " "),
            MdxNode(type = MdxType.TEXT, literal = "text"),
            MdxNode(type = MdxType.NEWLINE, literal = "\n"),

            MdxNode(type = MdxType.TEXT, literal = "this is "),
            MdxNode(type = MdxType.BLOCK_SYMBOL, literal = "_"),
            MdxNode(type = MdxType.TEXT, literal = "underline"),
            MdxNode(type = MdxType.BLOCK_SYMBOL, literal = "_"),
            MdxNode(type = MdxType.WHITESPACE, literal = " "),
            MdxNode(type = MdxType.TEXT, literal = "text"),
            MdxNode(type = MdxType.NEWLINE, literal = "\n"),

            MdxNode(type = MdxType.TEXT, literal = "this is "),
            MdxNode(type = MdxType.BLOCK_SYMBOL, literal = "~"),
            MdxNode(type = MdxType.TEXT, literal = "strikethrough"),
            MdxNode(type = MdxType.BLOCK_SYMBOL, literal = "~"),
            MdxNode(type = MdxType.WHITESPACE, literal = " "),
            MdxNode(type = MdxType.TEXT, literal = "text")
        )
        assertEquals(expected, actual)
    }

    @Test
    fun dividerTest() {
        val source = """
            =line=
        """.trimIndent()
        val actual = MdxLexer(source).tokenize()
        val expected = listOf(
            MdxNode(type = MdxType.DIVIDER, literal = "line")
        )
        assertEquals(expected, actual)
    }

    @Test
    fun ignoreTest() {
        val source = """
            `ignore ~syntax~ here`
        """.trimIndent()
        val actual = MdxLexer(source).tokenize()
        val expected = listOf(
            MdxNode(type = MdxType.IGNORE, literal = "ignore ~syntax~ here")
        )
        assertEquals(expected, actual)
    }

    @Test
    fun colorTest() {
        val source = """
            <color this text#FF0000>
        """.trimIndent()
        val actual = MdxLexer(source).tokenize()
        val expected = listOf(
            MdxNode(type = MdxType.COLOR_START, literal = "<"),
            MdxNode(type = MdxType.TEXT, literal = "color this text"),
            MdxNode(type = MdxType.COLOR_HEX, literal = "#FF0000"),
            MdxNode(type = MdxType.COLOR_END, literal = ">")
        )
        assertEquals(expected, actual)
    }

    @Test
    fun linkTest() {
        val source = """
            (https://www.google.com)
            Search (here@http://www.google.com)
        """.trimIndent()
        val actual = MdxLexer(source).tokenize()
        val expected = listOf(
            MdxNode(type = MdxType.LINK, literal = "https://www.google.com"),
            MdxNode(type = MdxType.NEWLINE, literal = "\n"),
            MdxNode(type = MdxType.TEXT, literal = "Search "),
            MdxNode(type = MdxType.HYPER_LINK, literal = "here@http://www.google.com")
        )
        assertEquals(expected, actual)
    }

    @Test
    fun imageAndYoutubeTest() {
        val source = """
            (img@https://picsum.photos/id/67/300/200)
            (ytb@https://www.youtube.com/watch?v=dQw4w9WgXcQ)
        """.trimIndent()
        val actual = MdxLexer(source).tokenize()
        val expected = listOf(
            MdxNode(type = MdxType.IMAGE, literal = "https://picsum.photos/id/67/300/200"),
            MdxNode(type = MdxType.NEWLINE, literal = "\n"),
            MdxNode(
                type = MdxType.YOUTUBE,
                literal = "https://www.youtube.com/watch?v=dQw4w9WgXcQ"
            )
        )
        assertEquals(expected, actual)
    }

    @Test
    fun escapedTest() {
        val source = """
            \"this should not show as quote\"
        """.trimIndent()
        val actual = MdxLexer(source).tokenize()
        val expected = listOf(
            MdxNode(type = MdxType.ESCAPE, literal = "\""),
            MdxNode(type = MdxType.TEXT, literal = "this should not show as quote"),
            MdxNode(type = MdxType.ESCAPE, literal = "\"")
        )
        assertEquals(expected, actual)
    }

    @Test
    fun headingTest() {
        val source = """
            #1 heading 1
            #2 heading 2
        """.trimIndent()
        val actual = MdxLexer(source).tokenize()
        val expected = listOf(
            MdxNode(type = MdxType.HEADER, literal = "#1"),
            MdxNode(type = MdxType.WHITESPACE, literal = " "),
            MdxNode(type = MdxType.TEXT, literal = "heading 1"),
            MdxNode(type = MdxType.NEWLINE, literal = "\n"),
            MdxNode(type = MdxType.HEADER, literal = "#2"),
            MdxNode(type = MdxType.WHITESPACE, literal = " "),
            MdxNode(type = MdxType.TEXT, literal = "heading 2")
        )
        assertEquals(expected, actual)
    }

    @Test
    fun codeBlockTest() {
        val source = """
            {
            .py
            def main():
                print("Hello World!")
                
            if __name__ == '__main__':
                main()
            }
        """.trimIndent()
        val actual = MdxLexer(source).tokenize()
        val expected = listOf(
            MdxNode(
                type = MdxType.CODE,
                literal = """
                    .py
                    def main():
                        print("Hello World!")
                        
                    if __name__ == '__main__':
                        main()
                """.trimIndent()
            ),
        )
        assertEquals(expected, actual)
    }

    @Test
    fun quoteBlockTest() {
        val source = """
            "this is quote text -author"
        """.trimIndent()
        val actual = MdxLexer(source).tokenize()
        val expected = listOf(
            MdxNode(type = MdxType.BLOCK_SYMBOL, literal = "\""),
            MdxNode(
                type = MdxType.TEXT,
                literal = "this is quote text -author".trimIndent()
            ),
            MdxNode(type = MdxType.BLOCK_SYMBOL, literal = "\"")
        )
        assertEquals(expected, actual)
    }

    @Test
    fun listTest() {
        val source = """
            * unordered item
            *o unchecked item
            *x checked item
        """.trimIndent()
        val actual = MdxLexer(source).tokenize()
        val expected = listOf(
            MdxNode(type = MdxType.ELEMENT, literal = "*"),
            MdxNode(type = MdxType.WHITESPACE, literal = " "),
            MdxNode(type = MdxType.TEXT, literal = "unordered item"),
            MdxNode(type = MdxType.NEWLINE, literal = "\n"),
            MdxNode(type = MdxType.ELEMENT, literal = "*o"),
            MdxNode(type = MdxType.WHITESPACE, literal = " "),
            MdxNode(type = MdxType.TEXT, literal = "unchecked item"),
            MdxNode(type = MdxType.NEWLINE, literal = "\n"),
            MdxNode(type = MdxType.ELEMENT, literal = "*x"),
            MdxNode(type = MdxType.WHITESPACE, literal = " "),
            MdxNode(type = MdxType.TEXT, literal = "checked item"),
        )
        assertEquals(expected, actual)
    }

    @Test
    fun tableTest() {
        val source = """
            [
            a    |b    |result
            true |true |true
            ]
        """.trimIndent()
        val actual = MdxLexer(source).tokenize()
        val expected = listOf(
            MdxNode(type = MdxType.TABLE_START, literal = "["),
            MdxNode(type = MdxType.NEWLINE, literal = "\n"),
            MdxNode(type = MdxType.TEXT, literal = "a    "),
            MdxNode(type = MdxType.PIPE, literal = "|"),
            MdxNode(type = MdxType.TEXT, literal = "b    "),
            MdxNode(type = MdxType.PIPE, literal = "|"),
            MdxNode(type = MdxType.TEXT, literal = "result"),
            MdxNode(type = MdxType.NEWLINE, literal = "\n"),
            MdxNode(type = MdxType.TEXT, literal = "true "),
            MdxNode(type = MdxType.PIPE, literal = "|"),
            MdxNode(type = MdxType.TEXT, literal = "true "),
            MdxNode(type = MdxType.PIPE, literal = "|"),
            MdxNode(type = MdxType.TEXT, literal = "true"),
            MdxNode(type = MdxType.NEWLINE, literal = "\n"),
            MdxNode(type = MdxType.TABLE_END, literal = "]"),
        )
        assertEquals(expected, actual)
    }
}