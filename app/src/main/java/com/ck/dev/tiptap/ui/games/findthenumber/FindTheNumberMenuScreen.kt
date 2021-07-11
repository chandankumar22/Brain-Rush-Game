package com.ck.dev.tiptap.ui.games.findthenumber

import android.os.Bundle
import android.view.View
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.ck.dev.tiptap.R
import com.ck.dev.tiptap.extensions.fetchColor
import com.ck.dev.tiptap.extensions.setHeaderBgColor
import com.ck.dev.tiptap.helpers.GameConstants
import com.ck.dev.tiptap.helpers.GameConstants.FIND_THE_NUMBER_GAME_NAME
import com.ck.dev.tiptap.helpers.roundTo2Digit
import com.ck.dev.tiptap.ui.games.BaseFragment
import com.ck.dev.tiptap.viewmodelfactories.FindTheNumberVmFactory
import kotlinx.android.synthetic.main.activity_find_the_numbers.*
import kotlinx.android.synthetic.main.fragment_find_the_number_menu_screen.*
import kotlinx.android.synthetic.main.fragment_jumbled_words_menu_screen.*
import timber.log.Timber

class FindTheNumberMenuScreen : BaseFragment(R.layout.fragment_find_the_number_menu_screen) {

    private lateinit var navController: NavController

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Timber.i("onViewCreated called")
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment)
        setListeners()
        requireActivity().onBackPressedDispatcher.addCallback(this) {
            requireActivity().finish()
        }
        (requireActivity() as AppCompatActivity).setHeaderBgColor(R.color.primaryLightColor)
    }

    private fun setListeners() {
        Timber.i("setListeners called")
        time_bound_play.setOnClickListener {
            Timber.i("time_bound_play.onclick called")
            (requireActivity() as FindTheNumbersActivity).apply {
                nav_host_fragment.visibility = View.VISIBLE
                //  main_screen_root.setBackgroundColor(ContextCompat.getColor(requireContext(),R.color.primaryLightColor))
                navController.navigate(R.id.action_findNumbersMainScreenFragment_to_gameLevelsFragment)
            }
        }
        infinite_game_play.setOnClickListener {
            Timber.i("infinite_game_play.onclick called")
            (requireActivity() as FindTheNumbersActivity).apply {
                nav_host_fragment.visibility = View.VISIBLE
                //  main_screen_root.setBackgroundColor(ContextCompat.getColor(requireContext(),R.color.primaryLightColor))
                navController.navigate(R.id.action_findNumbersMainScreenFragment_to_infinitePlayGameFragment)
            }
        }
    }

    private fun getAndDisplayHighScore() {
        Timber.i("getAndDisplayHighScore called")
        lifecycleScope.launchWhenCreated {
            val data = viewModel.getGameDataByName(FIND_THE_NUMBER_GAME_NAME)
            if(data == null){
                best_score_tv.text = ""
                best_time_tv.text = ""
            }else{
                best_score_tv.text = getString(R.string.total_games_text,data.totalGamesPlayed.toString())
                best_time_tv.text = getString(R.string.total_time_played,(data.totalTime/60f).toDouble().roundTo2Digit().toString())
            }
        }
    }
}