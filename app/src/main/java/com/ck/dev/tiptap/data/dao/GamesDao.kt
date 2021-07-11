package com.ck.dev.tiptap.data.dao

import androidx.room.*
import com.ck.dev.tiptap.data.entity.BestScores
import com.ck.dev.tiptap.data.entity.Games
import com.ck.dev.tiptap.models.GameLevelData

@Dao
interface GamesDao {

    @Query("SELECT * FROM games WHERE gameName=:gameName")
    suspend fun getGameDataByName(gameName: String): Games

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertGame(game: Games)

    @Query("UPDATE games SET currentLevel = :levelNum WHERE gameName = :gameName")
    suspend fun updateCurrentLevel(gameName:String,levelNum: String)

    @Query("SELECT * FROM games as g ,best_scores as b WHERE g.gameName = :gameName AND g.gameName = b.gameName")
    suspend fun getCompletedGameList(gameName:String): List<Games>

    @Query("SELECT bestScores FROM best_scores WHERE gameName = :gameName AND levelNum = :levelNum")
    suspend fun getHighScore(gameName: String,levelNum:String):Int?

    @Query("SELECT * FROM best_scores WHERE gameName = :gameName")
    suspend fun getHighScoreForAllLevels(gameName: String):List<BestScores>

   @Insert
    suspend fun insertHighScore(bestScore: BestScores)

    @Query("UPDATE best_scores SET bestScores = :highScore WHERE gameName = :gameName AND levelNum =:levelNum")
    suspend fun updateHighScore(gameName: String,levelNum: String,highScore:Int)

}