package com.ck.dev.tiptap.ui.custom

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ck.dev.tiptap.R
import com.ck.dev.tiptap.models.RememberTheCardData
import com.ck.dev.tiptap.ui.games.rememberthecard.PlayRememberTheCardGameFragment
import kotlinx.android.synthetic.main.layout_remember_card.view.*
import kotlinx.android.synthetic.main.list_item_remember_card.view.*
import timber.log.Timber


class RememberCardView : LinearLayout, OnItemClick {

    private lateinit var adapter: RememberCardAdapter
    private var col: Int = 0
    private var model: ArrayList<RememberTheCardData> = ArrayList()
    private var currentIdSel: String = ""
    private var prevIdSelPos: Int = 0
    private var isCurrentSet = false
    private lateinit var gameFragment: PlayRememberTheCardGameFragment
    private var isEndless: Boolean = false


    constructor(
        context: Context,
        model: ArrayList<RememberTheCardData>,
        col: Int,
        gameFr: PlayRememberTheCardGameFragment
    ) : super(context) {
        this.col = col
        this.model = model
        gameFragment = gameFr
        isEndless = gameFragment.isEndless
        initViews()
    }

    constructor(context: Context, attr: AttributeSet) : super(context, attr)

    private fun initViews() {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        inflater.inflate(R.layout.layout_remember_card, this)
        setRecyclerView()
    }

    fun hideAllCards() {
        Timber.i("hideAllCards called")
        if (::adapter.isInitialized) {
            adapter.setAllRevealFalse()
        }
    }

    private fun setRecyclerView() {
        adapter = RememberCardAdapter(model, this, isEndless)
        val gridLayoutManager =
            GridLayoutManager(context, col/*, LinearLayoutManager.HORIZONTAL, false*/)
        rem_card_rv.layoutManager = gridLayoutManager
        rem_card_rv.adapter = adapter
    }

    override fun onPicSelected(position: Int) {
        Timber.i("onPicSelected called with isCurrentSet=$isCurrentSet")
        gameFragment.increaseMovesCounter()
        val rememberTheCardData = model[position]
        if (isCurrentSet) {
            Timber.i("2nd image for pair with previous id $currentIdSel at previous position=$prevIdSelPos")
            Timber.i("current id for the card selected ${rememberTheCardData.id}")
            if (rememberTheCardData.id == currentIdSel) {
                Timber.i("card matched")
                adapter.setRevealImage(position, true)
                gameFragment.updateTime()
            } else {
                Timber.i("card do not matched")
                adapter.setRevealImage(position, true)
                Handler(Looper.myLooper()!!).postDelayed({
                    adapter.setRevealImage(position, false)
                    adapter.setRevealImage(prevIdSelPos, false)
                }, 500)
            }
        } else {
            currentIdSel = rememberTheCardData.id
            prevIdSelPos = position
            Timber.i("1st image for pair with id $currentIdSel at position=$position")
            adapter.setRevealImage(position, true)
        }
        isCurrentSet = !isCurrentSet
        if (adapter.isAllPairRevealed()) {
            Timber.i("isAllPairRevealed is true")
            gameFragment.updateTime(true)
        }
        Timber.i("=====================after=====================")
        adapter.printAll()
    }

    override fun onPicSelectedEndless(rememberTheCardData: RememberTheCardData, position: Int) {
        Timber.i("onPicSelectedEndless called at pos ${rememberTheCardData}}")
        adapter.setRevealImage(position,true)
        gameFragment.checkIfMatched(rememberTheCardData,position)
    }

    fun hideCardAt(position: Int){
        Timber.i("hideCardAt called at pos ${position}}")
        Handler(Looper.myLooper()!!).postDelayed({
            adapter.setRevealImage(position,false)
        },500)
    }

    fun showCardAt(position: Int){
        Timber.i("showCardAt called at pos ${position}}")
        //adapter.setRevealImage(position,true)
    }

}

interface OnItemClick {
    fun onPicSelected(position: Int)
    fun onPicSelectedEndless(rememberTheCardData: RememberTheCardData, position: Int)
}

class RememberCardAdapter(
    private val model: List<RememberTheCardData>,
    private val onItemClick: OnItemClick,
    private val isEndless: Boolean
) :
    RecyclerView.Adapter<RememberCardAdapter.ViewHolder>() {
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.list_item_remember_card, parent, false)
        )

    fun setAllRevealFalse() {
        Timber.i("setAllRevealFalse called")
        for (i in model.indices) {
            model[i].isRevealed = false
            model[i].isLocked = false
        }
        notifyDataSetChanged()
    }

    fun isAllPairRevealed(): Boolean {
        Timber.i("isAllPairRevealed called")
        for (i in model.indices) {
            if (!model[i].isRevealed) return false
        }
        return true
    }

    fun setRevealImage(position: Int, isRevealed: Boolean) {
        Timber.i("setRevealImage called at $position")
        model[position].isRevealed = isRevealed
        model[position].isLocked = isRevealed
        notifyItemChanged(position)
        //notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Timber.i("onBindViewHolder called")
        holder.itemView.apply {
            with(model[position]) {
                Glide.with(context).load(drawableRes).into(img_tv)
                if (isRevealed) {
                    img_tv.visibility = View.VISIBLE
                } else {
                    img_tv.visibility = View.GONE
                }
                this@apply.setOnClickListener {
                    Timber.i("card.onclick called for $position with $isLocked")
                    Timber.i("=====================before=====================")
                    //printAll()
                    if (!isLocked) {
                        isLocked = true
                        if (isEndless) {
                            onItemClick.onPicSelectedEndless(this,position)
                        } else onItemClick.onPicSelected(position)
                    }
                }
                /*background = context.fetchDrawable(drawableRes)*/
            }
        }
    }

    fun printAll() {
        var i = 0
        model.forEach {
            Timber.i("pos $i : locked = ${it.isLocked}, revealed = ${it.isRevealed}, id = ${it.id}")
            i++
        }
    }

    override fun getItemCount():Int {
        Timber.i("getItemCount ${model.size}")
        return model.size
    }

}