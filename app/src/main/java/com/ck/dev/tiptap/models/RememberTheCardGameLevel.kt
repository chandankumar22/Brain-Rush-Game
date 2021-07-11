package com.ck.dev.tiptap.models

data class RememberTheCardGameLevel(
    val levelNum: String,
    val isGameUnlocked: Boolean,
    val rule: RememberTheCardGameRule,
    val highScore:Int=0
)