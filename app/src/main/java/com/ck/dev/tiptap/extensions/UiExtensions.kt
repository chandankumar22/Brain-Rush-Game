package com.ck.dev.tiptap.extensions

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.LayerDrawable
import android.view.View
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import com.ck.dev.tiptap.R
import java.util.*

fun Context.fetchDrawable(@DrawableRes drawable: Int): Drawable {
    ContextCompat.getDrawable(this, drawable)?.let {
        return it
    }
    return ContextCompat.getDrawable(this, R.drawable.ic_default_image)!!
}

fun Context.fetchColor(@ColorRes color: Int): Int {
    ContextCompat.getColor(this, color).let {
        return it
    }
}

fun getRandomColor(): Int {
    val rnd = Random()
    return Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256))
}

fun Context.setLayerListFrontBg(@ColorRes color: Int,view:View){
    val layerDrawable = fetchDrawable(R.drawable.layer_list_number) as LayerDrawable
    val btnBg = layerDrawable.findDrawableByLayerId(R.id.btn_front) as GradientDrawable
    btnBg.setColor(ContextCompat.getColor(this,color))
    view.background = btnBg
}