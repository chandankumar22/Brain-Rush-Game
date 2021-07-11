package com.ck.dev.tiptap.ui.games.findthenumber

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.GridLayoutManager
import com.ck.dev.tiptap.R
import com.ck.dev.tiptap.adapters.FindTheNumberLevelsAdapter
import com.ck.dev.tiptap.data.entity.Games
import com.ck.dev.tiptap.helpers.AppConstants
import com.ck.dev.tiptap.helpers.GameConstants.DESTINATION_FIND_THE_NUMBER
import com.ck.dev.tiptap.helpers.GameConstants.FIND_THE_NUMBER_GAME_NAME
import com.ck.dev.tiptap.helpers.readJsonFromAsset
import com.ck.dev.tiptap.models.FindTheNumberGameRule
import com.ck.dev.tiptap.ui.games.BaseFragment
import com.ck.dev.tiptap.viewmodelfactories.FindTheNumberVmFactory
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_game_levels.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber

class GameLevelsFragment : BaseFragment(R.layout.fragment_game_levels) {

    private lateinit var navController: NavController

    private var gameData: Games = Games(FIND_THE_NUMBER_GAME_NAME)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Timber.i("onViewCreated called")
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment)
        setLevelsRecyclerView()
        requireActivity().findViewById<ConstraintLayout>(R.id.header).visibility = View.VISIBLE
    }

    private fun setLevelsRecyclerView() {
        Timber.i("setLevelsRecyclerView called")
        lifecycleScope.launch {
            viewModel.insertGameIfNotAdded(FIND_THE_NUMBER_GAME_NAME)
            val completedGameLevels = viewModel.getCompletedLevels(FIND_THE_NUMBER_GAME_NAME)
            if (!completedGameLevels.isNullOrEmpty()) {
                val sortedLevels = completedGameLevels.sortedByDescending {
                    it.currentLevel.toInt()
                }
                gameData = Games(
                        sortedLevels[0].gameName,
                        sortedLevels[0].currentLevel
                )
            }
            withContext(Dispatchers.Main) {
                val rulesJson =
                        (requireActivity() as AppCompatActivity).readJsonFromAsset(AppConstants.FIND_THE_NUM_GAME_RULE_FILE_NAME)
                val getLevels = viewModel.getFindTheNumGameRules(gameData, Gson().fromJson(rulesJson, Array<FindTheNumberGameRule>::class.java))
                game_levels_tv.text = "${gameData?.currentLevel}/${getLevels.size}"
                val gridLayoutManager = GridLayoutManager(requireContext(), 5)
                levels_rv.layoutManager = gridLayoutManager
                levels_rv.adapter = FindTheNumberLevelsAdapter(getLevels, navController, DESTINATION_FIND_THE_NUMBER)
            }
        }
    }


}