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
import timber.log.Timber


class FindTheNumGridAdapter(
    private val list: List<GridNumberElement>,
    private val callback: GridNumSelectCallback
) : RecyclerView.Adapter<FindTheNumGridAdapter.ViewHolder>() {

    private lateinit var layerDrawable: LayerDrawable
    private var listOfVisibleNums = listOf<VisibleNumberElement>()

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.list_item_number, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount() = list.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.itemView.apply {
            number_tv.text = list[position].number.toString()
            layerDrawable = context.fetchDrawable(R.drawable.layer_list_number) as LayerDrawable
            val btnBg = layerDrawable.findDrawableByLayerId(R.id.btn_front) as GradientDrawable
            btnBg.setColor(list[position].color)
            setOnClickListener {
                val nums = listOfVisibleNums.filter {
                    it.number == list[position].number
                }
                val currentNum=list[position].number
                if (nums.isNotEmpty()) {
                    // list[position].isSelected = true
                    var number:Int
                    Timber.i("current num $currentNum")
                    while (true){
                            Timber.i("finding next num ${list.size}")
                        number = java.util.Random().nextInt(list.size) + 1
                        Timber.i("number is $number")
                        if(number!=currentNum){
                            Timber.i("number is $number")
                            break
                        }
                    }
                    list[position].number = number
                    number_tv.text = number.toString()
                    callback.numSelected(currentNum)
                } else {
                    callback.numSelected(currentNum,false)
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
    fun numSelected(number: Int,isCorrect:Boolean=true)
}