package com.ck.dev.tiptap.models

data class JumbledWordGameLevelData(
        val level: String,
        val unJumbledWords: Array<UnJumbledWord>,
        val word: String,
        val timeLimit:Int=0,
        val rowSize:Int,
        val colSize:Int
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as JumbledWordGameLevelData

        if (level != other.level) return false
        if (!unJumbledWords.contentEquals(other.unJumbledWords)) return false
        if (word != other.word) return false

        return true
    }

    override fun hashCode(): Int {
        var result = level.hashCode()
        result = 31 * result + unJumbledWords.contentHashCode()
        result = 31 * result + word.hashCode()
        return result
    }
}