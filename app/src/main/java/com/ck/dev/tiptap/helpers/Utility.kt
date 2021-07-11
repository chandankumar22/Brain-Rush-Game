package com.ck.dev.tiptap.helpers

import androidx.appcompat.app.AppCompatActivity
import timber.log.Timber
import java.io.IOException
import java.io.InputStream

fun AppCompatActivity.readJsonFromAsset(fileName: String): String? {
    Timber.i("readJsonFromAsset called")
    val json: String
    json = try {
        val inputStream: InputStream = assets.open(fileName)
        val size: Int = inputStream.available()
        val buffer = ByteArray(size)
        inputStream.read(buffer)
        inputStream.close()
        String(buffer, Charsets.UTF_8)
    } catch (ex: IOException) {
        return null
    }
    return json
}
