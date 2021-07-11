package com.ck.dev.tiptap.ui.custom

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.annotation.DrawableRes
import com.ck.dev.tiptap.R
import com.ck.dev.tiptap.extensions.fetchDrawable
import kotlinx.android.synthetic.main.custom_button_view.view.*

class CustomButtonView : LinearLayout {

    @DrawableRes
    private var leftIc: Int = -1

    @DrawableRes
    private var rightIc: Int = -1

    @DrawableRes
    private var bg: Int = -1
    var text: String? = ""


    constructor(context: Context) : super(context)

    constructor(context: Context, attr: AttributeSet) : super(context, attr) {
        var typedArray: TypedArray? = null
        try {
            typedArray = context.obtainStyledAttributes(attr, R.styleable.CustomButtonView)
            leftIc = typedArray.getResourceId(R.styleable.CustomButtonView_leftIcon, -1)
            rightIc = typedArray.getResourceId(R.styleable.CustomButtonView_rightIcon, -1)
            bg = typedArray.getResourceId(R.styleable.CustomButtonView_android_background, -1)
            text = typedArray.getString(R.styleable.CustomButtonView_android_text)
            initViews()
        } catch (ex: Exception) {
            ex.printStackTrace()
        } finally {
            typedArray?.recycle()
        }
    }

    private fun initViews() {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        inflater.inflate(R.layout.custom_button_view, this)

        button_text.text = text

        if (leftIc == -1) {
            left_ic.visibility = GONE
        } else {
            left_ic.visibility = VISIBLE
            left_ic.setImageDrawable(context.fetchDrawable(leftIc))
        }

        if (rightIc == -1) {
            right_ic.visibility = GONE
        } else {
            right_ic.visibility = VISIBLE
            right_ic.setImageDrawable(context.fetchDrawable(leftIc))
        }

        if (bg == -1) {
            background = context.fetchDrawable(R.drawable.layer_list_button_ui)
        } else {
            background = context.fetchDrawable(bg)
        }
    }

    fun set(value: String) {
        button_text.text = value
    }
}