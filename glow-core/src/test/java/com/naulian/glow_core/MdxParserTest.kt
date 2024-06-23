package com.naulian.glow_core

import com.naulian.glow_core.mdx.MdxNode2
import com.naulian.glow_core.mdx.MdxParser2
import com.naulian.glow_core.mdx.MdxType2
import org.junit.Assert.assertEquals
import org.junit.Test

class MdxParserTest {

    @Test
    fun plainTextTest() {
        val source = "plain text"
        val actual = MdxParser2(source).parse().children
        val expected = listOf(
            MdxNode2(
                type = MdxType2.PARAGRAPH,
                children = listOf(
                    MdxNode2(type = MdxType2.TEXT, literal = "plain text")
                )
            )
        )
        assertEquals(expected, actual)
    }

    @Test
    fun headerTest() {
        val source = "#1 header"
        val actual = MdxParser2(source).parse().children
        val expected = listOf(
            MdxNode2(
                type = MdxType2.H1,
                literal = "",
                children = listOf(
                    MdxNode2(
                        type = MdxType2.PARAGRAPH,
                        children = listOf(
                            MdxNode2(type = MdxType2.TEXT, literal = "header")
                        )
                    )
                )
            )
        )
        assertEquals(expected, actual)
    }

    @Test
    fun header2Test() {
        val source = """
            #1 header 1
            #2 header 2
        """.trimIndent()
        val actual = MdxParser2(source).parse().children
        val expected = listOf(
            MdxNode2(
                type = MdxType2.H1,
                literal = "",
                children = listOf(
                    MdxNode2(
                        type = MdxType2.PARAGRAPH,
                        children = listOf(
                            MdxNode2(type = MdxType2.TEXT, literal = "header 1")
                        )
                    )
                )
            ),
            MdxNode2(
                type = MdxType2.H2,
                literal = "",
                children = listOf(
                    MdxNode2(
                        type = MdxType2.PARAGRAPH,
                        children = listOf(
                            MdxNode2(type = MdxType2.TEXT, literal = "header 2")
                        )
                    )
                )
            )
        )
        assertEquals(expected, actual)
    }

    @Test
    fun headerWithStyleTest() {
        val source = "#1 ~header underline~"
        val actual = MdxParser2(source).parse().children
        val expected = listOf(
            MdxNode2(
                type = MdxType2.H1,
                literal = "",
                children = listOf(
                    MdxNode2(
                        type = MdxType2.PARAGRAPH, children = listOf(
                            MdxNode2(
                                type = MdxType2.STRIKE,
                                children = listOf(
                                    MdxNode2(
                                        type = MdxType2.TEXT,
                                        literal = "header underline"
                                    )
                                )
                            )
                        )
                    )
                )
            )
        )
        assertEquals(expected, actual)
    }

    @Test
    fun quotationTest() {
        val source = """
            "this is a quote text &-author&"
        """.trimIndent()
        val actual = MdxParser2(source).parse().children
        val expected = listOf(
            MdxNode2(
                type = MdxType2.QUOTATION,
                literal = "",
                children = listOf(
                    MdxNode2(
                        type = MdxType2.PARAGRAPH, children = listOf(
                            MdxNode2(type = MdxType2.TEXT, literal = "this is a quote text "),
                            MdxNode2(
                                type = MdxType2.BOLD,
                                children = listOf(
                                    MdxNode2(type = MdxType2.TEXT, literal = "-author")
                                )
                            )
                        )
                    )
                )
            )
        )
        assertEquals(expected, actual)
    }

