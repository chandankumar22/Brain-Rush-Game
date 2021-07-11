package com.ck.dev.tiptap.ui.games.rememberthecard

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.addCallback
import androidx.lifecycle.lifecycleScope
import com.ck.dev.tiptap.R
import com.ck.dev.tiptap.helpers.GameConstants
import com.ck.dev.tiptap.helpers.GameConstants.REMEMBER_THE_CARD_NAME_GAME_ENDLESS
import com.ck.dev.tiptap.helpers.GameConstants.REMEMBER_THE_CARD_NAME_GAME_TIME_BOUND
import com.ck.dev.tiptap.helpers.roundTo2Digit
import com.ck.dev.tiptap.ui.GameApp
import com.ck.dev.tiptap.ui.games.BaseFragment
import kotlinx.android.synthetic.main.fragment_remember_the_card_game.*
import timber.log.Timber

class RememberTheCardGameFragment : BaseFragment(R.layout.fragment_remember_the_card_game) {
    companion object {
        @JvmStatic
        fun newInstance() = RememberTheCardGameFragment()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Timber.i("onViewCreated called")
        super.onViewCreated(view, savedInstanceState)
        setListeners()
        getAndDisplayHighScore()

    }

    private fun getAndDisplayHighScore() {
        Timber.i("getAndDisplayHighScore called")
        lifecycleScope.launchWhenCreated {
            val timeBound = viewModel.getGameDataByName(REMEMBER_THE_CARD_NAME_GAME_TIME_BOUND)
            val endless = viewModel.getGameDataByName(REMEMBER_THE_CARD_NAME_GAME_ENDLESS)
            if (timeBound == null && endless == null) {
                rem_the_card_best_score_tv.text = ""
                rem_the_card_best_time_tv.text = ""
            } else {
                var totalPlayed: Int = 0
                var totalTime: Long = 0
                if (timeBound != null) {
                    totalPlayed +=timeBound.totalGamesPlayed
                    totalTime +=timeBound.totalTime
                }
                if (endless != null) {
                    totalPlayed += endless.totalGamesPlayed
                    totalTime +=endless.totalTime
                }
                rem_the_card_best_score_tv.text =
                        getString(R.string.total_games_text, totalPlayed.toString())
                rem_the_card_best_time_tv.text = getString(
                        R.string.total_time_played,
                        (totalTime / 60f).toDouble().roundTo2Digit().toString()
                )
            }
        }
    }

    private fun setListeners() {
        Timber.i("setListeners called")
        rem_the_card_time_bound_play.setOnClickListener {
            Timber.i("rem_the_card_time_bound_play.onclick called")
            val intent = Intent(requireContext(), RememberTheCardActivity::class.java)
            intent.putExtra("gameMode",REMEMBER_THE_CARD_NAME_GAME_TIME_BOUND)
            startActivity(intent)
        }
        rem_the_card_infinite_game_play.setOnClickListener {
            Timber.i("rem_the_card_infinite_game_play.onclick called")
            val intent = Intent(requireContext(), RememberTheCardActivity::class.java)
            intent.putExtra("gameMode",REMEMBER_THE_CARD_NAME_GAME_ENDLESS)
            startActivity(intent)
        }

        GameApp.hasGame1Played.observe(viewLifecycleOwner, {
            getAndDisplayHighScore()
        })
    }
}