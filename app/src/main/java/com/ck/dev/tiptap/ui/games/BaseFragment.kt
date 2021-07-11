package com.ck.dev.tiptap.ui.games

import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.ck.dev.tiptap.viewmodelfactories.FindTheNumberVmFactory

open class BaseFragment(@LayoutRes layoutRes: Int):Fragment(layoutRes) {
    val viewModel: GamesViewModel by activityViewModels {
        FindTheNumberVmFactory(requireContext())
    }
}