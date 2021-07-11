package com.ck.dev.tiptap.ui

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.GridLayoutManager
import com.ck.dev.tiptap.R
import com.ck.dev.tiptap.adapters.GameLevelsAdapter
import com.ck.dev.tiptap.helpers.AppConstants
import com.ck.dev.tiptap.helpers.readJsonFromAsset
import com.ck.dev.tiptap.models.FindTheNumberGameRule
import com.ck.dev.tiptap.models.GameLevel
import com.ck.dev.tiptap.ui.games.BaseFragment
import kotlinx.android.synthetic.main.fragment_game_levels.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import timber.log.Timber

class GameLevelsFragment : BaseFragment(R.layout.fragment_game_levels) {

    private lateinit var navController: NavController

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment)
        setLevelsRecyclerView()
    }

    private fun setLevelsRecyclerView() {
        Timber.i("setLevelsRecyclerView called")
        val rulesJson= (requireActivity() as AppCompatActivity).readJsonFromAsset(AppConstants.FIND_THE_NUM_GAME_RULE_FILE_NAME)
        rulesJson?.let{
            val getLevels = getFindTheNumGameRules(rulesJson)
            val gridLayoutManager =
                GridLayoutManager(requireContext(), 5/*, LinearLayoutManager.HORIZONTAL, false*/)
            levels_rv.layoutManager = gridLayoutManager
            /// StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);

            levels_rv.adapter = GameLevelsAdapter(getLevels, navController)
        }

    }

    private fun getFindTheNumGameRules(countriesJson: String): ArrayList<GameLevel> {
        Timber.i("getFindTheNumGameRules called")
        val list = ArrayList<GameLevel>()
        try {
            val countriesArray = JSONArray(countriesJson)
            for (i in 0 until countriesArray.length()) {
                val countriesJsonObj: JSONObject = countriesArray.getJSONObject(i)
                val level = countriesJsonObj.getString("level")
                val gridSize = countriesJsonObj.getInt("gridSize")
                val visibleNumSize = countriesJsonObj.getInt("visibleNumSize")
                val time = countriesJsonObj.getLong("time")
                val coinsReqd = countriesJsonObj.getInt("coinsReqd")
                list.add(GameLevel(level,true,FindTheNumberGameRule(level,gridSize,visibleNumSize,time,coinsReqd)))
            }
            /*list.sortByDescending {
                it.levelNum.toInt()
            }*/
        } catch (ex: JSONException) {
            Timber.e(ex, "exception while extracting game rules from json string")
        }
        return list
    }
}