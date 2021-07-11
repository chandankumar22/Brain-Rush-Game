package com.ck.dev.tiptap.ui.games.findthenumber

import android.os.Bundle
import android.view.View
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.ck.dev.tiptap.R
import com.ck.dev.tiptap.extensions.fetchColor
import com.ck.dev.tiptap.ui.games.BaseFragment
import kotlinx.android.synthetic.main.activity_find_the_numbers.*
import kotlinx.android.synthetic.main.fragment_find_the_number_menu_screen.*
import timber.log.Timber

class FindTheNumberMenuScreen : BaseFragment(R.layout.fragment_find_the_number_menu_screen) {

    private lateinit var navController: NavController

    companion object {
        @JvmStatic
        fun newInstance() = FindTheNumberMenuScreen()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Timber.i("onViewCreated called")
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment)
        time_bound_play.setOnClickListener {
            (requireActivity() as FindTheNumbersActivity).apply {
                nav_host_fragment.visibility = View.VISIBLE
                //  main_screen_root.setBackgroundColor(ContextCompat.getColor(requireContext(),R.color.primaryLightColor))
                navController.navigate(R.id.action_findNumbersMainScreenFragment_to_gameLevelsFragment)
            }
        }
    }

    override fun onResume() {
        Timber.i("onResume called")
        super.onResume()
        (requireActivity() as FindTheNumbersActivity).find_the_num_header.setBackgroundColor(
            requireContext().fetchColor(R.color.primaryLightColor)
        )
    }
}