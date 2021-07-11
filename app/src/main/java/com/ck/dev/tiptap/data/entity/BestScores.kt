package com.ck.dev.tiptap.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.ck.dev.tiptap.models.GameLevelData

@Entity(tableName = "best_scores")
data class BestScores (
    @ColumnInfo(name = "gameName") var gameName: String,
    @ColumnInfo(name = "levelNum") val currentLevel: String = "1",
    @ColumnInfo(name = "bestScores") val bestScores: Int?,
    @ColumnInfo(name = "gridSize") val gridSize: Int? = null,
    @ColumnInfo(name = "longestPlayed") val longestPlayed: Long?=null,
    @PrimaryKey(autoGenerate = true) val id:Int=0
)