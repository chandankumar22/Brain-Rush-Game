package com.ck.dev.tiptap.ui.games.jumbledwords

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.addCallback
import androidx.lifecycle.lifecycleScope
import com.ck.dev.tiptap.R
import com.ck.dev.tiptap.helpers.GameConstants
import com.ck.dev.tiptap.helpers.GameConstants.EASY_MODE
import com.ck.dev.tiptap.helpers.GameConstants.HARD_MODE
import com.ck.dev.tiptap.helpers.GameConstants.MEDIUM_MODE
import com.ck.dev.tiptap.helpers.roundTo2Digit
import com.ck.dev.tiptap.ui.games.BaseFragment
import kotlinx.android.synthetic.main.fragment_jumbled_words_game.*
import timber.log.Timber

class JumbledWordsGameFragment : BaseFragment(R.layout.fragment_jumbled_words_game) {
    companion object {
        @JvmStatic
        fun newInstance() = JumbledWordsGameFragment()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Timber.i("onViewCreated called")
        super.onViewCreated(view, savedInstanceState)
        setListeners()
        getAndDisplayHighScore()
    }

    private fun setListeners() {
        Timber.i("setListeners called")
        requireActivity().onBackPressedDispatcher.addCallback(this) {
            requireActivity().finish()
        }
        easy_play.setOnClickListener {
            Timber.i("easy_play.onclick called")
            val intent = Intent(requireContext(), JumbledWordsActivity::class.java)
            intent.putExtra("gameMode", EASY_MODE)
            startActivity(intent)
        }
        medium_play.setOnClickListener {
            Timber.i("medium_play.onclick called")
            val intent = Intent(requireContext(), JumbledWordsActivity::class.java)
            intent.putExtra("gameMode", MEDIUM_MODE)
            startActivity(intent)
        }
        hard_play.setOnClickListener {
            Timber.i("hard_play.onclick called")
            val intent = Intent(requireContext(), JumbledWordsActivity::class.java)
            intent.putExtra("gameMode", HARD_MODE)
            startActivity(intent)
        }
    }


    private fun getAndDisplayHighScore() {
        Timber.i("getAndDisplayHighScore called")
        lifecycleScope.launchWhenCreated {
            val easy = viewModel.getGameDataByName(GameConstants.JUMBLED_NUMBER_GAME_NAME_EASY)
            val medium = viewModel.getGameDataByName(GameConstants.JUMBLED_NUMBER_GAME_NAME_MED)
            val hard = viewModel.getGameDataByName(GameConstants.JUMBLED_NUMBER_GAME_NAME_HARD)
            if (easy == null && medium == null && hard == null) {
                jum_best_score_tv.text = ""
                jum_best_time_tv.text = ""
            } else {
                var totalPlayed: Int = 0
                var totalTime: Long = 0
                if (easy != null) {
                    totalPlayed += easy.totalGamesPlayed
                    totalTime += easy.totalTime
                }
                if (medium != null) {
                    totalPlayed += medium.totalGamesPlayed
                    totalTime += medium.totalTime
                }
                if (hard != null) {
                    totalPlayed += hard.totalGamesPlayed
                    totalTime += hard.totalTime
                }
                jum_best_score_tv.text =
                        getString(R.string.total_games_text, totalPlayed.toString())
                jum_best_time_tv.text = getString(
                        R.string.total_time_played,
                        (totalTime / 60f).toDouble().roundTo2Digit().toString()
                )
            }
        }
    }
}