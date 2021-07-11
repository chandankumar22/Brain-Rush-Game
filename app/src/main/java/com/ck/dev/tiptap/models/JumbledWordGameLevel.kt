package com.ck.dev.tiptap.models

data class JumbledWordGameLevel(
        val level: String,
        val isGameUnlocked: Boolean,
        val stars: Int,
        val rule: JumbledWordGameLevelData)
