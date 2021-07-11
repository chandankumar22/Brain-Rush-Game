package com.ck.dev.tiptap.ui.custom

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.GridLayout
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import com.ck.dev.tiptap.R
import com.ck.dev.tiptap.extensions.fetchColor
import com.ck.dev.tiptap.models.JumbledWord
import kotlinx.android.synthetic.main.layout_crossword_puzzle.view.*
import kotlinx.android.synthetic.main.list_item_jumbled_word.view.*
import timber.log.Timber


class CrosswordPuzzleView : LinearLayout {

    private lateinit var array: Array<Array<SquareView>>
    private var size: Int = 0
    private lateinit var adapter: JumbledWordsAdapter
    private lateinit var model: ArrayList<JumbledWord>



    constructor(context: Context, model: ArrayList<JumbledWord>, size: Int) : super(context) {
        this.model = model
        this.size = size
        array = Array(size){i->Array(size){j->SquareView(context)} }
        initViews()
    }

    constructor(context: Context, attr: AttributeSet) : super(context, attr) {

        /*var typedArray: TypedArray? = null
        try {
            typedArray = context.obtainStyledAttributes(attr, R.styleable.CustomButtonView)
            leftIc = typedArray.getResourceId(R.styleable.CustomButtonView_leftIcon, -1)
            rightIc = typedArray.getResourceId(R.styleable.CustomButtonView_rightIcon, -1)
            bg = typedArray.getResourceId(R.styleable.CustomButtonView_android_background, -1)
            text = typedArray.getString(R.styleable.CustomButtonView_android_text)
           // textSize = typedArray.getFloat(R.styleable.CustomButtonView_android_textSize,context.resources.getDimension(R.dimen._14ssp))
            initViews()
        } catch (ex: Exception) {
            ex.printStackTrace()
        } finally {
            typedArray?.recycle()
        }*/
    }

    private fun initViews() {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        inflater.inflate(R.layout.layout_crossword_puzzle, this)
        setConstraints()


//        val adapter = PuzzleAdapter(context, model)
//        grid_view.setAdapter(adapter)

        /*val gridLayoutManager =
            GridLayoutManager(context, size, LinearLayoutManager.HORIZONTAL, false)
        crossword_rv.layoutManager = gridLayoutManager
        adapter = JumbledWordsAdapter(model)
        crossword_rv.adapter = adapter
        val spacing = 0
        val includeEdge = false
        crossword_rv.addItemDecoration(
            CrosswordPuzzleItemDecoration(
                spanCount = size,
                spacing = spacing,
                includeEdge = includeEdge
            )
        )*/
    }

    private fun setConstraints() {

        val MAX_COLUMN: Int = size //5

        val MAX_ROW: Int = size //7

        //grid_view.setColumnCount(MAX_COLUMN);
        //grid_view.setRowCount(MAX_ROW);

        val itemsCount = MAX_ROW * MAX_COLUMN //35


        var row = 0
        var column = 0

        for (i in 0 until itemsCount) {
            val view = LayoutInflater.from(context)
                .inflate(R.layout.list_item_jumbled_word, this, false)
            view.char_text.text = model[i].letter.toString()
            if (!model[i].isIncluded) view.visibility = View.INVISIBLE
            val params: GridLayout.LayoutParams =
                GridLayout.LayoutParams(GridLayout.spec(row, 1f), GridLayout.spec(column, 1f))
            view.layoutParams = params
             grid_view.addView(view)
            array[row][column] = view as SquareView
            column++
            if (column >= MAX_COLUMN) {
                column = 0
                row++
            }
        }
    }



    fun revealTheChar(startPos: String, endPos: String) {
        Timber.i("revealTheChar called $startPos-$endPos")
        var startPosI: Int = 0
        var startPosJ: Int = 0
        var endPosI: Int = 0
        var endPosJ: Int = 0
        startPos.split("-").apply {
            startPosI = this[0].toInt()
            startPosJ = this[1].toInt()
        }
        endPos.split("-").apply {
            endPosI = this[0].toInt()
            endPosJ =  this[1].toInt()
        }
        if (startPosI != endPosI && startPosJ == endPosJ) {//0-1 to 2-1 (vertical)
            for (i in startPosI..endPosI) {
                val view = array[i][startPosJ]
                view.char_text.setTextColor(context.fetchColor(R.color.primaryTextColor))
            }

        }else if (startPosJ != endPosJ && startPosI == endPosI) {//1-0 to 1-2 (horizontal)
            for (i in startPosJ..endPosJ) {
                val view = array[startPosI][i]
                view.char_text.setTextColor(context.fetchColor(R.color.primaryTextColor))
            }
        }

        /*for (i in start..end) {
                val view = array[]

            (grid_view.getChildAt(i) as SquareView).apply {
                this
            }
        }*/
    }

}

class JumbledWordsAdapter(private val jumbledChar: List<JumbledWord>) :
    RecyclerView.Adapter<JumbledWordsAdapter.ViewHolder>() {
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.list_item_jumbled_word, parent, false)
        )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.itemView.apply {
            with(jumbledChar[position]) {
                if(isIncluded){
                    visibility = View.VISIBLE
                    char_text.text = this.letter.toString()
                    if(isReveled){
                        char_text.setTextColor(context.fetchColor(R.color.primaryTextColor))
                    }else{
                        char_text.setTextColor(context.fetchColor(R.color.primaryDarkColor))
                    }
                }else{
                    visibility = View.GONE
                }
                /*
                if (!isIncluded) {
                    Timber.i("hiding visibility at position $position")
                    holder.itemView.visibility = View.GONE
                } else if (isIncluded && isReveled) {



                } else {
                    visibility = View.VISIBLE
                    char_text.text = this.letter.toString()

                }*/
            }
        }
    }

    fun revealTheChar(startPos: String, endPos: String) {
        Timber.i("revealTheChar called $startPos-$endPos")
        var start: Int = 0
        var end: Int = 0
        startPos.split("-").apply {
            start = this[0].toInt() + this[1].toInt()
        }
        endPos.split("-").apply {
            end = this[0].toInt() + this[1].toInt()
        }
        for (i in start..end) {
            jumbledChar[i].isReveled = true
            notifyItemChanged(i)
        }
    }

    override fun getItemCount() = jumbledChar.size

}

class PuzzleAdapter(context: Context, courseModelArrayList: ArrayList<JumbledWord>) :
    ArrayAdapter<JumbledWord>(context, 0, courseModelArrayList) {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var listitemView = convertView
        if (listitemView == null) {
            // Layout Inflater inflates each item to be displayed in GridView.
            listitemView =
                LayoutInflater.from(context).inflate(R.layout.list_item_levels, parent, false)
        }
        val courseModel = getItem(position)
        listitemView!!.char_text.text = courseModel?.letter.toString()
        return listitemView
    }
}