package com.ck.dev.tiptap.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import com.ck.dev.tiptap.R
import com.ck.dev.tiptap.models.JumbledWordGameLevel
import com.ck.dev.tiptap.ui.games.jumbledwords.JumbledWordsLevelsFragmentDirections
import kotlinx.android.synthetic.main.list_item_levels.view.*

class JumbledWordsLevelsAdapter(
    private val list: ArrayList<JumbledWordGameLevel>,
    private val navController: NavController,
    private val gameName: String
) :
    RecyclerView.Adapter<JumbledWordsLevelsAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.list_item_levels, parent, false)
        )

    override fun getItemCount() = list.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.itemView.apply {
            level_num_text.text = list[position].level
            setOnClickListener {
                if (list[position].isGameUnlocked) {
                    val action =
                        JumbledWordsLevelsFragmentDirections.actionJumbledWordsLevelsFragmentToPlayJumbledWordsGameFragment(
                            word = list[position].rule.word,
                            unjumbledWords = list[position].rule.unJumbledWords,
                            level = list[position].level,
                            timeLimits = list[position].rule.timeLimit.toIntArray(),
                            gameMode = gameName
                        )
                    navController.navigate(action)
                }
            }
            if (list[position].isGameUnlocked) {
                lock_iv.visibility = View.GONE
                coins_reqd_container.visibility = View.VISIBLE
                high_score_tv.visibility = View.GONE
                level_stars.visibility = View.VISIBLE
                level_stars.rating = list[position].stars.toFloat()
                //deductCoins(list[position].rule.coinsReqd)

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