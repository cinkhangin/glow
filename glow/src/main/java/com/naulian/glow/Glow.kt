@file:Suppress("unused")

package com.naulian.glow

import com.naulian.anhance.html
import com.naulian.glow.language.kotlin.tokenizeKt
import com.naulian.glow.tokens.JTokens
import com.naulian.glow.tokens.JsTokens
import com.naulian.glow.tokens.PTokens
import com.naulian.glow_core.Type

fun color(hex: String): Int {
    return android.graphics.Color.parseColor(hex)
}

fun String.color(color: String) =
    "<font color=$color>$this</font>"

fun String.italic() = "<i>$this</i>"
fun String.bold() = "<b>$this</b>"

fun String.italic(rex: Regex) =
    replace(rex) { it.value.italic() }

fun String.bold(rex: Regex) =
    replace(rex) { it.value.bold() }

fun String.color(rex: Regex, color: String) =
    replace(rex) { it.value.color(color) }

fun String.color2(rex: Regex, color: String) =
    replace(rex) { "${it.groups[1]?.value} ${it.groups[2]?.value?.color(color)}" }


fun glowSyntax(
    source: String,
    language: String = "kotlin",
    theme: Theme = Theme(),
) = Glow.highlight(source, language, theme)


object Glow {
    private val TAG = Glow::class.java.simpleName

    fun highlight(source: String, language: String, theme: Theme = Theme()): HighLight {
        return when (language.lowercase()) {
            "java" -> hlJava(source, theme)
            "python", "py" -> hlPython(source, theme)
            "kotlin", "kt" -> hlKotlin(source, theme)
            "javascript", "js" -> hlJavaScript(source, theme)
            else -> hlText(source)
        }
    }

    fun hlText(input: String): HighLight {
        val raw = input
            .replace("  ", "&nbsp;&nbsp;")
            .replace("\n", "<br>")

        val spanned = raw.html()
        return HighLight(spanned, raw)
    }


    fun hlJava(input: String, theme: Theme = Theme()): HighLight {
        val tokens = JTokens.tokenize(input)

        val builder = StringBuilder()
        tokens.forEach {
            val code = when (it.type) {
                Type.LT, Type.GT -> {
                    it.value.replace(">", "&gt")
                        .replace("<", "&lt")
                        .color(theme.normal)
                }
                Type.KEYWORD -> it.value.color(theme.keyword)
                Type.PROPERTY -> it.value.color(theme.property)
                Type.VARIABLE -> it.value.color(theme.keyword)
                Type.VAR_NAME -> it.value.color(theme.variable)
                Type.CLASS -> it.value.color(theme.keyword)
                Type.FUNCTION -> it.value.color(theme.keyword)
                Type.FUNC_NAME -> it.value.color(theme.method)
                Type.FUNC_CALL -> it.value.color(theme.method)
                Type.NUMBER -> it.value.color(theme.number)
                Type.VALUE_INT -> it.value.color(theme.number)
                Type.VALUE_LONG -> it.value.color(theme.number)
                Type.VALUE_FLOAT -> it.value.color(theme.number)
                Type.CHAR -> it.value.color(theme.string)
                Type.STRING -> it.value.color(theme.string)
                Type.EQUAL_TO -> it.value.color(theme.normal)
                Type.M_COMMENT -> it.value.color(theme.comment)
                Type.S_COMMENT -> it.value.color(theme.comment)
                else -> it.value
            }
            builder.append(code)
        }

        val output = builder.toString()
            .replace("  ", "&nbsp;&nbsp;")
            .replace("\n", "<br>")

        val spanned = output.html()
        return HighLight(spanned, output)
    }

