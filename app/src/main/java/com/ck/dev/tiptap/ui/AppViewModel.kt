package com.ck.dev.tiptap.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ck.dev.tiptap.data.AppDatabaseHelperImpl
import com.ck.dev.tiptap.data.entity.BestScores
import com.ck.dev.tiptap.data.entity.Games
import com.ck.dev.tiptap.models.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONException
import timber.log.Timber

class AppViewModel() : ViewModel() {

    var name:String = ""
    var profilePic:String = ""
}