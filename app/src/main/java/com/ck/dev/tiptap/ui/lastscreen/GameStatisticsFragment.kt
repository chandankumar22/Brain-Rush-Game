package com.ck.dev.tiptap.ui.lastscreen

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.ck.dev.tiptap.R
import com.ck.dev.tiptap.extensions.changeStatusBarColor
import com.ck.dev.tiptap.helpers.GameConstants
import com.ck.dev.tiptap.helpers.roundTo2Digit
import com.ck.dev.tiptap.ui.games.BaseFragment
import kotlinx.android.synthetic.main.fragment_game_statistics.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber

class GameStatisticsFragment : BaseFragment(R.layout.fragment_game_statistics) {

    companion object {
        @JvmStatic
        fun newInstance() = GameStatisticsFragment()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Timber.i("onViewCreated called")
        super.onViewCreated(view, savedInstanceState)
        setRememberCardStats()
        setFindTheNumStats()
        setJumbledWordsStats()
        (requireActivity() as AppCompatActivity).changeStatusBarColor(R.color.primaryLightColor)
    }

    private fun setRememberCardStats() {
        Timber.i("setRememberCardStats called")
        lifecycleScope.launchWhenCreated {
            val remCardDataTimeBound =
                viewModel.getGameDataByName(GameConstants.REMEMBER_THE_CARD_NAME_GAME_TIME_BOUND)
            val remCardDataEndless =
                viewModel.getGameDataByName(GameConstants.REMEMBER_THE_CARD_NAME_GAME_ENDLESS)
            val highScoreEndless =
                viewModel.getHighScoreAndLevel(GameConstants.REMEMBER_THE_CARD_NAME_GAME_ENDLESS)
            val highScoreTimeBound = viewModel.getHighScoreAndLevel(
                GameConstants.REMEMBER_THE_CARD_NAME_GAME_TIME_BOUND,
                true
            )
            withContext(Dispatchers.Main) {
                with(remCardDataTimeBound) {
                    if (this == null || highScoreEndless.isEmpty()) {
                        getString(R.string.never_played).apply {
                            rem_card_best_score_time_bound.text = this
                            rem_card_total_time_bound.visibility = View.GONE
                            rem_card_total_games_time_bound.visibility = View.GONE
                            rem_card_total_current_level_time_bound.visibility = View.GONE
                        }
                    } else {
                        rem_card_best_score_time_bound.text = getString(
                            R.string.best_score_in_level,
                            highScoreTimeBound[0],
                            highScoreTimeBound[1]
                        )
                        rem_card_total_time_bound.text = getString(
                            R.string.total_time_mins,
                            (totalTime / 60f).toDouble().roundTo2Digit().toString()
                        )
                        rem_card_total_games_time_bound.text = getString(
                            R.string.total_games,
                            totalGamesPlayed.toString()
                        )
                        rem_card_total_current_level_time_bound.text = getString(
                            R.string.current_level_on,
                            currentLevel
                        )
                    }
                }
                with(remCardDataEndless) {
                    if (this == null) {
                        getString(R.string.never_played).apply {
                            rem_card_best_score_endless.text = this
                            rem_card_total_time_endless.visibility = View.GONE
                            rem_card_total_games_endless.visibility = View.GONE
                            rem_card_current_level_endless.visibility = View.GONE
                        }
                    } else {
                        rem_card_best_score_endless.text = getString(
                            R.string.best_score_in_level,
                            highScoreEndless[0],
                            highScoreEndless[1]
                        )
                        rem_card_total_time_endless.text = getString(
                            R.string.total_time_mins,
                            (totalTime / 60f).toDouble().roundTo2Digit().toString()
                        )
                        rem_card_total_games_endless.text = getString(
                            R.string.total_games,
                            totalGamesPlayed.toString()
                        )
                        rem_card_current_level_endless.text = getString(
                            R.string.current_level_on,
                            currentLevel
                        )
                    }
                }
            }
        }
    }

