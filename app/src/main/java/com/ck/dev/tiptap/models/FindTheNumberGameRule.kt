package com.ck.dev.tiptap.models

data class FindTheNumberGameRule(
    val level: String,
    val gridSize: Int,
    val visibleNumSize: Int,
    val time: Long,
    val coinsReqd:Int=0
)