package com.ck.dev.tiptap.adapters

import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.ck.dev.tiptap.R
import com.ck.dev.tiptap.ui.games.findthenumber.FindNumbersMainScreenFragment
import com.ck.dev.tiptap.ui.games.RememberTheCardGameFragment

class GameChangerAdapter(mContext: FragmentActivity) : FragmentStateAdapter(mContext) {

    companion object {
        @StringRes
        private val TAB_TITLES = intArrayOf(
            R.string.game_1,
            R.string.game_2
        )
    }

    override fun getItemCount(): Int {
        return TAB_TITLES.size
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> FindNumbersMainScreenFragment.newInstance()
            1 -> RememberTheCardGameFragment.newInstance()
            else -> null!!
        }
    }
}
