package com.ck.dev.tiptap.ui.games.jumbledwords

import android.os.Bundle
import android.view.View
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.ck.dev.tiptap.R
import com.ck.dev.tiptap.extensions.setHeaderBgColor
import com.ck.dev.tiptap.helpers.GameConstants.EASY_MODE
import com.ck.dev.tiptap.helpers.GameConstants.HARD_MODE
import com.ck.dev.tiptap.helpers.GameConstants.JUMBLED_NUMBER_GAME_NAME_EASY
import com.ck.dev.tiptap.helpers.GameConstants.JUMBLED_NUMBER_GAME_NAME_HARD
import com.ck.dev.tiptap.helpers.GameConstants.JUMBLED_NUMBER_GAME_NAME_MED
import com.ck.dev.tiptap.helpers.GameConstants.MEDIUM_MODE
import com.ck.dev.tiptap.helpers.roundTo2Digit
import com.ck.dev.tiptap.ui.games.BaseFragment
import com.ck.dev.tiptap.ui.games.findthenumber.FindTheNumberViewModel
import com.ck.dev.tiptap.viewmodelfactories.FindTheNumberVmFactory
import kotlinx.android.synthetic.main.fragment_jumbled_words_menu_screen.*

class JumbledWordsMenuScreenFragment : BaseFragment(R.layout.fragment_jumbled_words_menu_screen) {

    private lateinit var navController: NavController
    private var mode = EASY_MODE

    private val viewModel: FindTheNumberViewModel by activityViewModels {
        FindTheNumberVmFactory(requireContext())
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController =
            Navigation.findNavController(requireActivity(), R.id.jumbled_words_nav_host_fragment)
        (requireActivity() as AppCompatActivity).setHeaderBgColor(R.color.primaryLightColor)
        easy_play.setOnClickListener {
            (requireActivity() as JumbledWordsActivity).apply {
                mode = EASY_MODE
                val action =
                    JumbledWordsMenuScreenFragmentDirections.actionJumbledWordsMenuScreenFragmentToJumbledWordsLevelsFragment(
                        gameMode = mode
                    )
                // jumbled_words_nav_host_fragment.visibility = View.VISIBLE
                navController.navigate(action)
            }
        }
        medium_play.setOnClickListener {
            mode = MEDIUM_MODE
            val action =
                JumbledWordsMenuScreenFragmentDirections.actionJumbledWordsMenuScreenFragmentToJumbledWordsLevelsFragment(
                    gameMode = mode
                )
            // jumbled_words_nav_host_fragment.visibility = View.VISIBLE
            navController.navigate(action)
        }
        hard_play.setOnClickListener {
            mode = HARD_MODE
            val action =
                JumbledWordsMenuScreenFragmentDirections.actionJumbledWordsMenuScreenFragmentToJumbledWordsLevelsFragment(
                    gameMode = mode
                )
            //jumbled_words_nav_host_fragment.visibility = View.VISIBLE
            navController.navigate(action)
        }


        requireActivity().onBackPressedDispatcher.addCallback(this) {
            requireActivity().finish()
        }
        lifecycleScope.launchWhenCreated {
            val easy = viewModel.getGameDataByName(JUMBLED_NUMBER_GAME_NAME_EASY)
            val medium = viewModel.getGameDataByName(JUMBLED_NUMBER_GAME_NAME_MED)
            val hard = viewModel.getGameDataByName(JUMBLED_NUMBER_GAME_NAME_HARD)
            if (easy == null && medium == null && hard == null) {
                jum_best_score_tv.text = ""
                jum_best_time_tv.text = ""
            } else {
                var totalPlayed: Int = 0
                var totalTime: Long = 0
                if (easy != null) {
                    totalPlayed +=easy.totalGamesPlayed
                    totalTime +=easy.totalTime
                }
                if (medium != null) {
                    totalPlayed += medium.totalGamesPlayed
                    totalTime +=medium.totalTime
                }
                if (hard != null) {
                    totalPlayed +=hard.totalGamesPlayed
                    totalTime +=hard.totalTime
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