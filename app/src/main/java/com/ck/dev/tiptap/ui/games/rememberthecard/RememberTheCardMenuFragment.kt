package com.ck.dev.tiptap.ui.games.rememberthecard

import android.os.Bundle
import android.view.View
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.ck.dev.tiptap.R
import com.ck.dev.tiptap.extensions.setHeaderBgColor
import com.ck.dev.tiptap.helpers.GameConstants.REMEMBER_THE_CARD_NAME_GAME_ENDLESS
import com.ck.dev.tiptap.helpers.GameConstants.REMEMBER_THE_CARD_NAME_GAME_TIME_BOUND
import com.ck.dev.tiptap.ui.games.BaseFragment
import kotlinx.android.synthetic.main.fragment_remeber_the_card_menu.*
import timber.log.Timber

class RememberTheCardMenuFragment : BaseFragment(R.layout.fragment_remeber_the_card_menu) {

    private lateinit var navController: NavController

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController =
            Navigation.findNavController(requireActivity(), R.id.rem_the_card_nav_host_fragment)
        requireActivity().onBackPressedDispatcher.addCallback(this) {
            requireActivity().finish()
        }
        (requireActivity() as AppCompatActivity).setHeaderBgColor(R.color.primaryLightColor)
        setListeners()
    }

    private fun setListeners() {
        Timber.i("setListeners called")
        rem_the_card_time_bound_play.setOnClickListener {
            Timber.i("rem_the_card_time_bound_play.onclick called")
            (requireActivity() as RememberTheCardActivity).apply {
                val action =
                    RememberTheCardMenuFragmentDirections.actionRememberTheCardMenuFragmentToRememberTheCardGameLevelsFragment(
                        REMEMBER_THE_CARD_NAME_GAME_TIME_BOUND
                    )
                navController.navigate(action)
            }
        }
        rem_the_card_infinite_game_play.setOnClickListener {
            Timber.i("rem_the_card_infinite_game_play.onclick called")
            val action =
                RememberTheCardMenuFragmentDirections.actionRememberTheCardMenuFragmentToRememberTheCardGameLevelsFragment(
                    REMEMBER_THE_CARD_NAME_GAME_ENDLESS
                )
            navController.navigate(action)
        }
    }

}