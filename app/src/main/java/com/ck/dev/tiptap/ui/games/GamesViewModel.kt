package com.ck.dev.tiptap.ui.games

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ck.dev.tiptap.data.AppDatabaseHelperImpl
import com.ck.dev.tiptap.data.entity.BestScores
import com.ck.dev.tiptap.data.entity.Games
import com.ck.dev.tiptap.helpers.AppConstants
import com.ck.dev.tiptap.helpers.GameConstants.FIND_THE_NUMBER_GAME_NAME_TIME_BOUND
import com.ck.dev.tiptap.helpers.GameConstants.FIND_THE_NUMBER_GAME_NAME_ENDLESS
import com.ck.dev.tiptap.helpers.GameConstants.FTN_TB_LEVEL_COUNT
import com.ck.dev.tiptap.helpers.GameConstants.JUMBLED_NUMBER_GAME_NAME_ENDLESS
import com.ck.dev.tiptap.helpers.GameConstants.JUMBLED_NUMBER_GAME_NAME_TIME_BOUND
import com.ck.dev.tiptap.helpers.GameConstants.JW_ENDLESS_LEVEL_COUNT
import com.ck.dev.tiptap.helpers.GameConstants.JW_TB_LEVEL_COUNT
import com.ck.dev.tiptap.helpers.GameConstants.REMEMBER_THE_CARD_NAME_GAME_ENDLESS
import com.ck.dev.tiptap.helpers.GameConstants.REMEMBER_THE_CARD_NAME_GAME_TIME_BOUND
import com.ck.dev.tiptap.helpers.GameConstants.RTC_ENDLESS_LEVEL_COUNT
import com.ck.dev.tiptap.helpers.GameConstants.RTC_TB_LEVEL_COUNT
import com.ck.dev.tiptap.helpers.SharedPreferenceHelper
import com.ck.dev.tiptap.helpers.updateCoins
import com.ck.dev.tiptap.models.*
import com.ck.dev.tiptap.ui.GameApp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONException
import timber.log.Timber

