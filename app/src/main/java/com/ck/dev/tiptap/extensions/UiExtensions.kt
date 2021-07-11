package com.ck.dev.tiptap.extensions

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.LayerDrawable
import android.util.Base64
import android.view.View
import android.view.WindowManager
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.ck.dev.tiptap.R
import com.ck.dev.tiptap.helpers.SharedPreferenceHelper
import com.ck.dev.tiptap.models.DialogData
import kotlinx.android.synthetic.main.layout_header.view.*
import kotlinx.android.synthetic.main.profile_image.view.*
import timber.log.Timber
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
    findViewById<ConstraintLayout>(R.id.header).apply {
        user_name.text = SharedPreferenceHelper.userName
        val image = Base64.decode(SharedPreferenceHelper.profilePic, Base64.DEFAULT)
        Glide.with(this).load(image).into(profile_img.profile_pic_iv)
        header_coins.text = SharedPreferenceHelper.coins.toString()
        user_game_rating.rating = SharedPreferenceHelper.currentUserRating.toFloat()
        setBackgroundColor(fetchColor(color))
        findViewById<RelativeLayout>(R.id.profile_img).setBackgroundColor(if (color == R.color.primaryDarkColor) fetchColor(R.color.primaryLightColor) else fetchColor(R.color.primaryDarkColor))
    }

}

fun AppCompatActivity.changeStatusBarColor(@ColorRes color: Int) {
    val window = this.window
    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
//    window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
    window.statusBarColor = fetchColor(color)
}

fun Context.handleGameAllLevelComplete() {
    Toast.makeText(this,
            getString(R.string.all_level_complete_msg), Toast.LENGTH_LONG).show()
}