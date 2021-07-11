package com.ck.dev.tiptap.ui.games.rememberthecard

import android.os.Bundle
import android.view.View
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import com.ck.dev.tiptap.R
import com.ck.dev.tiptap.adapters.RememberTheCardLevelsAdapter
import com.ck.dev.tiptap.data.entity.Games
import com.ck.dev.tiptap.extensions.changeStatusBarColor
import com.ck.dev.tiptap.extensions.setHeaderBgColor
import com.ck.dev.tiptap.helpers.AppConstants.REMEMBER_CARDS_GAME_RULE_FILE_NAME
import com.ck.dev.tiptap.helpers.ClickSound
import com.ck.dev.tiptap.helpers.GameConstants
import com.ck.dev.tiptap.helpers.readJsonFromAsset
import com.ck.dev.tiptap.models.RememberTheCardGameRule
import com.ck.dev.tiptap.sounds.GameSound.playBtnClickSound
import com.ck.dev.tiptap.ui.games.BaseFragment
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_game_levels.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import timber.log.Timber

class RememberTheCardGameLevelsFragment : BaseFragment(R.layout.fragment_game_levels), ClickSound {

    private lateinit var navController: NavController

    private val gameArgs: RememberTheCardGameLevelsFragmentArgs by navArgs()
    private lateinit var gameData: Games
    private var gameName = GameConstants.REMEMBER_THE_CARD_NAME_GAME_TIME_BOUND

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Timber.i("onViewCreated called")
        super.onViewCreated(view, savedInstanceState)
        gameName = gameArgs.gameName
        gameData = Games(gameName)
        navController =
                Navigation.findNavController(requireActivity(), R.id.rem_the_card_nav_host_fragment)
        setLevelsRecyclerView()
        (requireActivity() as AppCompatActivity).setHeaderBgColor(R.color.primaryLightColor)
        (requireActivity() as AppCompatActivity).changeStatusBarColor(R.color.primaryLightColor)
        requireActivity().findViewById<ConstraintLayout>(R.id.header).visibility = View.VISIBLE
        requireActivity().onBackPressedDispatcher.addCallback(this) {
            requireActivity().finish()
        }
    }

    private fun setLevelsRecyclerView() {
        Timber.i("setLevelsRecyclerView called")
        lifecycleScope.launch {
            viewModel.insertGameIfNotAdded(gameName)
            val completedGameLevels = viewModel.getCompletedLevels(gameName)
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
                val rulesJson = (requireActivity() as AppCompatActivity).readJsonFromAsset(REMEMBER_CARDS_GAME_RULE_FILE_NAME)

                val json = JSONObject(rulesJson)
                val jumbledRules = if (gameName == GameConstants.REMEMBER_THE_CARD_NAME_GAME_TIME_BOUND) {
                    Gson().fromJson(json.getJSONArray("time-bound").toString(), Array<RememberTheCardGameRule>::class.java)
                } else {
                    Gson().fromJson(json.getJSONArray("endless").toString(), Array<RememberTheCardGameRule>::class.java)
                }
                val getLevels = viewModel.getRememberTheCardGameRules(gameData, jumbledRules)
                game_levels_tv.text = "${gameData?.currentLevel}/${getLevels.size}"
                val gridLayoutManager = GridLayoutManager(requireContext(), 5)
                levels_rv.layoutManager = gridLayoutManager
                levels_rv.adapter = RememberTheCardLevelsAdapter(getLevels, navController, gameName,this@RememberTheCardGameLevelsFragment)
            }
        }
    }

    override fun playSound() {
        lifecycleScope.launch {
            requireContext().playBtnClickSound()
        }
    }
}