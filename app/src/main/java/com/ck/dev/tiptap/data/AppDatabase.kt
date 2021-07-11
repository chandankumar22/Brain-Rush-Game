package com.ck.dev.tiptap.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.ck.dev.tiptap.data.dao.GamesDao
import com.ck.dev.tiptap.data.entity.BestScores
import com.ck.dev.tiptap.data.entity.Games

@Database(entities = [Games::class,BestScores::class], version = 1)
@TypeConverters(Converters::class)
abstract class AppDatabase :RoomDatabase() {

    abstract fun gamesDao(): GamesDao
}