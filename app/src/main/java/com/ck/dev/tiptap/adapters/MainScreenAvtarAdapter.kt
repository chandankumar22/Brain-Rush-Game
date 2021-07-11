package com.ck.dev.tiptap.adapters

import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ck.dev.tiptap.R
import kotlinx.android.synthetic.main.list_item_avtar.view.*

class MainScreenAvtarAdapter(private val list: List<Drawable>) :
    RecyclerView.Adapter<MainScreenAvtarAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.list_item_avtar, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount() = list.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.itemView.apply {
            Glide.with(context).load(list[position]).dontAnimate().into(avtar_img)
        }
    }
}
