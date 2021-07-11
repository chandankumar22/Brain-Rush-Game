package com.ck.dev.tiptap.models

data class FindTheNumGameLevel(
    val levelNum: String,
    val isGameUnlocked: Boolean,
    val rule: FindTheNumberGameRule,
    val highScore:Int=0
)