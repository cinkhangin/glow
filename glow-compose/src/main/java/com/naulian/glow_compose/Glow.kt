package com.naulian.glow_compose

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.withStyle
import com.naulian.anhance.logError
import com.naulian.glow.Theme
import com.naulian.glow.tokens.JTokens
import com.naulian.glow.tokens.JsTokens
import com.naulian.glow.tokens.PTokens
import com.naulian.glow_compose.kotlin.tokenizeKt
import android.graphics.Color as LegacyColor

val font = FontFamily(Font(R.font.jetbrains_mono))
fun spanStyle(color: Color) = SpanStyle(
    color = color, fontFamily = font
)

fun String.toAnnotatedString(): AnnotatedString {
    return buildAnnotatedString {
        append(this@toAnnotatedString)
    }
}

fun String.hexToColor(): Color {
    return if (startsWith("#")) {
        val legacyColor = LegacyColor.parseColor(this)
        Color(legacyColor)
    } else {
        logError("GLow: toComposeColor", "color should start with #. at $this")
        Color.Black
    }
}

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
        val annotatedString = input.toAnnotatedString()
        return HighLight(annotatedString, input)
    }


    fun hlJava(input: String, theme: Theme = Theme()): HighLight {
        val tokens = JTokens.tokenize(input)

        val builder = buildAnnotatedString {
            tokens.forEach {
                val hlColor = when (it.type) {
                    com.naulian.glow_core.Type.LT, com.naulian.glow_core.Type.GT -> theme.normal
                    com.naulian.glow_core.Type.KEYWORD -> theme.keyword
                    com.naulian.glow_core.Type.PROPERTY -> theme.property
                    com.naulian.glow_core.Type.VARIABLE -> theme.keyword
                    com.naulian.glow_core.Type.VAR_NAME -> theme.variable
                    com.naulian.glow_core.Type.CLASS -> theme.keyword
                    com.naulian.glow_core.Type.FUNCTION -> theme.keyword
                    com.naulian.glow_core.Type.FUNC_NAME -> theme.method
                    com.naulian.glow_core.Type.FUNC_CALL -> theme.method
                    com.naulian.glow_core.Type.NUMBER -> theme.number
                    com.naulian.glow_core.Type.VALUE_INT -> theme.number
                    com.naulian.glow_core.Type.VALUE_LONG -> theme.number
                    com.naulian.glow_core.Type.VALUE_FLOAT -> theme.number
                    com.naulian.glow_core.Type.CHAR -> theme.string
                    com.naulian.glow_core.Type.STRING -> theme.string
                    com.naulian.glow_core.Type.EQUAL_TO -> theme.normal
                    com.naulian.glow_core.Type.M_COMMENT -> theme.comment
                    com.naulian.glow_core.Type.S_COMMENT -> theme.comment
                    else -> theme.normal
                }.hexToColor()

                val hlCode = it.value
                withStyle(style = spanStyle(color = hlColor)) {
                    append(hlCode)
                }
            }
        }

        return HighLight(builder, builder.toString())
    }

    fun hlJavaScript(input: String, theme: Theme = Theme()): HighLight {
        val tokens = JsTokens.tokenize(input)

        val builder = buildAnnotatedString {
            tokens.forEach {
                val hlColor = when (it.type) {
                    com.naulian.glow_core.Type.LT, com.naulian.glow_core.Type.GT -> theme.normal
                    com.naulian.glow_core.Type.KEYWORD -> theme.keyword
                    com.naulian.glow_core.Type.VARIABLE -> theme.keyword
                    com.naulian.glow_core.Type.VAR_NAME -> theme.variable
                    com.naulian.glow_core.Type.CLASS -> theme.keyword
                    com.naulian.glow_core.Type.FUNCTION -> theme.keyword
                    com.naulian.glow_core.Type.FUNC_NAME -> theme.method
                    com.naulian.glow_core.Type.NUMBER -> theme.number
                    com.naulian.glow_core.Type.VALUE_INT -> theme.number
                    com.naulian.glow_core.Type.VALUE_LONG -> theme.number
                    com.naulian.glow_core.Type.VALUE_FLOAT -> theme.number
                    com.naulian.glow_core.Type.CHAR -> theme.string
                    com.naulian.glow_core.Type.STRING -> theme.string
                    com.naulian.glow_core.Type.EQUAL_TO -> theme.normal
                    com.naulian.glow_core.Type.M_COMMENT -> theme.comment
                    com.naulian.glow_core.Type.S_COMMENT -> theme.comment
                    else -> theme.normal
                }.hexToColor()

                val hlCode = it.value

                withStyle(style = spanStyle(color = hlColor)) {
                    append(hlCode)
                }
            }
        }

        return HighLight(builder, builder.toString())
    }

    fun hlPython(input: String, theme: Theme = Theme()): HighLight {
        val tokens = PTokens.tokenize(input)

        val builder = buildAnnotatedString {
            tokens.forEach {
                val hlColor = when (it.type) {
                    com.naulian.glow_core.Type.LT, com.naulian.glow_core.Type.GT -> theme.normal
                    com.naulian.glow_core.Type.KEYWORD -> theme.keyword
                    com.naulian.glow_core.Type.PROPERTY -> theme.property
                    com.naulian.glow_core.Type.CLASS -> theme.keyword
                    com.naulian.glow_core.Type.FUNCTION -> theme.keyword
                    com.naulian.glow_core.Type.FUNC_NAME -> theme.method
                    com.naulian.glow_core.Type.FUNC_CALL -> theme.method
                    com.naulian.glow_core.Type.NUMBER -> theme.number
                    com.naulian.glow_core.Type.VALUE_INT -> theme.number
                    com.naulian.glow_core.Type.VALUE_LONG -> theme.number
                    com.naulian.glow_core.Type.VALUE_FLOAT -> theme.number
                    com.naulian.glow_core.Type.STRING -> theme.string
                    com.naulian.glow_core.Type.EQUAL_TO -> theme.normal
                    com.naulian.glow_core.Type.S_COMMENT -> theme.comment
                    else -> theme.normal
                }.hexToColor()

                val hlCode = it.value
                withStyle(style = spanStyle(color = hlColor)) {
                    append(hlCode)
                }
            }
        }

        return HighLight(builder, builder.toString())
    }

    fun hlKotlin(input: String, theme: Theme = Theme()): HighLight {
        val tokens = tokenizeKt(input)

        val builder = buildAnnotatedString {
            tokens.forEach {
                val hlColor = when (it.type) {
                    com.naulian.glow_core.Type.LT, com.naulian.glow_core.Type.GT -> theme.normal
                    com.naulian.glow_core.Type.KEYWORD -> theme.keyword
                    com.naulian.glow_core.Type.VARIABLE -> theme.keyword
                    com.naulian.glow_core.Type.VAR_NAME -> theme.variable
                    com.naulian.glow_core.Type.CLASS -> theme.keyword
                    com.naulian.glow_core.Type.FUNCTION -> theme.keyword
                    com.naulian.glow_core.Type.FUNC_NAME -> theme.method
                    com.naulian.glow_core.Type.FUNC_CALL -> theme.method
                    com.naulian.glow_core.Type.NUMBER -> theme.number
                    com.naulian.glow_core.Type.VALUE_INT -> theme.number
                    com.naulian.glow_core.Type.VALUE_LONG -> theme.number
                    com.naulian.glow_core.Type.VALUE_FLOAT -> theme.number
                    com.naulian.glow_core.Type.CHAR -> theme.string
                    com.naulian.glow_core.Type.PROPERTY -> theme.property
                    com.naulian.glow_core.Type.STRING -> theme.string
                    com.naulian.glow_core.Type.STRING_BRACE -> theme.keyword
                    com.naulian.glow_core.Type.INTERPOLATION -> theme.property
                    com.naulian.glow_core.Type.EQUAL_TO -> theme.normal
                    com.naulian.glow_core.Type.B_SCAPE -> theme.keyword
                    com.naulian.glow_core.Type.M_COMMENT -> theme.comment
                    com.naulian.glow_core.Type.S_COMMENT -> theme.comment
                    else -> theme.normal
                }.hexToColor()

                val hlCode = it.value
                withStyle(style = spanStyle(color = hlColor)) {
                    append(hlCode)
                }
            }
        }
        return HighLight(builder, builder.toString())
    }
}