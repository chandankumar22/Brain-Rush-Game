package com.ck.dev.tiptap.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.ck.dev.tiptap.models.GameLevelData

@Entity(tableName = "games")
data class Games (
    @PrimaryKey
    @ColumnInfo(name = "gameName") var gameName: String,
    @ColumnInfo(name = "currentLevel") val currentLevel: String = "1"
)