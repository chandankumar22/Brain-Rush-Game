package com.ck.dev.tiptap.ui.games.findthenumber

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ck.dev.tiptap.data.AppDatabaseHelperImpl
import com.ck.dev.tiptap.data.entity.BestScores
import com.ck.dev.tiptap.data.entity.Games
import com.ck.dev.tiptap.models.FindTheNumGameLevel
import com.ck.dev.tiptap.models.FindTheNumberGameRule
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONException
import timber.log.Timber

class FindTheNumberViewModel(private val database: AppDatabaseHelperImpl) : ViewModel() {

    suspend fun getGameDataByName(gameName: String): Games {
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

    suspend fun updateHighScoreIfApplicable(gameName: String, levelNum: String, score: Int) {
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
        if (highScore < score) {
            database.executeDbQuery(
                successMsg = "successfully updated high score for $gameName at level $levelNum with $score",
                errorMsg = "exception in updating high score for the $gameName"
            ) {
                viewModelScope.launch {
                    database.updateBestScore(BestScores(gameName, levelNum, score))
                }
            }
        } else {
            Timber.i("high score $highScore is greater than current score $score")
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
                if (existingGame.currentLevel.toInt() < nextLevel.toInt()) {
                    database.updateGameLevel(gameName, nextLevel)
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
}