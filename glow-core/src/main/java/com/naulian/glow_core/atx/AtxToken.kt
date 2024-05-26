package com.naulian.glow_core.atx

data class AtxToken(
    val type: AtxType,
    val text: String
) {
    companion object {
        val END = AtxToken(AtxType.END, "")
    }
}
