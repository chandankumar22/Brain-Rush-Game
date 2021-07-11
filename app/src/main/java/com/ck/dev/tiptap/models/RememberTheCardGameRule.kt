package com.ck.dev.tiptap.models

data class RememberTheCardGameRule(
    val level: String,
    val row: Int,
    val col: Int,
    val timeLimit: Long,
    val cardVisibleTime: Int,
    val coins:Int
)