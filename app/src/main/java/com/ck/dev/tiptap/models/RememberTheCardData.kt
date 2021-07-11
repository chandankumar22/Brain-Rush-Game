package com.ck.dev.tiptap.models

import android.graphics.drawable.Drawable
import androidx.annotation.DrawableRes
import kotlin.random.Random

data class RememberTheCardData(
    val drawableRes: Drawable,
    val id: String,
    var isRevealed: Boolean = true,
    var isLocked:Boolean = true,
    var isDuplicate:Boolean = false
)