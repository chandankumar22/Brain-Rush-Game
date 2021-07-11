package com.ck.dev.tiptap.data

import android.content.Context
import androidx.room.Room
import com.ck.dev.tiptap.helpers.AppConstants

object DatabaseFactory {
    private var INSTANCE: AppDatabase? = null

    fun getInstance(context: Context): AppDatabase {
        if (INSTANCE == null) {
            synchronized(AppDatabase::class) {
                INSTANCE = buildRoomDB(context)
            }
        }
        return INSTANCE!!
    }

    private fun buildRoomDB(context: Context) =
        Room.databaseBuilder(
            context.applicationContext,
            AppDatabase::class.java,
            AppConstants.APP_DB_NAME
        ).build()
}