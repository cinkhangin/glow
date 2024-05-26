package com.naulian.glow_core.atx

data class AtxNode(
    val kind: AtxKind,
    val children: List<AtxToken>
)
