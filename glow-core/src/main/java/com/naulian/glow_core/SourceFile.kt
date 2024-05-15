package com.naulian.glow_core

data class SourceFile(
    val tokenLines: List<SourceLine>
)

data class SourceLine(
    val number: Int = 0,
    val level: Int = 0,
    val value: String = "",
)