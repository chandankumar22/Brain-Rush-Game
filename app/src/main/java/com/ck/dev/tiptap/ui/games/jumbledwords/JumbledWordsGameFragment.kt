package com.ck.dev.tiptap.ui.games.jumbledwords

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.addCallback
import com.ck.dev.tiptap.R
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
        go_to_jumbled_words.setOnClickListener {
            val intent = Intent(requireContext(), JumbledWordsActivity::class.java)
            startActivity(intent)
        }
    }
}