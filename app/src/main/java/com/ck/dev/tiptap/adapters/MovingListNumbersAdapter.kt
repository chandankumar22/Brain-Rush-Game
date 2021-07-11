package com.ck.dev.tiptap.adapters

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.LayerDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ck.dev.tiptap.R
import com.ck.dev.tiptap.extensions.fetchColor
import com.ck.dev.tiptap.extensions.fetchDrawable
import com.ck.dev.tiptap.models.VisibleNumberElement
import kotlinx.android.synthetic.main.list_item_visible_number.view.*

class MovingListNumbersAdapter(private val list: List<VisibleNumberElement>) :
    RecyclerView.Adapter<MovingListNumbersAdapter.ViewHolder>() {

    private lateinit var color:Color

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.list_item_visible_number, parent, false)
        )

    override fun getItemCount() = list.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.itemView.apply {
            val layerDrawable = context.fetchDrawable(R.drawable.layer_list_number) as LayerDrawable
            val btnBg = layerDrawable.findDrawableByLayerId(R.id.btn_front) as GradientDrawable
            val btnShadow = layerDrawable.findDrawableByLayerId(R.id.btn_shadow) as GradientDrawable
            btnBg.setColor(context.fetchColor(R.color.primaryDarkColor))
            number_tv.text = list[position].number.toString()
            occurrences_tv.text = list[position].occurrences.toString()
            isClickable = false
            isFocusable = false
            isEnabled = false
        }
    }
}