package com.ck.dev.tiptap.adapters

import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.LayerDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.ck.dev.tiptap.R
import com.ck.dev.tiptap.extensions.fetchDrawable
import com.ck.dev.tiptap.models.GridNumberElement
import com.ck.dev.tiptap.models.VisibleNumberElement
import kotlinx.android.synthetic.main.list_item_number.view.*


class FindTheNumGridAdapter(
    private val list: List<GridNumberElement>,
    private val callback: GridNumSelectCallback
) : RecyclerView.Adapter<FindTheNumGridAdapter.ViewHolder>() {

    private lateinit var layerDrawable: LayerDrawable
    private var listOfVisibleNums = listOf<VisibleNumberElement>()
    private val TYPE_FULL = 0
    private val TYPE_HALF = 1
    private val TYPE_QUARTER = 2

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    }

/*    override fun getItemViewType(position: Int): Int {
        when (position % 8) {
            0, 5 -> return TYPE_HALF
            1, 2, 4, 6 -> return TYPE_QUARTER
        }
        return TYPE_FULL
    }*/

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.list_item_number, parent, false)
        // drawStaggeredView(view, viewType, parent)
        return ViewHolder(view)
    }

    fun drawStaggeredView(itemView: View, viewType: Int, parent: ViewGroup) {
        itemView.getViewTreeObserver()
            .addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {
                override fun onPreDraw(): Boolean {
                    val type: Int = viewType
                    val lp: ViewGroup.LayoutParams = itemView.getLayoutParams()
                    if (lp is StaggeredGridLayoutManager.LayoutParams) {
                        val sglp =
                            lp
                        when (type) {
                            TYPE_FULL -> sglp.isFullSpan = true
                            TYPE_HALF -> {
                                sglp.isFullSpan = false
                                sglp.width = itemView.getWidth() / 2
                            }
                            TYPE_QUARTER -> {
                                sglp.isFullSpan = false
                                sglp.width = itemView.getWidth() / 2
                                sglp.height = itemView.getHeight() / 2
                            }
                        }
                        itemView.setLayoutParams(sglp)
                        val lm =
                            (parent as RecyclerView).layoutManager as StaggeredGridLayoutManager?
                        lm!!.invalidateSpanAssignments()
                    }
                    itemView.getViewTreeObserver().removeOnPreDrawListener(this)
                    return true
                }
            })
    }

    override fun getItemCount() = list.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.itemView.apply {
            number_tv.text = list[position].number.toString()
            layerDrawable = context.fetchDrawable(R.drawable.layer_list_number) as LayerDrawable
            val btnBg = layerDrawable.findDrawableByLayerId(R.id.btn_front) as GradientDrawable
            val btnShadow = layerDrawable.findDrawableByLayerId(R.id.btn_shadow) as GradientDrawable
            btnBg.setColor(list[position].color)
            setOnClickListener {
                val nums = listOfVisibleNums.filter {
                    it.number == list[position].number
                }

                if (nums.isNotEmpty()) {
                    // list[position].isSelected = true
                    val currentNum=list[position].number
                    var number:Int
                    while (true){
                        number = java.util.Random().nextInt(25) + 1
                        if(number!=currentNum){
                            break
                        }
                    }
                    list[position].number = number
                    number_tv.text = number.toString()
                    callback.numSelected(currentNum)
                } else {
                    callback.numSelected(-1)
                }


                /* if (!list[position].isSelected) {
                     if (true*//*listOfVisibleNums.contains(list[position].number.toString())*//*) {
                        list[position].isSelected = true
                        callback.numSelected(list[position].number)
                        this.backgroundTintList =
                            AppCompatResources.getColorStateList(context, android.R.color.black)
                    } else this.backgroundTintList = null
                } else {
                    this.backgroundTintList =
                        AppCompatResources.getColorStateList(context, android.R.color.black)
                }*/
            }

        }
    }

    fun getCurrentGrid() = list

    fun currentVisibleList(listOfVisibleNums: List<VisibleNumberElement>) {
        // Log.i(javaClass.name, "currentVisibleList called with $listOfVisibleNums")
        this.listOfVisibleNums = listOfVisibleNums
    }
}

interface GridNumSelectCallback {
    fun numSelected(number: Int)
}