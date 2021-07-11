package com.ck.dev.tiptap.extensions

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.LayerDrawable
import android.view.View
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.ck.dev.tiptap.R
import com.ck.dev.tiptap.models.DialogData
import timber.log.Timber
import java.io.IOException
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

fun Context.setLayerListFrontBg(@DrawableRes drawable: Int, @ColorRes color: Int, view: View) {
    val layerDrawable = fetchDrawable(drawable) as LayerDrawable
    val btnBg = layerDrawable.findDrawableByLayerId(R.id.btn_front) as GradientDrawable
    btnBg.setColor(ContextCompat.getColor(this, color))
    view.background = btnBg
}

fun Context.getGameExitPopup(pos: () -> Unit, neg: () -> Unit): DialogData {
    Timber.i("setBackButtonHandling called")
    return DialogData(
        title = getString(R.string.game_exit_title),
        content = getString(R.string.game_exit_content),
        posBtnText = getString(R.string.game_exit_positive_btn_txt),
        negBtnText = getString(R.string.game_exit_negative_btn_txt),
        posListener = { pos() },
        megListener = { neg() }
    )
}

fun AppCompatActivity.setHeaderBgColor(@ColorRes color: Int) {
    findViewById<ConstraintLayout>(R.id.header).setBackgroundColor(fetchColor(color))
}