    fun hlJavaScript(input: String, theme: Theme = Theme()): HighLight {
        val tokens = JsTokens.tokenize(input)

        val builder = StringBuilder()
        tokens.forEach {
            val code = when (it.type) {
                Type.LT, Type.GT -> {
                    it.value.replace(">", "&gt")
                        .replace("<", "&lt")
                        .color(theme.normal)
                }

                Type.KEYWORD -> it.value.color(theme.keyword)
                Type.VARIABLE -> it.value.color(theme.keyword)
                Type.VAR_NAME -> it.value.color(theme.variable)
                Type.CLASS -> it.value.color(theme.keyword)
                Type.FUNCTION -> it.value.color(theme.keyword)
                Type.FUNC_NAME -> it.value.color(theme.method)
                Type.NUMBER -> it.value.color(theme.number)
                Type.VALUE_INT -> it.value.color(theme.number)
                Type.VALUE_LONG -> it.value.color(theme.number)
                Type.VALUE_FLOAT -> it.value.color(theme.number)
                Type.CHAR -> it.value.color(theme.string)
                Type.STRING -> it.value.color(theme.string)
                Type.EQUAL_TO -> it.value.color(theme.normal)
                Type.M_COMMENT -> it.value.color(theme.comment)
                Type.S_COMMENT -> it.value.color(theme.comment)
                else -> it.value
            }
            builder.append(code)
        }

        val output = builder.toString()
            .replace("  ", "&nbsp;&nbsp;")
            .replace("\n", "<br>")

        val spanned = output.html()
        return HighLight(spanned, output)
    }

    fun hlPython(input: String, theme: Theme = Theme()): HighLight {
        val tokens = PTokens.tokenize(input)

        val builder = StringBuilder()
        tokens.forEach {
            val code = when (it.type) {
                Type.LT, Type.GT -> {
                    it.value.replace(">", "&gt")
                        .replace("<", "&lt")
                        .color(theme.normal)
                }
                Type.KEYWORD -> it.value.color(theme.keyword)
                Type.PROPERTY -> it.value.color(theme.property)
                Type.CLASS -> it.value.color(theme.keyword)
                Type.FUNCTION -> it.value.color(theme.keyword)
                Type.FUNC_NAME -> it.value.color(theme.method)
                Type.FUNC_CALL -> it.value.color(theme.method)
                Type.NUMBER -> it.value.color(theme.number)
                Type.VALUE_INT -> it.value.color(theme.number)
                Type.VALUE_LONG -> it.value.color(theme.number)
                Type.VALUE_FLOAT -> it.value.color(theme.number)
                Type.STRING -> it.value.color(theme.string)
                Type.EQUAL_TO -> it.value.color(theme.normal)
                Type.S_COMMENT -> it.value.color(theme.comment)
                else -> it.value
            }
            builder.append(code)
        }

        val output = builder.toString()
            .replace("  ", "&nbsp;&nbsp;")
            .replace("\n", "<br>")

        val spanned = output.html()
        return HighLight(spanned, output)
    }

    fun hlKotlin(input: String, theme: Theme = Theme()): HighLight {
        val tokens = tokenizeKt(input)

        val builder = StringBuilder()
        tokens.forEach {
            val code = when (it.type) {
                Type.LT, Type.GT -> {
                    it.value.replace(">", "&gt")
                        .replace("<", "&lt")
                        .color(theme.normal)
                }
                Type.KEYWORD -> it.value.color(theme.keyword)
                Type.VARIABLE -> it.value.color(theme.keyword)
                Type.VAR_NAME -> it.value.color(theme.variable)
                Type.CLASS -> it.value.color(theme.keyword)
                Type.FUNCTION -> it.value.color(theme.keyword)
                Type.FUNC_NAME -> it.value.color(theme.method)
                Type.FUNC_CALL -> it.value.color(theme.method)
                Type.NUMBER -> it.value.color(theme.number)
                Type.VALUE_INT -> it.value.color(theme.number)
                Type.VALUE_LONG -> it.value.color(theme.number)
                Type.VALUE_FLOAT -> it.value.color(theme.number)
                Type.CHAR -> it.value.color(theme.string)
                Type.PROPERTY -> it.value.color(theme.property)
                Type.STRING -> it.value.color(theme.string)
                Type.STRING_BRACE -> it.value.color(theme.keyword)
                Type.INTERPOLATION -> it.value.color(theme.property)
                Type.EQUAL_TO -> it.value.color(theme.normal)
                Type.B_SCAPE -> it.value.color(theme.keyword)
                Type.M_COMMENT -> it.value.color(theme.comment)
                Type.S_COMMENT -> it.value.color(theme.comment)
                else -> it.value
            }
            builder.append(code)
        }

        val output = builder.toString()
            .replace("  ", "&nbsp;&nbsp;")
            .replace("\n", "<br>")

        val spanned = output.html()
        return HighLight(spanned, output)
    }
}
