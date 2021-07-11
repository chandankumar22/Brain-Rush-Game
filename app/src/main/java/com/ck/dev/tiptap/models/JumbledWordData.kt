package com.ck.dev.tiptap.models

data class JumbledWordData(
        var position: String,
        var isIncluded: Boolean,
        var letter: Char,
        var isReveled: Boolean
)