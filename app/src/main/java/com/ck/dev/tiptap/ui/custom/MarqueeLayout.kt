package com.ck.dev.tiptap.ui.custom

import android.content.Context
import android.view.animation.Animation
import android.view.animation.TranslateAnimation
import android.widget.FrameLayout

class MarqueeLayout : FrameLayout {

    constructor(context: Context) : super(context) {
        animation = TranslateAnimation(
            Animation.RELATIVE_TO_SELF,
            1f,
            Animation.RELATIVE_TO_SELF,
            -1f,
            Animation.RELATIVE_TO_SELF,
            0f,
            Animation.RELATIVE_TO_SELF,
            0f
        )
        animation.repeatCount = Animation.INFINITE
        animation.repeatMode = Animation.RESTART
    }

    fun setDuration(durationInMillis: Int) {
        animation.duration = durationInMillis.toLong()
    }

    fun startAnimation() {
        startAnimation(animation)
    }
}