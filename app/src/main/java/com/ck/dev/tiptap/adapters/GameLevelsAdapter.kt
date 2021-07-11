package com.ck.dev.tiptap.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import com.ck.dev.tiptap.R
import com.ck.dev.tiptap.models.FindTheNumGameLevel
import com.ck.dev.tiptap.ui.GameLevelsFragmentDirections
import kotlinx.android.synthetic.main.list_item_levels.view.*

class GameLevelsAdapter(
    private val list: List<FindTheNumGameLevel>,
    private val navController: NavController
) :
    RecyclerView.Adapter<GameLevelsAdapter.ViewHolder>() {

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
                    list[position].rule.apply {
                        val action =
                            GameLevelsFragmentDirections.actionGameLevelsFragmentToGameScreenFragment(
                                gridSize = gridSize,
                                visibleNums = visibleNumSize,
                                time = time, level = level
                            )
                        /*val action = GameLevelsFragmentDirections.actionGameLevelsFragmentToGameScreenFragment(name)
                        v.findNavController().navigate(action)*/
                        navController.navigate(action)
                    }
                }
            }
            if (list[position].isGameUnlocked) {
                lock_iv.visibility = View.GONE
                coins_reqd_container.visibility = View.VISIBLE
                high_score_tv.text = list[position].highScore.toString()
                deductCoins(list[position].rule.coinsReqd)

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