    @Test
    fun codeTest() {
        val source = """
            {
            .py
            def main():
                print("Hello World!")
                
            if __name__ == '__main__':
                main()
            }
        """.trimIndent()
        val actual = MdxParser2(source).parse().children
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
            )
        )
        assertEquals(expected, actual)
    }

    @Test
    fun dividerTest() {
        val source = """
            =line=
        """.trimIndent()
        val actual = MdxParser2(source).parse().children
        val expected = listOf(
            MdxNode2(
                type = MdxType2.DIVIDER,
                literal = "line"
            )
        )
        assertEquals(expected, actual)
    }

    @Test
    fun tableTest() {
        val source = """
            [
            a    |b    |c
            true |false|true
            ]
        """.trimIndent()
        val actual = MdxParser2(source).parse().children
        val expected = listOf(
            MdxNode2(
                type = MdxType2.TABLE,
                children = listOf(
                    MdxNode2(
                        type = MdxType2.TABLE_COLOMN,
                        children = listOf(
                            MdxNode2(
                                type = MdxType2.PARAGRAPH,
                                children = listOf(
                                    MdxNode2(type = MdxType2.TEXT, literal = "a    ")
                                )
                            ),
                            MdxNode2(type = MdxType2.PIPE, literal = "|"),
                            MdxNode2(
                                type = MdxType2.PARAGRAPH,
                                children = listOf(
                                    MdxNode2(type = MdxType2.TEXT, literal = "b    ")
                                )
                            ),
                            MdxNode2(type = MdxType2.PIPE, literal = "|"),
                            MdxNode2(
                                type = MdxType2.PARAGRAPH,
                                children = listOf(
                                    MdxNode2(type = MdxType2.TEXT, literal = "c"),
                                )
                            )
                        )
                    ),
                    MdxNode2(
                        type = MdxType2.TABLE_COLOMN,
                        children = listOf(
                            MdxNode2(
                                type = MdxType2.PARAGRAPH,
                                children = listOf(
                                    MdxNode2(type = MdxType2.TEXT, literal = "true "),
                                )
                            ),
                            MdxNode2(type = MdxType2.PIPE, literal = "|"),
                            MdxNode2(
                                type = MdxType2.PARAGRAPH,
                                children = listOf(
                                    MdxNode2(type = MdxType2.TEXT, literal = "false"),
                                )
                            ),
                            MdxNode2(type = MdxType2.PIPE, literal = "|"),
                            MdxNode2(
                                type = MdxType2.PARAGRAPH,
                                children = listOf(
                                    MdxNode2(type = MdxType2.TEXT, literal = "true"),
                                )
                            )
                        )
                    )
                )
            )
        )
        assertEquals(expected, actual)
    }

    @Test
    fun coloredTest() {
        val source = """
            <color this text#FF0000>
        """.trimIndent()
        val actual = MdxParser2(source).parse().children
        val expected = listOf(
            MdxNode2(
                type = MdxType2.PARAGRAPH,
                children = listOf(
                    MdxNode2(
                        type = MdxType2.COLORED,
                        literal = "#FF0000",
                        children = listOf(
                            MdxNode2(type = MdxType2.TEXT, literal = "color this text"),
                        )
                    )
                )
            )
        )
        assertEquals(expected, actual)
    }

    @Test
    fun ignoreTest() {
        val source = """
            `the syntax ~should~ be ignore here`
        """.trimIndent()
        val actual = MdxParser2(source).parse().children
        val expected = listOf(
            MdxNode2(
                type = MdxType2.PARAGRAPH,
                children = listOf(
                    MdxNode2(
                        type = MdxType2.IGNORE,
                        literal = "the syntax ~should~ be ignore here",
                    )
                )
            )
        )
        assertEquals(expected, actual)
    }

    @Test
    fun elementTest() {
        val source = """
            * unordered item
            
            *o unchecked item
            *x checked item
        """.trimIndent()
        val actual = MdxParser2(source).parse().children
        val expected = listOf(
            MdxNode2(
                type = MdxType2.ELEMENT_BULLET,
                children = listOf(
                    MdxNode2(
                        type = MdxType2.PARAGRAPH,
                        children = listOf(
                            MdxNode2(type = MdxType2.TEXT, literal = "unordered item"),
                        )
                    )
                )
            ),
            MdxNode2(
                type = MdxType2.ELEMENT_UNCHECKED,
                children = listOf(
                    MdxNode2(
                        type = MdxType2.PARAGRAPH,
                        children = listOf(
                            MdxNode2(type = MdxType2.TEXT, literal = "unchecked item"),
                        )
                    )
                )
            ),
            MdxNode2(
                type = MdxType2.ELEMENT_CHECKED,
                children = listOf(
                    MdxNode2(
                        type = MdxType2.PARAGRAPH,
                        children = listOf(
                            MdxNode2(type = MdxType2.TEXT, literal = "checked item"),
                        )
                    )
                )
            )
        )
        assertEquals(expected, actual)
    }
}