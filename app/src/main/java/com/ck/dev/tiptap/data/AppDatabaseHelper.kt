package com.ck.dev.tiptap.data

import com.ck.dev.tiptap.data.entity.BestScores
import com.ck.dev.tiptap.data.entity.Games
import com.ck.dev.tiptap.models.GameLevelData

interface AppDatabaseHelper {
    suspend fun getGameDataByName(gameName:String): Games
    suspend fun getCompletedLevels(gameName:String): List<Games>
    suspend fun insertGame(gameName:Games)
    suspend fun updateGameLevel(gameName: String,currentLevel:String)
    suspend fun getHighScore(gameName: String,levelNum:String):Int?
    suspend fun insertBestScore(bestScores: BestScores)
    suspend fun getHighScoreForAllLevels(gameName: String):List<BestScores>
    suspend fun updateBestScore(bestScores: BestScores)
}