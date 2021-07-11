package com.ck.dev.tiptap.models

data class JumbledWord(
        var position: String,
        var isIncluded: Boolean,
        var letter:Char,
        var isReveled:Boolean
)