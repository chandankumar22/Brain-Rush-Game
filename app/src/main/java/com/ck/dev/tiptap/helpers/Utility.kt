package com.ck.dev.tiptap.helpers

import androidx.appcompat.app.AppCompatActivity
import com.ck.dev.tiptap.models.FindTheNumberGameRule
import com.google.gson.Gson
import timber.log.Timber
import java.io.IOException
import java.io.InputStream

fun AppCompatActivity.readJsonFromAsset(fileName: String): Array<FindTheNumberGameRule> {
    Timber.i("readJsonFromAsset called")
   return  try {
        val inputStream: InputStream = assets.open(fileName)
        val size: Int = inputStream.available()
        val buffer = ByteArray(size)
        inputStream.read(buffer)
        inputStream.close()
        val json = String(buffer, Charsets.UTF_8)
        Gson().fromJson(json, Array<FindTheNumberGameRule>::class.java)
    } catch (ex: IOException) {
        emptyArray()
    }
}
