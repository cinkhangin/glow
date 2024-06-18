package com.naulian.glow_core

import com.naulian.glow_core.mdx.MdxPreProcessor
import org.junit.Assert.assertEquals
import org.junit.Test

class MdxPreProcessorTest {

    @Test
    fun testHeader() {
        val source = """
            #1 this is heading 1
            #6 this is heading 6
        """.trimIndent()

        val actual = MdxPreProcessor(source).process()
        val expected = listOf(
            "#1 this is heading 1mdx.nl",
            "#6 this is heading 6mdx.nl"
        )
        assertEquals(expected, actual)
    }

    @Test
    fun testDivider() {
        val source = """
            normal text
            =line=
        """.trimIndent()
        val actual = MdxPreProcessor(source).process()
        val expected = listOf(
            "normal textmdx.nl",
            "=line="
        )
        assertEquals(expected, actual)
    }
}