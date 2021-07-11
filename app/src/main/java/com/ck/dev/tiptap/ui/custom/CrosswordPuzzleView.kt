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
import com.ck.dev.tiptap.models.JumbledWordData
import kotlinx.android.synthetic.main.layout_crossword_puzzle.view.*
import kotlinx.android.synthetic.main.list_item_jumbled_word.view.*
import timber.log.Timber


class CrosswordPuzzleView : LinearLayout {

    private lateinit var array: Array<Array<SquareView>>
    private var rowSize: Int = 0
    private var colSize: Int = 0
    private var model: ArrayList<JumbledWordData> = ArrayList()
    var numOfBoxes = 0



    constructor(context: Context, model: ArrayList<JumbledWordData>, rowSize: Int,colSize:Int) : super(context) {
        this.model = model
        this.rowSize = rowSize
        this.colSize = colSize
        array = Array(rowSize){ i->Array(rowSize){ j->SquareView(context)} }
        model.forEach {
           if(it.isIncluded) numOfBoxes++
        }
        initViews()
    }

    constructor(context: Context, attr: AttributeSet) : super(context, attr) {
    }

    fun isAllCharRevealed(): Boolean {
        Timber.i("isAllCharRevealed called")
        var revealCount = 0
        model.forEach {
            if(it.isReveled) revealCount++
        }
        Timber.i("reveal count : $revealCount")
        return numOfBoxes==revealCount
    }

    private fun initViews() {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        inflater.inflate(R.layout.layout_crossword_puzzle, this)
        setConstraints()
    }

    private fun setConstraints() {

        val MAX_COLUMN: Int = rowSize //5

        val MAX_ROW: Int = colSize //7

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
        var startPosI: Int
        var startPosJ: Int
        var endPosI: Int
        var endPosJ: Int
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
                model[startPosJ + (i*colSize)].isReveled = true
            }

        }else if (startPosJ != endPosJ && startPosI == endPosI) {//1-0 to 1-2 (horizontal)
            for (i in startPosJ..endPosJ) {
                val view = array[startPosI][i]
                view.char_text.setTextColor(context.fetchColor(R.color.primaryTextColor))
                model[i + (startPosI*colSize)].isReveled = true
            }
        }
    }


}
