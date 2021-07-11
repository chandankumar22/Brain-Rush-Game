package com.ck.dev.tiptap.data

import androidx.room.TypeConverter
import com.ck.dev.tiptap.models.FindTheNumGameLevelData
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

object Converters {

    @TypeConverter
    @JvmStatic
    fun fromListOfString(value: List<String>?): String? {
        if (value == null) {
            return null
        }
        return value.joinToString(separator = ",")
    }


    @TypeConverter
    @JvmStatic
    fun toListOfString(value: String?): List<String>? {
        if (value == null) {
            return null
        }
        return value.split(",")
    }

    @TypeConverter
    @JvmStatic
    fun gameLevelDataToJson(value: List<FindTheNumGameLevelData>?): String? {
        return Gson().toJson(value)
    }


    @TypeConverter
    @JvmStatic
    fun jsonToGameLevelData(value: String?): List<FindTheNumGameLevelData> {
        return if (value == "null" ||  value.isNullOrEmpty()) emptyList()
        else {
            val listType: Type = object : TypeToken<List<FindTheNumGameLevelData>>() {}.type
            Gson().fromJson(value, listType)
        }
    }
}