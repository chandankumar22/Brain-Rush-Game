package com.ck.dev.tiptap.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import com.ck.dev.tiptap.R
import com.ck.dev.tiptap.helpers.AppConstants.NOT_PLAYED_TAG
import com.ck.dev.tiptap.helpers.ClickSound
import com.ck.dev.tiptap.helpers.GameConstants.DESTINATION_FIND_THE_NUMBER
import com.ck.dev.tiptap.helpers.GameConstants.DESTINATION_JUMBLED_WORDS
import com.ck.dev.tiptap.models.FindTheNumGameLevel
import com.ck.dev.tiptap.sounds.GameSound.playBtnClickSound
import com.ck.dev.tiptap.ui.games.findthenumber.GameLevelsFragmentDirections
import kotlinx.android.synthetic.main.list_item_levels.view.*
import kotlinx.coroutines.launch

class FindTheNumberLevelsAdapter(
    private val list: List<FindTheNumGameLevel>,
    private val navController: NavController,
    private val destination: String,
    private val soundCallback:ClickSound
) :
    RecyclerView.Adapter<FindTheNumberLevelsAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.list_item_levels, parent, false)
        )

    override fun getItemCount() = list.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.itemView.apply {
            level_num_text.text = list[position].levelNum
            setOnClickListener {
                if (list[position].isGameUnlocked) {
                    soundCallback.playSound()
                    list[position].rule.apply {
                        if (destination == DESTINATION_JUMBLED_WORDS) {
                            navController.navigate(R.id.action_jumbledWordsLevelsFragment_to_playJumbledWordsGameFragment)
                        } else if (destination == DESTINATION_FIND_THE_NUMBER) {
                            val action =
                                GameLevelsFragmentDirections.actionGameLevelsFragmentToGameScreenFragment(
                                    gridSize = gridSize,
                                    visibleNums = visibleNumSize,
                                    time = time,
                                    level = level,
                                    isEndless = false,
                                    coins = list[position].rule.coins
                                )
                            navController.navigate(action)
                        }

                    }
                }
            }
            if (list[position].isGameUnlocked) {
                lock_iv.visibility = View.GONE
                coins_reqd_container.visibility = View.VISIBLE
                high_score_tv.text = String.format("%s", if (list[position].highScore == NOT_PLAYED_TAG) "-" else {
                    "${list[position].highScore} points"
                })
                deductCoins(list[position].rule.coins)

            } else {
                lock_iv.visibility = View.VISIBLE
                coins_reqd_container.visibility = View.GONE
            }
        }
    }

    private fun deductCoins(coinsReqd: Int) {
        //TO BE IMPLEMENTED LATER
    }
}