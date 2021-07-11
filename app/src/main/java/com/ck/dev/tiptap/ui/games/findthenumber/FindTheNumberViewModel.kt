package com.ck.dev.tiptap.ui.games.findthenumber

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ck.dev.tiptap.data.AppDatabaseHelperImpl
import com.ck.dev.tiptap.data.entity.BestScores
import com.ck.dev.tiptap.data.entity.Games
import com.ck.dev.tiptap.models.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONException
import timber.log.Timber

class FindTheNumberViewModel(private val database: AppDatabaseHelperImpl) : ViewModel() {

    suspend fun getGameDataByName(gameName: String): Games? {
        Timber.i("getGameDataByName called")
        return withContext(Dispatchers.IO) {
            database.getGameDataByName(gameName)
        }
    }

    suspend fun getCompletedLevels(gameName: String): List<Games> {
        Timber.i("getCompletedLevels called")
        return withContext(Dispatchers.IO) {
            database.getCompletedLevels(gameName)
        }
    }

    suspend fun getHighScore(gameName: String, levelNum: String): Int? {
        Timber.i("getHighScore called")
        return withContext(Dispatchers.IO) {
            database.getHighScore(gameName, levelNum)
        }
    }

    suspend fun updateHighScoreIfApplicable(
        gameName: String,
        levelNum: String,
        score: Int,
        isLowScoreToSave: Boolean = false
    ) {
        Timber.i("updateHighScoreIfApplicable called")
        val highScore = database.getHighScore(gameName, levelNum)
        if (highScore == null) {
            database.executeDbQuery(
                successMsg = "successfully inserted high score for $gameName at level $levelNum with $score",
                errorMsg = "exception in inserting high score for the $gameName"
            ) {
                viewModelScope.launch {
                    database.insertBestScore(BestScores(gameName, levelNum, score))
                }
            }
            return
        }
        //val isHighScore = if (isLowScoreToSave) highScore > score else highScore < score
        val scoreToUpdate = if (isLowScoreToSave) {
            if (highScore > score) {
                score
            } else {
                highScore
            }
        } else {
            if (highScore < score) {
                highScore
            } else {
                score
            }
        }
        database.executeDbQuery(
            successMsg = "successfully updated high score for $gameName at level $levelNum with $score",
            errorMsg = "exception in updating high score for the $gameName"
        ) {
            viewModelScope.launch {
                database.updateBestScore(BestScores(gameName, levelNum, scoreToUpdate))
            }
        }
    }

    suspend fun insertGameIfNotAdded(gameName: String) {
        Timber.i("insertGameIfNotAdded called")
        if (getGameDataByName(gameName) == null) {
            database.executeDbQuery {
                viewModelScope.launch {
                    database.insertGame(Games(gameName))
                }
            }
        }
    }

    suspend fun updateGameLevel(gameName: String, nextLevel: String) {
        Timber.i("updateGameLevel called")
        database.executeDbQuery {
            viewModelScope.launch {
                val existingGame = getGameDataByName(gameName)
                existingGame?.let {
                    if (existingGame.currentLevel.toInt() < nextLevel.toInt()) {
                        database.updateGameLevel(gameName, nextLevel)
                    }
                }
            }
        }
    }

    suspend fun getFindTheNumGameRules(
        gameData: Games,
        gameRulesList: Array<FindTheNumberGameRule>
    ): ArrayList<FindTheNumGameLevel> {
        Timber.i("getFindTheNumGameRules called")
        return withContext(Dispatchers.Main) {
            val list = ArrayList<FindTheNumGameLevel>()
            try {
                for (element in gameRulesList) {
                    val gameRule: FindTheNumberGameRule = element
                    val level = gameRule.level
                    val gridSize = gameRule.gridSize
                    val visibleNumSize = gameRule.visibleNumSize
                    val time = gameRule.time
                    val coinsReqd = gameRule.coinsReqd
                    if (gameData.currentLevel.isNotEmpty()) {
                        if (level.toInt() <= gameData.currentLevel.toInt()) {
                            val highScore = getHighScore(gameData.gameName, level) ?: 0
                            list.add(
                                FindTheNumGameLevel(
                                    level,
                                    true,
                                    FindTheNumberGameRule(
                                        level,
                                        gridSize,
                                        visibleNumSize,
                                        time,
                                        coinsReqd
                                    ), highScore
                                )
                            )
                        } else {
                            list.add(
                                FindTheNumGameLevel(
                                    level,
                                    false,
                                    FindTheNumberGameRule(
                                        level,
                                        gridSize,
                                        visibleNumSize,
                                        time,
                                        coinsReqd
                                    )
                                )
                            )
                        }
                    }
                }
            } catch (ex: JSONException) {
                Timber.e(ex, "exception while extracting game rules from json string")
            }
            list
        }
    }


