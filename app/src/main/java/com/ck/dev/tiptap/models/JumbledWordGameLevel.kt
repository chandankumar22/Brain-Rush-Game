package com.ck.dev.tiptap.models

data class JumbledWordGameLevel(
        val level: String,
        val isGameUnlocked: Boolean,
        val bestTime: Int,
        val rule: JumbledWordGameLevelData)
