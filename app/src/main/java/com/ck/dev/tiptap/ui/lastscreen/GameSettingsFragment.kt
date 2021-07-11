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


class GameSettingsFragment : BaseFragment(R.layout.fragment_game_settings) {

    companion object {
        @JvmStatic
        fun newInstance() = GameSettingsFragment()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Timber.i("onViewCreated called")
        super.onViewCreated(view, savedInstanceState)
        (requireActivity() as AppCompatActivity).changeStatusBarColor(R.color.primaryLightColor)
    }

}