package com.ck.dev.tiptap.viewmodelfactories

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ck.dev.tiptap.data.AppDatabaseHelperImpl
import com.ck.dev.tiptap.data.DatabaseFactory
import com.ck.dev.tiptap.ui.games.findthenumber.FindTheNumberViewModel

class FindTheNumberVmFactory(val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        val appDatabase = DatabaseFactory.getInstance(context)
        val database = AppDatabaseHelperImpl(appDatabase)
        return FindTheNumberViewModel(database) as T
    }
}