class GamesViewModel(private val database: AppDatabaseHelperImpl) : ViewModel() {

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
            isLowScoreToSave: Boolean = false,
            coinsToAdd: Int = 0
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
                    updateCoins(coinsToAdd)
                }
            }
            return
        }
        //val isHighScore = if (isLowScoreToSave) highScore > score else highScore < score
        val scoreToUpdate = if (isLowScoreToSave) {
            when {
                highScore==0 -> {
                    updateCoins(coinsToAdd)
                    score
                }
                highScore > score -> {
                    updateCoins(coinsToAdd)
                    score
                }
                else -> {
                    updateCoins(coinsToAdd)
                    highScore
                }
            }
        } else {
            when {
                highScore == 0 || highScore== AppConstants.NOT_PLAYED_TAG -> {
                    updateCoins(coinsToAdd)
                    score
                }
                highScore < score -> {
                    updateCoins(coinsToAdd)
                    score
                }
                else -> {
                    updateCoins(coinsToAdd)
                    highScore
                }
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
        getRating()
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
                    val coinsReqd = gameRule.coins
                    if (gameData.currentLevel.isNotEmpty()) {
                        if (level.toInt() <= gameData.currentLevel.toInt()) {
                            val highScore = getHighScore(gameData.gameName, level) ?: AppConstants.NOT_PLAYED_TAG
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
                            val highScore = getHighScore(gameData.gameName, level) ?: AppConstants.NOT_PLAYED_TAG
                            list.add(
                                    JumbledWordGameLevel(
                                            level,
                                            true,
                                            highScore,
                                            JumbledWordGameLevelData(
                                                    level,
                                                    element.unJumbledWords,
                                                    element.word,
                                                    element.timeLimit,
                                                    element.rowSize,
                                                    element.colSize
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
                                                    element.timeLimit,
                                                    element.rowSize,
                                                    element.colSize
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
                            val highScore = getHighScore(gameData.gameName, level) ?: AppConstants.NOT_PLAYED_TAG
                            list.add(
                                    RememberTheCardGameLevel(
                                            level,
                                            true,
                                            RememberTheCardGameRule(
                                                    level,
                                                    element.row,
                                                    element.col,
                                                    element.timeLimit,
                                                    element.cardVisibleTime,
                                                    element.coins
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
                                                    element.cardVisibleTime,
                                                    element.coins
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
            longestPlayed: Long,
            coinsToAdd: Int = 0
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
        updateCoins(coinsToAdd)
    }

    suspend fun updateTotalGamePlayed(gameName: String) {
        Timber.i("updateTotalGamePlayed called")
        database.executeDbQuery {
            viewModelScope.launch {
                database.updateTotalGamePlayed(gameName)
            }
            if (gameName == REMEMBER_THE_CARD_NAME_GAME_ENDLESS || gameName == REMEMBER_THE_CARD_NAME_GAME_TIME_BOUND) {
                val value = GameApp.hasGame1Played.value
                if (value == null) GameApp.hasGame1Played.postValue(true)
                else GameApp.hasGame1Played.postValue(!value)
            } else if (gameName == FIND_THE_NUMBER_GAME_NAME_TIME_BOUND || gameName == FIND_THE_NUMBER_GAME_NAME_ENDLESS) {
                val value = GameApp.hasGame2Played.value
                if (value == null) GameApp.hasGame2Played.postValue(true)
                else GameApp.hasGame2Played.postValue(!value)
            } else {
                val value = GameApp.hasGame3Played.value
                if (value == null) GameApp.hasGame3Played.postValue(true)
                else GameApp.hasGame3Played.postValue(!value)
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

    suspend fun getHighScoreAndLevel(gameName: String, isLowestScore: Boolean = false): Array<String> {
        Timber.i("getHighScoreAndLevel called")
        return withContext(Dispatchers.IO) {
            val highScoreData = database.getHighScoreForAllLevels(gameName)
            var highScore = 0
            var level = ""
            for (i in highScoreData.indices) {
                if (isLowestScore) {
                    highScoreData[i].bestScores?.let {
                        if (highScore > it) {
                            highScore = it
                            level = highScoreData[i].currentLevel
                        }
                    }
                } else {
                    highScoreData[i].bestScores?.let {
                        if (highScore < it) {
                            highScore = it
                            level = highScoreData[i].currentLevel
                        }
                    }
                }
            }
            arrayOf(highScore.toString(), level)
        }
    }

    private suspend fun getRating() {
        Timber.i("getRating")
        val rtcTbGameData = getGameDataByName(REMEMBER_THE_CARD_NAME_GAME_TIME_BOUND)
        val rtcEndlessGameData = getGameDataByName(REMEMBER_THE_CARD_NAME_GAME_ENDLESS)
        val ftnTbGameData = getGameDataByName(FIND_THE_NUMBER_GAME_NAME_TIME_BOUND)
        val jwTbGameData = getGameDataByName(JUMBLED_NUMBER_GAME_NAME_TIME_BOUND)
        val jwEndlessGameData = getGameDataByName(JUMBLED_NUMBER_GAME_NAME_ENDLESS)
        var totalLevelsCompleted = 0
        rtcTbGameData?.let {
            totalLevelsCompleted=+it.currentLevel.toInt()-1
        }
        rtcEndlessGameData?.let {
            totalLevelsCompleted=+it.currentLevel.toInt()-1
        }
        ftnTbGameData?.let {
            totalLevelsCompleted=+it.currentLevel.toInt()-1
        }
        jwTbGameData?.let {
            totalLevelsCompleted=+it.currentLevel.toInt()-1
        }
        jwEndlessGameData?.let {
            totalLevelsCompleted=+it.currentLevel.toInt()-1
        }
        val ratingPercent = totalLevelsCompleted/(RTC_TB_LEVEL_COUNT+RTC_ENDLESS_LEVEL_COUNT+FTN_TB_LEVEL_COUNT+JW_TB_LEVEL_COUNT+JW_ENDLESS_LEVEL_COUNT).toFloat()
        Timber.i("Game completed level is $totalLevelsCompleted")
        Timber.i("Game complete level percent is $ratingPercent")
        val rat = 7*ratingPercent
        Timber.i("Rating percent is $rat")
        SharedPreferenceHelper.currentUserRating = rat
    }
}