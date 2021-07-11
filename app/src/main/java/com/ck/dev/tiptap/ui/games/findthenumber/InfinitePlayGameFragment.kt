package com.ck.dev.tiptap.ui.games.findthenumber

import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.LayerDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ck.dev.tiptap.R
import com.ck.dev.tiptap.extensions.fetchDrawable
import com.ck.dev.tiptap.extensions.getRandomColor
import com.ck.dev.tiptap.ui.games.BaseFragment
import kotlinx.android.synthetic.main.fragment_infinite_play_game.*
import timber.log.Timber

class InfinitePlayGameFragment : BaseFragment(R.layout.fragment_infinite_play_game) {

    private var visibleNum: String = "1"
    private var gridSize = 0
    private lateinit var navController: NavController

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Timber.i("onViewCreated called")
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment)
        setListeners()
        visible_num_ip_tv.setBtnText(visibleNum)
        setVisibleNumPreview()
    }

    private fun setGridPreview(size: Int) {
        val list = ArrayList<Int>()
        for (i in 0 until size * size) {
            list.add(getRandomColor())
        }
        val adapter = PreviewAdapter(list)
        unedit_grid_num_rv.layoutManager = GridLayoutManager(requireContext(), size)
        unedit_grid_num_rv.adapter = adapter
        gridSize = size
    }

    private fun setVisibleNumPreview() {
        val list = ArrayList<Int>()
        for (i in 0 until visibleNum.toInt()) {
            list.add(getRandomColor())
        }
        val adapter = PreviewAdapter(list)
        unedit_visible_nums_rv.layoutManager =
            GridLayoutManager(requireContext(), visibleNum.toInt())
        unedit_visible_nums_rv.adapter = adapter
    }

    private fun setListeners() {
        box_size_2_by_2.setOnClickListener {
            setCheckedButton(2)
            setGridPreview(2)
        }
        box_size_3_by_3.setOnClickListener {
            setCheckedButton(3)
            setGridPreview(3)
        }
        box_size_4_by_4.setOnClickListener {
            setCheckedButton(4)
            setGridPreview(4)
        }
        box_size_5_by_5.setOnClickListener {
            setCheckedButton(5)
            setGridPreview(5)
        }
        box_size_6_by_6.setOnClickListener {
            setCheckedButton(6)
            setGridPreview(6)
        }
        box_size_7_by_7.setOnClickListener {
            setCheckedButton(7)
            setGridPreview(7)
        }

        add_visible_nums.setOnClickListener {
            if (visibleNum.toInt() < 5) {
                visibleNum = (visibleNum.toInt() + 1).toString()
                visible_num_ip_tv.setBtnText(visibleNum)
                setVisibleNumPreview()
            }
        }
        minus_visible_nums.setOnClickListener {
            if (visibleNum.toInt() > 1) {
                visibleNum = (visibleNum.toInt() - 1).toString()
                visible_num_ip_tv.setBtnText(visibleNum)
                setVisibleNumPreview()
            }
        }

        create_game_button.setOnClickListener {
            if(gridSize>0){
                val action =
                    InfinitePlayGameFragmentDirections.actionInfinitePlayGameFragmentToGameScreenFragment(
                        gridSize = gridSize,
                        visibleNums = visibleNum.toInt(),
                        time = 0, level = "",isEndless = true
                    )
                navController.navigate(action)
            }
        }
    }

    private fun setCheckedButton(size: Int) {
        when (size) {
            2 -> {
                box_size_2_by_2.setRightIc(R.drawable.ic_check)
                box_size_3_by_3.setRightIc(null)
                box_size_4_by_4.setRightIc(null)
                box_size_5_by_5.setRightIc(null)
                box_size_6_by_6.setRightIc(null)
                box_size_7_by_7.setRightIc(null)
            }
            3 -> {
                box_size_2_by_2.setRightIc(null)
                box_size_3_by_3.setRightIc(R.drawable.ic_check)
                box_size_4_by_4.setRightIc(null)
                box_size_5_by_5.setRightIc(null)
                box_size_6_by_6.setRightIc(null)
                box_size_7_by_7.setRightIc(null)
            }
            4 -> {
                box_size_2_by_2.setRightIc(null)
                box_size_3_by_3.setRightIc(null)
                box_size_4_by_4.setRightIc(R.drawable.ic_check)
                box_size_5_by_5.setRightIc(null)
                box_size_6_by_6.setRightIc(null)
                box_size_7_by_7.setRightIc(null)
            }
            5 -> {
                box_size_2_by_2.setRightIc(null)
                box_size_3_by_3.setRightIc(null)
                box_size_4_by_4.setRightIc(null)
                box_size_5_by_5.setRightIc(R.drawable.ic_check)
                box_size_6_by_6.setRightIc(null)
                box_size_7_by_7.setRightIc(null)
            }
            6 -> {
                box_size_2_by_2.setRightIc(null)
                box_size_3_by_3.setRightIc(null)
                box_size_4_by_4.setRightIc(null)
                box_size_5_by_5.setRightIc(null)
                box_size_6_by_6.setRightIc(R.drawable.ic_check)
                box_size_7_by_7.setRightIc(null)
            }
            7 -> {
                box_size_2_by_2.setRightIc(null)
                box_size_3_by_3.setRightIc(null)
                box_size_4_by_4.setRightIc(null)
                box_size_5_by_5.setRightIc(null)
                box_size_6_by_6.setRightIc(null)
                box_size_7_by_7.setRightIc(R.drawable.ic_check)
            }
        }
    }
}

class PreviewAdapter(private val colors: List<Int>) :
    RecyclerView.Adapter<PreviewAdapter.ViewHolder>() {
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.list_item_number, parent, false)
        )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.itemView.apply {
            /*val size = context.resources.getDimension(R.dimen._28sdp).toInt()
            val params = LinearLayout.LayoutParams(size,size)
            params.gravity = Gravity.CENTER
            layoutParams = params*/
            val layerDrawable = context.fetchDrawable(R.drawable.layer_list_number) as LayerDrawable
            val btnBg = layerDrawable.findDrawableByLayerId(R.id.btn_front) as GradientDrawable
            btnBg.setColor(colors[position])
        }
    }

    override fun getItemCount() = colors.size

}