package com.naulian.glow_core

import com.naulian.glow_core.mdx.MdxLexer2
import com.naulian.glow_core.mdx.MdxNode2
import com.naulian.glow_core.mdx.MdxType2
import org.junit.Assert.assertEquals
import org.junit.Test

class MdxLexerTest {

    @Test
    fun plainTextTest() {
        val source = """
            Lorem ipsum dolor sit amet, consectetur adipiscing elit.
    
            Lorem ipsum dolor sit amet, consectetur adipiscing elit.
        """.trimIndent()
        val actual = MdxLexer2(source).tokenize()
        val expected = listOf(
            MdxNode2(
                type = MdxType2.TEXT,
                literal = "Lorem ipsum dolor sit amet, consectetur adipiscing elit."
            ),
            MdxNode2(type = MdxType2.NEWLINE, literal = "\n\n"),
            MdxNode2(
                type = MdxType2.TEXT,
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
        val actual = MdxLexer2(source).tokenize()
        val expected = listOf(
            MdxNode2(type = MdxType2.TEXT, literal = "this is "),
            MdxNode2(type = MdxType2.BLOCK_SYMBOL, literal = "&"),
            MdxNode2(type = MdxType2.TEXT, literal = "bold"),
            MdxNode2(type = MdxType2.BLOCK_SYMBOL, literal = "&"),
            MdxNode2(type = MdxType2.WHITESPACE, literal = " "),
            MdxNode2(type = MdxType2.TEXT, literal = "text"),
            MdxNode2(type = MdxType2.NEWLINE, literal = "\n"),

            MdxNode2(type = MdxType2.TEXT, literal = "this is "),
            MdxNode2(type = MdxType2.BLOCK_SYMBOL, literal = "/"),
            MdxNode2(type = MdxType2.TEXT, literal = "italic"),
            MdxNode2(type = MdxType2.BLOCK_SYMBOL, literal = "/"),
            MdxNode2(type = MdxType2.WHITESPACE, literal = " "),
            MdxNode2(type = MdxType2.TEXT, literal = "text"),
            MdxNode2(type = MdxType2.NEWLINE, literal = "\n"),

            MdxNode2(type = MdxType2.TEXT, literal = "this is "),
            MdxNode2(type = MdxType2.BLOCK_SYMBOL, literal = "_"),
            MdxNode2(type = MdxType2.TEXT, literal = "underline"),
            MdxNode2(type = MdxType2.BLOCK_SYMBOL, literal = "_"),
            MdxNode2(type = MdxType2.WHITESPACE, literal = " "),
            MdxNode2(type = MdxType2.TEXT, literal = "text"),
            MdxNode2(type = MdxType2.NEWLINE, literal = "\n"),

            MdxNode2(type = MdxType2.TEXT, literal = "this is "),
            MdxNode2(type = MdxType2.BLOCK_SYMBOL, literal = "~"),
            MdxNode2(type = MdxType2.TEXT, literal = "strikethrough"),
            MdxNode2(type = MdxType2.BLOCK_SYMBOL, literal = "~"),
            MdxNode2(type = MdxType2.WHITESPACE, literal = " "),
            MdxNode2(type = MdxType2.TEXT, literal = "text")
        )
        assertEquals(expected, actual)
    }

    @Test
    fun dividerTest() {
        val source = """
            =line=
        """.trimIndent()
        val actual = MdxLexer2(source).tokenize()
        val expected = listOf(
            MdxNode2(type = MdxType2.DIVIDER, literal = "line")
        )
        assertEquals(expected, actual)
    }

    @Test
    fun ignoreTest() {
        val source = """
            `ignore ~syntax~ here`
        """.trimIndent()
        val actual = MdxLexer2(source).tokenize()
        val expected = listOf(
            MdxNode2(type = MdxType2.IGNORE, literal = "ignore ~syntax~ here")
        )
        assertEquals(expected, actual)
    }

    @Test
    fun colorTest() {
        val source = """
            <color this text#FF0000>
        """.trimIndent()
        val actual = MdxLexer2(source).tokenize()
        val expected = listOf(
            MdxNode2(type = MdxType2.COLOR_START, literal = "<"),
            MdxNode2(type = MdxType2.TEXT, literal = "color this text"),
            MdxNode2(type = MdxType2.COLOR_HEX, literal = "#FF0000"),
            MdxNode2(type = MdxType2.COLOR_END, literal = ">")
        )
        assertEquals(expected, actual)
    }

    @Test
    fun linkTest() {
        val source = """
            (https://www.google.com)
            Search (here@http://www.google.com)
        """.trimIndent()
        val actual = MdxLexer2(source).tokenize()
        val expected = listOf(
            MdxNode2(type = MdxType2.LINK, literal = "https://www.google.com"),
            MdxNode2(type = MdxType2.NEWLINE, literal = "\n"),
            MdxNode2(type = MdxType2.TEXT, literal = "Search "),
            MdxNode2(type = MdxType2.HYPER_LINK, literal = "here@http://www.google.com")
        )
        assertEquals(expected, actual)
    }

    @Test
    fun imageAndYoutubeTest() {
        val source = """
            (img@https://picsum.photos/id/67/300/200)
            (ytb@https://www.youtube.com/watch?v=dQw4w9WgXcQ)
        """.trimIndent()
        val actual = MdxLexer2(source).tokenize()
        val expected = listOf(
            MdxNode2(type = MdxType2.IMAGE, literal = "https://picsum.photos/id/67/300/200"),
            MdxNode2(type = MdxType2.NEWLINE, literal = "\n"),
            MdxNode2(
                type = MdxType2.YOUTUBE,
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
        val actual = MdxLexer2(source).tokenize()
        val expected = listOf(
            MdxNode2(type = MdxType2.ESCAPE, literal = "\""),
            MdxNode2(type = MdxType2.TEXT, literal = "this should not show as quote"),
            MdxNode2(type = MdxType2.ESCAPE, literal = "\"")
        )
        assertEquals(expected, actual)
    }

    @Test
    fun headingTest() {
        val source = """
            #1 heading 1
            #2 heading 2
        """.trimIndent()
        val actual = MdxLexer2(source).tokenize()
        val expected = listOf(
            MdxNode2(type = MdxType2.HEADER, literal = "#1"),
            MdxNode2(type = MdxType2.WHITESPACE, literal = " "),
            MdxNode2(type = MdxType2.TEXT, literal = "heading 1"),
            MdxNode2(type = MdxType2.NEWLINE, literal = "\n"),
            MdxNode2(type = MdxType2.HEADER, literal = "#2"),
            MdxNode2(type = MdxType2.WHITESPACE, literal = " "),
            MdxNode2(type = MdxType2.TEXT, literal = "heading 2")
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
        val actual = MdxLexer2(source).tokenize()
        val expected = listOf(
            MdxNode2(
                type = MdxType2.CODE,
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
        val actual = MdxLexer2(source).tokenize()
        val expected = listOf(
            MdxNode2(type = MdxType2.BLOCK_SYMBOL, literal = "\""),
            MdxNode2(
                type = MdxType2.TEXT,
                literal = "this is quote text -author".trimIndent()
            ),
            MdxNode2(type = MdxType2.BLOCK_SYMBOL, literal = "\"")
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
        val actual = MdxLexer2(source).tokenize()
        val expected = listOf(
            MdxNode2(type = MdxType2.ELEMENT, literal = "*"),
            MdxNode2(type = MdxType2.WHITESPACE, literal = " "),
            MdxNode2(type = MdxType2.TEXT, literal = "unordered item"),
            MdxNode2(type = MdxType2.NEWLINE, literal = "\n"),
            MdxNode2(type = MdxType2.ELEMENT, literal = "*o"),
            MdxNode2(type = MdxType2.WHITESPACE, literal = " "),
            MdxNode2(type = MdxType2.TEXT, literal = "unchecked item"),
            MdxNode2(type = MdxType2.NEWLINE, literal = "\n"),
            MdxNode2(type = MdxType2.ELEMENT, literal = "*x"),
            MdxNode2(type = MdxType2.WHITESPACE, literal = " "),
            MdxNode2(type = MdxType2.TEXT, literal = "checked item"),
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
        val actual = MdxLexer2(source).tokenize()
        val expected = listOf(
            MdxNode2(type = MdxType2.TABLE_START, literal = "["),
            MdxNode2(type = MdxType2.NEWLINE, literal = "\n"),
            MdxNode2(type = MdxType2.TEXT, literal = "a    "),
            MdxNode2(type = MdxType2.PIPE, literal = "|"),
            MdxNode2(type = MdxType2.TEXT, literal = "b    "),
            MdxNode2(type = MdxType2.PIPE, literal = "|"),
            MdxNode2(type = MdxType2.TEXT, literal = "result"),
            MdxNode2(type = MdxType2.NEWLINE, literal = "\n"),
            MdxNode2(type = MdxType2.TEXT, literal = "true "),
            MdxNode2(type = MdxType2.PIPE, literal = "|"),
            MdxNode2(type = MdxType2.TEXT, literal = "true "),
            MdxNode2(type = MdxType2.PIPE, literal = "|"),
            MdxNode2(type = MdxType2.TEXT, literal = "true"),
            MdxNode2(type = MdxType2.NEWLINE, literal = "\n"),
            MdxNode2(type = MdxType2.TABLE_END, literal = "]"),
        )
        assertEquals(expected, actual)
    }
}