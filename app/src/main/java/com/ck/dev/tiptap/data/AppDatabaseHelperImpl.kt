package com.ck.dev.tiptap.data

import com.ck.dev.tiptap.data.entity.BestScores
import com.ck.dev.tiptap.data.entity.Games
import timber.log.Timber

class AppDatabaseHelperImpl(private val appDb: AppDatabase) : AppDatabaseHelper {

    override suspend fun getGameDataByName(gameName: String) =
        appDb.gamesDao().getGameDataByName(gameName)

    override suspend fun insertGame(gameName: Games) = appDb.gamesDao().insertGame(gameName)

    override suspend fun updateGameLevel(gameName: String, currentLevel: String) =
        appDb.gamesDao().updateCurrentLevel(gameName, currentLevel)


    override suspend fun getCompletedLevels(gameName: String) =
        appDb.gamesDao().getCompletedGameList(gameName)

    override suspend fun getHighScore(gameName: String, levelNum: String) =
        appDb.gamesDao().getHighScore(gameName, levelNum)

    override suspend fun insertBestScore(bestScores: BestScores) =
        appDb.gamesDao().insertHighScore(bestScores)

    override suspend fun updateBestScore(bestScores: BestScores) =
        appDb.gamesDao().updateHighScore(bestScores.gameName,bestScores.currentLevel,bestScores.bestScores!!)

    override suspend fun getHighScoreForAllLevels(gameName: String)=
        appDb.gamesDao().getHighScoreForAllLevels(gameName)

    fun executeDbQuery(successMsg:String = "",errorMsg:String = "",query: () -> Unit) {
        try {
            query()
            Timber.i("successfully executed db query")
            Timber.i(successMsg)
        } catch (ex: Exception) {
            Timber.e(ex, "exception in executing db query")
            Timber.i(errorMsg)
        }
    }

}