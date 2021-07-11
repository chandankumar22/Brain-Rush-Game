package com.ck.dev.tiptap.ui.games.jumbledwords

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.addCallback
import androidx.lifecycle.lifecycleScope
import com.ck.dev.tiptap.R
import com.ck.dev.tiptap.helpers.GameConstants
import com.ck.dev.tiptap.helpers.GameConstants.ENDLESS
import com.ck.dev.tiptap.helpers.GameConstants.TIME_BOUND
import com.ck.dev.tiptap.helpers.roundTo2Digit
import com.ck.dev.tiptap.ui.GameApp
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
        jum_endless_play.setOnClickListener {
            Timber.i("easy_play.onclick called")
            val intent = Intent(requireContext(), JumbledWordsActivity::class.java)
            intent.putExtra("gameMode", ENDLESS)
            startActivity(intent)
        }
        jum_time_bound_play.setOnClickListener {
            Timber.i("medium_play.onclick called")
            val intent = Intent(requireContext(), JumbledWordsActivity::class.java)
            intent.putExtra("gameMode", TIME_BOUND)
            startActivity(intent)
        }
        GameApp.hasGame3Played.observe(viewLifecycleOwner, {
            getAndDisplayHighScore()
        })
    }


    private fun getAndDisplayHighScore() {
        Timber.i("getAndDisplayHighScore called")
        lifecycleScope.launchWhenCreated {
            val easy = viewModel.getGameDataByName(GameConstants.JUMBLED_NUMBER_GAME_NAME_ENDLESS)
            val medium = viewModel.getGameDataByName(GameConstants.JUMBLED_NUMBER_GAME_NAME_TIME_BOUND)
            if (easy == null && medium == null) {
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