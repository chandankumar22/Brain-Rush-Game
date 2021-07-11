package com.ck.dev.tiptap.ui.games.findthenumber

import android.content.Intent
import android.os.Bundle
import android.view.View
import com.ck.dev.tiptap.R
import com.ck.dev.tiptap.ui.games.BaseFragment
import kotlinx.android.synthetic.main.fragment_find_numbers_game_main_screen.*
import timber.log.Timber

class FindNumbersMainScreenFragment :
    BaseFragment(R.layout.fragment_find_numbers_game_main_screen) {


    companion object {
        @JvmStatic
        fun newInstance() =
            FindNumbersMainScreenFragment()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Timber.i("onViewCreated called")
        super.onViewCreated(view, savedInstanceState)
        go_to_game_menu.setOnClickListener {
            val intent = Intent(requireContext(), FindTheNumbersActivity::class.java)
            startActivity(intent)
        }

    }
}