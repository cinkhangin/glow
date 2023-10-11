@file:Suppress("unused")

package com.naulian.glow

import com.naulian.anhance.html
import com.naulian.glow.tokens.JTokens
import com.naulian.glow.tokens.JsTokens
import com.naulian.glow.tokens.KTokens
import com.naulian.glow.tokens.PTokens
import com.naulian.glow.tokens.Type

private fun color(hex: String): Int {
    return android.graphics.Color.parseColor(hex)
}

private fun String.color(color: String) =
    "<font color=$color>$this</font>"

private fun String.italic() = "<i>$this</i>"
private fun String.bold() = "<b>$this</b>"

private fun String.italic(rex: Regex) =
    replace(rex) { it.value.italic() }

private fun String.bold(rex: Regex) =
    replace(rex) { it.value.bold() }

private fun String.color(rex: Regex, color: String) =
    replace(rex) { it.value.color(color) }

private fun String.color2(rex: Regex, color: String) =
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
        println(tokens)

        val builder = StringBuilder()
        tokens.forEach {
            val code = when (it.type) {
                Type.LT, Type.GT -> it.value.color(theme.normal)
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
                Type.ASSIGNMENT -> it.value.color(theme.normal)
                Type.COMMENT_MULTI -> it.value.color(theme.comment)
                Type.COMMENT_SINGLE -> it.value.color(theme.comment)
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
        val modified = input.replace("<", "&lt")
            .replace(">", "&gt")

        val tokens = JsTokens.tokenize(modified)

        val builder = StringBuilder()
        tokens.forEach {
            val code = when (it.type) {
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
                Type.ASSIGNMENT -> it.value.color(theme.normal)
                Type.COMMENT_MULTI -> it.value.color(theme.comment)
                Type.COMMENT_SINGLE -> it.value.color(theme.comment)
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
        val modified = input.replace("<", "&lt")
            .replace(">", "&gt")

        val tokens = PTokens.tokenize(modified)

        val builder = StringBuilder()
        tokens.forEach {
            val code = when (it.type) {
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
                Type.ASSIGNMENT -> it.value.color(theme.normal)
                Type.COMMENT_SINGLE -> it.value.color(theme.comment)
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
        val modified = input.replace("<", "&lt")
            .replace(">", "&gt")

        val tokens = KTokens.tokenize(modified)

        val builder = StringBuilder()
        tokens.forEach {
            val code = when (it.type) {
                Type.LT, Type.GT -> it.value.color(theme.normal)
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
                Type.ASSIGNMENT -> it.value.color(theme.normal)
                Type.COMMENT_MULTI -> it.value.color(theme.comment)
                Type.COMMENT_SINGLE -> it.value.color(theme.comment)
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
