package com.ck.dev.tiptap.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import com.ck.dev.tiptap.R
import com.ck.dev.tiptap.models.GameLevel
import com.ck.dev.tiptap.ui.GameLevelsFragmentDirections
import kotlinx.android.synthetic.main.list_item_levels.view.*

class GameLevelsAdapter(
    private val list: List<GameLevel>,
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
            /*  val layerDrawable = context.fetchDrawable(R.drawable.layer_list_number) as LayerDrawable
              val btnBg = layerDrawable.findDrawableByLayerId(R.id.btn_front) as GradientDrawable
              val btnShadow = layerDrawable.findDrawableByLayerId(R.id.btn_shadow) as GradientDrawable
              btnBg.setColor(context.fetchColor(R.color.primaryDarkColor))*/
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
                val textParam = RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT
                )
                textParam.addRule(RelativeLayout.CENTER_IN_PARENT)
                level_num_text.layoutParams = textParam
                level_num_text.textSize =
                    context.resources.getDimension(R.dimen.app_text_size_xsmall)

            } else {
                lock_iv.visibility = View.VISIBLE
            }
        }
    }
}