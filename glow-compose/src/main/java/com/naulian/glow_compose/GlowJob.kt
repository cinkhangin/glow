@file:Suppress("unused")

package com.naulian.glow_compose

import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import com.naulian.glow.Theme
import com.naulian.glow.tokens.JTokens
import com.naulian.glow.tokens.JsTokens
import com.naulian.glow.tokens.PTokens
import com.naulian.glow.tokens.TxtTokens
import com.naulian.glow_compose.kotlin.tokenizeKt
import com.naulian.glow_core.Token
import com.naulian.glow_core.Type

fun launchGlowJob(source: String, language: String, theme: Theme): GlowJob {
    return GlowJob().launch(source, language, theme)
}

class GlowJob {
    private var _tokens = emptyList<Token>()
    val tokens get() = _tokens

    private var _highLight = HighLight(value = buildAnnotatedString { })
    val highLight get() = _highLight

    fun launch(source: String, language: String, theme: Theme): GlowJob {
        tokenize(source, language)
        highLight(language, theme)
        return this
    }

    private fun highLight(language: String, theme: Theme) {
        _highLight = when (language.lowercase()) {
            "java" -> hlJava(theme)
            "python", "py" -> hlPython(theme)
            "kotlin", "kt" -> hlKotlin(theme)
            "javascript", "js" -> hlJavaScript(theme)
            else -> hlText()
        }
    }

    private fun hlText(): HighLight {
        val annotatedString = buildAnnotatedString {
            tokens.forEach { append(it.value) }
        }
        return HighLight(annotatedString, annotatedString.text)
    }


    private fun hlJava(theme: Theme = Theme()): HighLight {
        val builder = buildAnnotatedString {
            tokens.forEach {
                val hlColor = when (it.type) {
                    Type.LT, Type.GT -> theme.normal
                    Type.KEYWORD -> theme.keyword
                    Type.PROPERTY -> theme.property
                    Type.VARIABLE -> theme.keyword
                    Type.VAR_NAME -> theme.variable
                    Type.CLASS -> theme.keyword
                    Type.FUNCTION -> theme.keyword
                    Type.FUNC_NAME -> theme.method
                    Type.FUNC_CALL -> theme.method
                    Type.NUMBER -> theme.number
                    Type.VALUE_INT -> theme.number
                    Type.VALUE_LONG -> theme.number
                    Type.VALUE_FLOAT -> theme.number
                    Type.CHAR -> theme.string
                    Type.STRING -> theme.string
                    Type.ASSIGNMENT -> theme.normal
                    Type.MCOMMENT -> theme.comment
                    Type.SCOMMENT -> theme.comment
                    else -> theme.normal
                }.toComposeColor()

                val hlCode = it.value
                withStyle(style = spanStyle(color = hlColor)) {
                    append(hlCode)
                }
            }
        }

        return HighLight(builder, builder.toString())
    }

    private fun hlJavaScript(theme: Theme = Theme()): HighLight {
        val builder = buildAnnotatedString {
            tokens.forEach {
                val hlColor = when (it.type) {
                    Type.LT, Type.GT -> theme.normal
                    Type.KEYWORD -> theme.keyword
                    Type.VARIABLE -> theme.keyword
                    Type.VAR_NAME -> theme.variable
                    Type.CLASS -> theme.keyword
                    Type.FUNCTION -> theme.keyword
                    Type.FUNC_NAME -> theme.method
                    Type.NUMBER -> theme.number
                    Type.VALUE_INT -> theme.number
                    Type.VALUE_LONG -> theme.number
                    Type.VALUE_FLOAT -> theme.number
                    Type.CHAR -> theme.string
                    Type.STRING -> theme.string
                    Type.ASSIGNMENT -> theme.normal
                    Type.MCOMMENT -> theme.comment
                    Type.SCOMMENT -> theme.comment
                    else -> theme.normal
                }.toComposeColor()

                val hlCode = it.value

                withStyle(style = spanStyle(color = hlColor)) {
                    append(hlCode)
                }
            }
        }

        return HighLight(builder, builder.toString())
    }

    private fun hlPython(theme: Theme = Theme()): HighLight {
        val builder = buildAnnotatedString {
            tokens.forEach {
                val hlColor = when (it.type) {
                    Type.LT, Type.GT -> theme.normal
                    Type.KEYWORD -> theme.keyword
                    Type.PROPERTY -> theme.property
                    Type.CLASS -> theme.keyword
                    Type.FUNCTION -> theme.keyword
                    Type.FUNC_NAME -> theme.method
                    Type.FUNC_CALL -> theme.method
                    Type.NUMBER -> theme.number
                    Type.VALUE_INT -> theme.number
                    Type.VALUE_LONG -> theme.number
                    Type.VALUE_FLOAT -> theme.number
                    Type.STRING -> theme.string
                    Type.ASSIGNMENT -> theme.normal
                    Type.SCOMMENT -> theme.comment
                    else -> theme.normal
                }.toComposeColor()

                val hlCode = it.value
                withStyle(style = spanStyle(color = hlColor)) {
                    append(hlCode)
                }
            }
        }

        return HighLight(builder, builder.toString())
    }

    private fun hlKotlin(theme: Theme = Theme()): HighLight {
        val builder = buildAnnotatedString {
            tokens.forEach {
                val hlColor = when (it.type) {
                    Type.LT, Type.GT -> theme.normal
                    Type.KEYWORD -> theme.keyword
                    Type.VARIABLE -> theme.keyword
                    Type.VAR_NAME -> theme.variable
                    Type.CLASS -> theme.keyword
                    Type.FUNCTION -> theme.keyword
                    Type.FUNC_NAME -> theme.method
                    Type.FUNC_CALL -> theme.method
                    Type.NUMBER -> theme.number
                    Type.VALUE_INT -> theme.number
                    Type.VALUE_LONG -> theme.number
                    Type.VALUE_FLOAT -> theme.number
                    Type.CHAR -> theme.string
                    Type.PROPERTY -> theme.property
                    Type.STRING -> theme.string
                    Type.STRING_BRACE -> theme.keyword
                    Type.INTERPOLATION -> theme.property
                    Type.ASSIGNMENT -> theme.normal
                    Type.ESCAPE -> theme.keyword
                    Type.MCOMMENT -> theme.comment
                    Type.SCOMMENT -> theme.comment
                    else -> theme.normal
                }.toComposeColor()

                val hlCode = it.value
                withStyle(style = spanStyle(color = hlColor)) {
                    append(hlCode)
                }
            }
        }
        return HighLight(builder, builder.toString())
    }

    private fun tokenize(source: String, language: String) {
        _tokens = when (language.lowercase()) {
            "java" -> JTokens.tokenize(source)
            "python", "py" -> PTokens.tokenize(source)
            "kotlin", "kt" -> tokenizeKt(source)
            "javascript", "js" -> JsTokens.tokenize(source)
            else -> TxtTokens.tokenize(source)
        }
    }
}

