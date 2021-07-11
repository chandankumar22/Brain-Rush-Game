package com.ck.dev.tiptap.ui.custom

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
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
    private var textSize: Float=0f


    constructor(context: Context) : super(context)

    constructor(context: Context, attr: AttributeSet) : super(context, attr) {
        var typedArray: TypedArray? = null
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
        }
    }

    private fun initViews() {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        inflater.inflate(R.layout.custom_button_view, this)

        button_text.apply {
            text = this@CustomButtonView.text
         //   textSize = this@CustomButtonView.textSize
        }

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

    fun setBtnText(value: String) {
        button_text.text = value
    }

    fun setBtnTextColor(@ColorRes color: Int) {
        button_text.setTextColor(ContextCompat.getColor(context,color))
    }

    fun setRightIc(@DrawableRes drawable: Int?){
        if (drawable == null) {
            right_ic.visibility = GONE
        } else {
            right_ic.visibility = VISIBLE
            right_ic.setImageDrawable(context.fetchDrawable(drawable))
        }
    }
}