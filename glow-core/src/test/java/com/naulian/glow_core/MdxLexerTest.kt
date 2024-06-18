package com.naulian.glow_core

import com.naulian.glow_core.mdx.MdxLexer
import com.naulian.glow_core.mdx.MdxToken
import com.naulian.glow_core.mdx.MdxType
import org.junit.Assert.assertEquals
import org.junit.Test

class MdxLexerTest {
    @Test
    fun testText() {
        val source = """
            hello
        """.trimIndent()
        val actual = MdxLexer(source).tokenize()
        val expected = listOf(
            MdxToken(MdxType.TEXT, "hello")
        )
        assertEquals(expected, actual)
    }

    @Test
    fun testHeader() {
        val source = """
            #1 heading 1
        """.trimIndent()

        val actual = MdxLexer(source).tokenize()
        val expected = listOf(
            MdxToken(MdxType.H1, "heading 1")
        )
        assertEquals(expected, actual)
    }

    @Test
    fun testDivider() {
        val source = """
            =line=
        """.trimIndent()
        val actual = MdxLexer(source).tokenize()
        val expected = listOf(
            MdxToken(MdxType.DIVIDER, "line")
        )
        assertEquals(expected, actual)
    }

    @Test
    fun testCombine() {
        val source = """
            hello
            #1 heading 1
            =line=
        """.trimIndent()
        val actual = MdxLexer(source).tokenize()
        val expected = listOf(
            MdxToken(MdxType.TEXT, "hello"),
            MdxToken(MdxType.WHITESPACE, "\n"),
            MdxToken(MdxType.H1, "heading 1"),
            MdxToken(MdxType.WHITESPACE, "\n"),
            MdxToken(MdxType.DIVIDER, "line")
        )
        assertEquals(expected, actual)
    }
}