package com.ck.dev.tiptap.helpers

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import com.ck.dev.tiptap.models.FindTheNumberGameRule
import com.google.gson.Gson
import timber.log.Timber
import java.io.IOException
import java.io.InputStream
import java.math.RoundingMode
import java.text.DecimalFormat

fun AppCompatActivity.readJsonFromAsset(fileName: String): String {
    Timber.i("readJsonFromAsset called")
   return  try {
        val inputStream: InputStream = assets.open(fileName)
        val size: Int = inputStream.available()
        val buffer = ByteArray(size)
        inputStream.read(buffer)
        inputStream.close()
        String(buffer, Charsets.UTF_8)
    } catch (ex: IOException) {
        ""
    }
}

fun Double.roundTo2Digit(): Double {
    val df = DecimalFormat("#.##")
    df.roundingMode = RoundingMode.CEILING
    return df.format(this).toDouble()
}

fun getRandomString(length: Int = 30): String {
    val allowedChars = ('A'..'Z') + ('a'..'z') + ('0'..'9')
    return (1..length)
        .map { allowedChars.random() }
        .joinToString("")
}

fun Context.assetToBitmap(fileName: String): Bitmap? {
    return try {
        with(assets.open("$fileName.png")) {
            BitmapFactory.decodeStream(this)
        }
    } catch (e: IOException) {
        Timber.e(e)
        null
    }
}