    private fun setFindTheNumStats() {
        Timber.i("setFindTheNumStats called")
        lifecycleScope.launchWhenCreated {
            val dataTimeBound = viewModel.getGameDataByName(GameConstants.FIND_THE_NUMBER_GAME_NAME_TIME_BOUND)
            val dataEndless =
                viewModel.getGameDataByName(GameConstants.FIND_THE_NUMBER_GAME_NAME_ENDLESS)
            val highScoreEndless =
                viewModel.getHighScoreAndLevel(GameConstants.FIND_THE_NUMBER_GAME_NAME_ENDLESS)
            val highScoreTimeBound =
                viewModel.getHighScoreAndLevel(GameConstants.FIND_THE_NUMBER_GAME_NAME_TIME_BOUND)
            withContext(Dispatchers.Main) {
                with(dataTimeBound) {
                    if (this == null|| highScoreEndless.isEmpty()) {
                        getString(R.string.never_played).apply {
                            find_the_num_best_score_time_bound.text = this
                            find_the_num_total_time_bound.visibility = View.GONE
                            find_the_num_total_games_bound.visibility = View.GONE
                            find_the_current_level_time_bound.visibility = View.GONE
                        }
                    } else {
                        find_the_num_best_score_time_bound.text = getString(
                            R.string.best_score_in_level,
                            highScoreTimeBound[0],
                            highScoreTimeBound[1]
                        )
                        find_the_num_total_time_bound.text = getString(
                            R.string.total_time_mins,
                            (totalTime / 60f).toDouble().roundTo2Digit().toString()
                        )
                        find_the_num_total_games_bound.text = getString(
                            R.string.total_games,
                            totalGamesPlayed.toString()
                        )
                        find_the_current_level_time_bound.text = getString(
                            R.string.current_level_on,
                            currentLevel
                        )
                    }
                }
                with(dataEndless) {
                    if (this == null) {
                        getString(R.string.never_played).apply {
                            find_the_num_best_score_endless.text = this
                            find_the_num_total_time_endless.visibility = View.GONE
                            find_the_num_total_games_endless.visibility = View.GONE
                            find_the_num_current_level_endless.visibility = View.GONE
                        }
                    } else {
                        find_the_num_best_score_endless.text = getString(
                            R.string.best_score_in_level,
                            highScoreEndless[0],
                            highScoreEndless[1]
                        )
                        find_the_num_total_time_endless.text = getString(
                            R.string.total_time_mins,
                            (totalTime / 60f).toDouble().roundTo2Digit().toString()
                        )
                        find_the_num_total_games_endless.text = getString(
                            R.string.total_games,
                            totalGamesPlayed.toString()
                        )
                        find_the_num_current_level_endless.text = getString(
                            R.string.current_level_on,
                            currentLevel
                        )
                    }
                }
            }
        }
    }

    private fun setJumbledWordsStats() {
        Timber.i("setJumbledWordsStats called")
        lifecycleScope.launchWhenCreated {
            val dataEndless = viewModel.getGameDataByName(GameConstants.JUMBLED_NUMBER_GAME_NAME_ENDLESS)
            val highScoreTimeEndless =
                viewModel.getHighScoreAndLevel(GameConstants.JUMBLED_NUMBER_GAME_NAME_ENDLESS)

            val dataTimeBound = viewModel.getGameDataByName(GameConstants.JUMBLED_NUMBER_GAME_NAME_TIME_BOUND)
            val highScoreTimeBound =
                viewModel.getHighScoreAndLevel(GameConstants.JUMBLED_NUMBER_GAME_NAME_TIME_BOUND)

            withContext(Dispatchers.Main) {
                with(dataEndless) {
                    if (this == null) {
                        getString(R.string.never_played).apply {
                            jumbled_best_score_easy.text = this
                            jumbled_total_time_easy.visibility = View.GONE
                            jumbled_total_games_easy.visibility = View.GONE
                            jumbled_current_level_easy.visibility = View.GONE
                        }
                    } else {
                        jumbled_best_score_easy.text = getString(
                            R.string.best_score_in_level,
                            highScoreTimeEndless[0],
                            highScoreTimeEndless[1]
                        )
                        jumbled_total_time_easy.text = getString(
                            R.string.total_time_mins,
                            (totalTime / 60f).toDouble().roundTo2Digit().toString()
                        )
                        jumbled_total_games_easy.text = getString(
                            R.string.total_games,
                            totalGamesPlayed.toString()
                        )
                        jumbled_current_level_easy.text = getString(
                            R.string.current_level_on,
                            currentLevel
                        )
                    }
                }
                with(dataTimeBound) {
                    if (this == null) {
                        getString(R.string.never_played).apply {
                            jumbled_best_score_medium.text = this
                            jumbled_total_time_medium.visibility = View.GONE
                            jumbled_total_games_medium.visibility = View.GONE
                            jumbled_current_level_medium.visibility = View.GONE
                        }
                    } else {
                        jumbled_best_score_medium.text = getString(
                            R.string.best_score_in_level,
                            highScoreTimeBound[0],
                            highScoreTimeBound[1]
                        )
                        jumbled_total_time_medium.text = getString(
                            R.string.total_time_mins,
                            (totalTime / 60f).toDouble().roundTo2Digit().toString()
                        )
                        jumbled_total_games_medium.text = getString(
                            R.string.total_games,
                            totalGamesPlayed.toString()
                        )
                        jumbled_current_level_medium.text = getString(
                            R.string.current_level_on,
                            currentLevel
                        )
                    }
                }
            }
        }
    }

}