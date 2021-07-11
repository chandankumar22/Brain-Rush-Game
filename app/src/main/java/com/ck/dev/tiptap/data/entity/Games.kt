package com.ck.dev.tiptap.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "games")
data class Games (
    @PrimaryKey
    @ColumnInfo(name = "gameName") var gameName: String,
    @ColumnInfo(name = "currentLevel") val currentLevel: String = "1",
    @ColumnInfo(name = "totalGamesPlayed") val totalGamesPlayed: Int = 0,
    @ColumnInfo(name = "totalTime") val totalTime :Long = 0L
)