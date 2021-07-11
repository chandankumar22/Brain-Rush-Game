package com.ck.dev.tiptap.ui.lastscreen

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.lifecycle.lifecycleScope
import com.ck.dev.tiptap.R
import com.ck.dev.tiptap.helpers.AppConstants.ABOUT_SCREEN_TAG
import com.ck.dev.tiptap.helpers.AppConstants.GET_COINS_SCREEN_TAG
import com.ck.dev.tiptap.helpers.AppConstants.SETTINGS_SCREEN_TAG
import com.ck.dev.tiptap.helpers.AppConstants.STATISTICS_SCREEN_TAG
import com.ck.dev.tiptap.sounds.GameSound.playBtnClickSound
import com.ck.dev.tiptap.ui.games.BaseFragment
import kotlinx.android.synthetic.main.fragment_the_quiz_game.*
import kotlinx.coroutines.launch

class LastScreenFragment : BaseFragment(R.layout.fragment_the_quiz_game) {
    companion object {
        @JvmStatic
        fun newInstance() = LastScreenFragment()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setListeners()
    }

    private fun setListeners() {
        statistics.setOnClickListener {
            lifecycleScope.launch {
                requireContext().playBtnClickSound()
            }
            startLastPageActivity(STATISTICS_SCREEN_TAG) }
        watch_ad.setOnClickListener {
            lifecycleScope.launch {
                requireContext().playBtnClickSound()
            }
            //implement share app logic
        }
        settings.setOnClickListener {
            lifecycleScope.launch {
                requireContext().playBtnClickSound()
            }
            startLastPageActivity(SETTINGS_SCREEN_TAG) }
        about.setOnClickListener {
            lifecycleScope.launch {
                requireContext().playBtnClickSound()
            }
            startLastPageActivity(ABOUT_SCREEN_TAG) }
    }

    private fun startLastPageActivity(screenName: String) {
        Intent(requireContext(), LastScreenActivity::class.java).also {
            it.putExtra("screenName", screenName)
            requireContext().startActivity(it)
        }
    }
}