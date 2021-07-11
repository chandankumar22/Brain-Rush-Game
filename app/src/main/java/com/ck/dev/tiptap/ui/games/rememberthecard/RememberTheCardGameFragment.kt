package com.ck.dev.tiptap.ui.games.rememberthecard

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.addCallback
import com.ck.dev.tiptap.R
import com.ck.dev.tiptap.ui.games.BaseFragment
import com.ck.dev.tiptap.ui.games.findthenumber.FindTheNumbersActivity
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
        rem_the_card_go_to_game_menu.setOnClickListener {
            val intent = Intent(requireContext(), RememberTheCardActivity::class.java)
            startActivity(intent)
        }
    }
}