    suspend fun getJumbledWordGameRules(
        gameData: Games,
        gameRulesList: Array<JumbledWordGameLevelData>
    ): ArrayList<JumbledWordGameLevel> {
        Timber.i("getJumbledWordGameRules called")
        return withContext(Dispatchers.Main) {
            val list = ArrayList<JumbledWordGameLevel>()
            try {
                for (element in gameRulesList) {
                    val level = element.level
                    if (gameData.currentLevel.isNotEmpty()) {
                        if (level.toInt() <= gameData.currentLevel.toInt()) {
                            val highScore = getHighScore(gameData.gameName, level) ?: 0
                            list.add(
                                JumbledWordGameLevel(
                                    level,
                                    true,
                                    highScore,
                                    JumbledWordGameLevelData(
                                        level,
                                        element.unJumbledWords,
                                        element.word,
                                        element.timeLimit
                                    )
                                )
                            )
                        } else {
                            list.add(
                                JumbledWordGameLevel(
                                    level,
                                    false,
                                    0,
                                    JumbledWordGameLevelData(
                                        level,
                                        element.unJumbledWords,
                                        element.word,
                                        element.timeLimit
                                    )
                                )
                            )
                        }
                    }
                }
            } catch (ex: JSONException) {
                Timber.e(ex, "exception while extracting game rules from json string")
            }
            list
        }
    }

    suspend fun getRememberTheCardGameRules(
        gameData: Games,
        gameRulesList: Array<RememberTheCardGameRule>
    ): ArrayList<RememberTheCardGameLevel> {
        Timber.i("getRememberTheCardGameRules called")
        return withContext(Dispatchers.Main) {
            val list = ArrayList<RememberTheCardGameLevel>()
            try {
                for (element in gameRulesList) {
                    val level = element.level
                    if (gameData.currentLevel.isNotEmpty()) {
                        if (level.toInt() <= gameData.currentLevel.toInt()) {
                            val highScore = getHighScore(gameData.gameName, level) ?: 0
                            list.add(
                                RememberTheCardGameLevel(
                                    level,
                                    true,
                                    RememberTheCardGameRule(
                                        level,
                                        element.row,
                                        element.col,
                                        element.timeLimit,
                                        element.cardVisibleTime
                                    ),
                                    highScore
                                )
                            )
                        } else {
                            list.add(
                                RememberTheCardGameLevel(
                                    level,
                                    false,
                                    RememberTheCardGameRule(
                                        level,
                                        element.row,
                                        element.col,
                                        element.timeLimit,
                                        element.cardVisibleTime
                                    )
                                )
                            )
                        }
                    }
                }
            } catch (ex: JSONException) {
                Timber.e(ex, "exception while extracting game rules from json string")
            }
            list
        }
    }

    suspend fun getHighScoreForInfinite(gameName: String, gridSize: Int): BestScores {
        Timber.i("getHighScoreForInfinite called")
        return withContext(Dispatchers.IO) {
            database.getHighScoreForInfinite(gameName, gridSize)
        }
    }

    suspend fun updateHighScoreForInfinite(
        gameName: String,
        gridSize: Int,
        highScore: Int,
        longestPlayed: Long
    ) {
        Timber.i("updateHighScoreForInfinite called")
        val existingData = getHighScoreForInfinite(gameName, gridSize)
        if (existingData == null) {
            database.executeDbQuery {
                viewModelScope.launch {
                    database.insertBestScore(
                        BestScores(
                            gameName,
                            "0",
                            highScore,
                            gridSize,
                            longestPlayed
                        )
                    )
                }
            }
        } else {
            if (existingData.bestScores == null || existingData.bestScores < highScore) {
                database.executeDbQuery {
                    viewModelScope.launch {
                        database.updateBestScoreForInfiniteGame(gameName, gridSize, highScore)

                    }
                }
            }
            if (existingData.longestPlayed == null || existingData.longestPlayed < longestPlayed) {
                database.executeDbQuery {
                    viewModelScope.launch {
                        database.updateLongestPlayedForInfiniteGame(
                            gameName,
                            gridSize,
                            longestPlayed
                        )

                    }
                }
            }
        }
    }

    suspend fun updateTotalGamePlayed(gameName: String) {
        Timber.i("updateTotalGamePlayed called")
        database.executeDbQuery {
            viewModelScope.launch {
                database.updateTotalGamePlayed(gameName)
            }
        }
    }

    suspend fun updateTotalTimePlayed(gameName: String, totalTime: Long) {
        Timber.i("updateTotalTimePlayed called")
        database.executeDbQuery {
            viewModelScope.launch {
                database.updateTotalTimePlayed(gameName, totalTime)
            }
        }
    }
}