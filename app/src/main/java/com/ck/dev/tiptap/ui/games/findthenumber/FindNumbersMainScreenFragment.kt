package com.ck.dev.tiptap.ui.games.findthenumber

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.addCallback
import androidx.lifecycle.lifecycleScope
import com.ck.dev.tiptap.R
import com.ck.dev.tiptap.helpers.GameConstants
import com.ck.dev.tiptap.helpers.GameConstants.FIND_THE_NUMBER_GAME_NAME
import com.ck.dev.tiptap.helpers.GameConstants.FIND_THE_NUMBER_INFINITE_GAME_NAME
import com.ck.dev.tiptap.helpers.roundTo2Digit
import com.ck.dev.tiptap.ui.games.BaseFragment
import kotlinx.android.synthetic.main.fragment_find_numbers_game_main_screen.infinite_game_play
import kotlinx.android.synthetic.main.fragment_find_numbers_game_main_screen.time_bound_play
import kotlinx.android.synthetic.main.fragment_find_the_number_menu_screen.*
import kotlinx.android.synthetic.main.fragment_remeber_the_card_menu.*
import timber.log.Timber

class FindNumbersMainScreenFragment : BaseFragment(R.layout.fragment_find_numbers_game_main_screen) {

    companion object {
        @JvmStatic
        fun newInstance() =
                FindNumbersMainScreenFragment()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Timber.i("onViewCreated called")
        super.onViewCreated(view, savedInstanceState)
        requireActivity().onBackPressedDispatcher.addCallback(this) {
            requireActivity().finish()
        }
        setListeners()
        getAndDisplayHighScore()
    }

    private fun setListeners() {
        Timber.i("setListeners called")
        time_bound_play.setOnClickListener {
            Timber.i("time_bound_play.onclick called")
            val intent = Intent(requireContext(), FindTheNumbersActivity::class.java)
            intent.putExtra("gameMode", FIND_THE_NUMBER_GAME_NAME)
            startActivity(intent)
        }
        infinite_game_play.setOnClickListener {
            Timber.i("infinite_game_play.onclick called")
            val intent = Intent(requireContext(), FindTheNumbersActivity::class.java)
            intent.putExtra("gameMode", FIND_THE_NUMBER_INFINITE_GAME_NAME)
            startActivity(intent)
        }
    }

    private fun getAndDisplayHighScore() {
        Timber.i("getAndDisplayHighScore called")
        lifecycleScope.launchWhenCreated {
            val timeBound = viewModel.getGameDataByName(FIND_THE_NUMBER_GAME_NAME)
            val endless = viewModel.getGameDataByName(FIND_THE_NUMBER_INFINITE_GAME_NAME)
            if (timeBound == null && endless == null) {
                best_score_tv.text = ""
                best_time_tv.text = ""
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
                best_score_tv.text =
                        getString(R.string.total_games_text, totalPlayed.toString())
                best_time_tv.text = getString(
                        R.string.total_time_played,
                        (totalTime / 60f).toDouble().roundTo2Digit().toString()
                )
            }
        }
    }
}