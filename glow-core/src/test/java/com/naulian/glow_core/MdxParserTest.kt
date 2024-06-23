package com.naulian.glow_core

import com.naulian.glow_core.mdx.MdxNode
import com.naulian.glow_core.mdx.MdxParser
import com.naulian.glow_core.mdx.MdxType
import org.junit.Assert.assertEquals
import org.junit.Test

class MdxParserTest {

    @Test
    fun plainTextTest() {
        val source = "plain text"
        val actual = MdxParser(source).parse().children
        val expected = listOf(
            MdxNode(
                type = MdxType.PARAGRAPH,
                children = listOf(
                    MdxNode(type = MdxType.TEXT, literal = "plain text")
                )
            )
        )
        assertEquals(expected, actual)
    }

    @Test
    fun headerTest() {
        val source = "#1 header"
        val actual = MdxParser(source).parse().children
        val expected = listOf(
            MdxNode(
                type = MdxType.H1,
                literal = "",
                children = listOf(
                    MdxNode(
                        type = MdxType.PARAGRAPH,
                        children = listOf(
                            MdxNode(type = MdxType.TEXT, literal = "header")
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
        val actual = MdxParser(source).parse().children
        val expected = listOf(
            MdxNode(
                type = MdxType.H1,
                literal = "",
                children = listOf(
                    MdxNode(
                        type = MdxType.PARAGRAPH,
                        children = listOf(
                            MdxNode(type = MdxType.TEXT, literal = "header 1")
                        )
                    )
                )
            ),
            MdxNode(
                type = MdxType.H2,
                literal = "",
                children = listOf(
                    MdxNode(
                        type = MdxType.PARAGRAPH,
                        children = listOf(
                            MdxNode(type = MdxType.TEXT, literal = "header 2")
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
        val actual = MdxParser(source).parse().children
        val expected = listOf(
            MdxNode(
                type = MdxType.H1,
                literal = "",
                children = listOf(
                    MdxNode(
                        type = MdxType.PARAGRAPH, children = listOf(
                            MdxNode(
                                type = MdxType.STRIKE,
                                children = listOf(
                                    MdxNode(
                                        type = MdxType.TEXT,
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
        val actual = MdxParser(source).parse().children
        val expected = listOf(
            MdxNode(
                type = MdxType.QUOTATION,
                literal = "",
                children = listOf(
                    MdxNode(
                        type = MdxType.PARAGRAPH, children = listOf(
                            MdxNode(type = MdxType.TEXT, literal = "this is a quote text "),
                            MdxNode(
                                type = MdxType.BOLD,
                                children = listOf(
                                    MdxNode(type = MdxType.TEXT, literal = "-author")
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
        val actual = MdxParser(source).parse().children
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
            )
        )
        assertEquals(expected, actual)
    }

    @Test
    fun dividerTest() {
        val source = """
            =line=
        """.trimIndent()
        val actual = MdxParser(source).parse().children
        val expected = listOf(
            MdxNode(
                type = MdxType.DIVIDER,
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
        val actual = MdxParser(source).parse().children
        val expected = listOf(
            MdxNode(
                type = MdxType.TABLE,
                children = listOf(
                    MdxNode(
                        type = MdxType.TABLE_COLOMN,
                        children = listOf(
                            MdxNode(
                                type = MdxType.PARAGRAPH,
                                children = listOf(
                                    MdxNode(type = MdxType.TEXT, literal = "a    ")
                                )
                            ),
                            MdxNode(type = MdxType.PIPE, literal = "|"),
                            MdxNode(
                                type = MdxType.PARAGRAPH,
                                children = listOf(
                                    MdxNode(type = MdxType.TEXT, literal = "b    ")
                                )
                            ),
                            MdxNode(type = MdxType.PIPE, literal = "|"),
                            MdxNode(
                                type = MdxType.PARAGRAPH,
                                children = listOf(
                                    MdxNode(type = MdxType.TEXT, literal = "c"),
                                )
                            )
                        )
                    ),
                    MdxNode(
                        type = MdxType.TABLE_COLOMN,
                        children = listOf(
                            MdxNode(
                                type = MdxType.PARAGRAPH,
                                children = listOf(
                                    MdxNode(type = MdxType.TEXT, literal = "true "),
                                )
                            ),
                            MdxNode(type = MdxType.PIPE, literal = "|"),
                            MdxNode(
                                type = MdxType.PARAGRAPH,
                                children = listOf(
                                    MdxNode(type = MdxType.TEXT, literal = "false"),
                                )
                            ),
                            MdxNode(type = MdxType.PIPE, literal = "|"),
                            MdxNode(
                                type = MdxType.PARAGRAPH,
                                children = listOf(
                                    MdxNode(type = MdxType.TEXT, literal = "true"),
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
        val actual = MdxParser(source).parse().children
        val expected = listOf(
            MdxNode(
                type = MdxType.PARAGRAPH,
                children = listOf(
                    MdxNode(
                        type = MdxType.COLORED,
                        literal = "#FF0000",
                        children = listOf(
                            MdxNode(type = MdxType.TEXT, literal = "color this text"),
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
        val actual = MdxParser(source).parse().children
        val expected = listOf(
            MdxNode(
                type = MdxType.PARAGRAPH,
                children = listOf(
                    MdxNode(
                        type = MdxType.IGNORE,
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
        val actual = MdxParser(source).parse().children
        val expected = listOf(
            MdxNode(
                type = MdxType.ELEMENT_BULLET,
                children = listOf(
                    MdxNode(
                        type = MdxType.PARAGRAPH,
                        children = listOf(
                            MdxNode(type = MdxType.TEXT, literal = "unordered item"),
                        )
                    )
                )
            ),
            MdxNode(
                type = MdxType.ELEMENT_UNCHECKED,
                children = listOf(
                    MdxNode(
                        type = MdxType.PARAGRAPH,
                        children = listOf(
                            MdxNode(type = MdxType.TEXT, literal = "unchecked item"),
                        )
                    )
                )
            ),
            MdxNode(
                type = MdxType.ELEMENT_CHECKED,
                children = listOf(
                    MdxNode(
                        type = MdxType.PARAGRAPH,
                        children = listOf(
                            MdxNode(type = MdxType.TEXT, literal = "checked item"),
                        )
                    )
                )
            )
        )
        assertEquals(expected, actual)
    }
}