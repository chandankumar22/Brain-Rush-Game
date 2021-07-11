package com.ck.dev.tiptap.ui

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.ck.dev.tiptap.helpers.SharedPreferenceHelper
import com.ck.dev.tiptap.ui.GameApp.Companion.hasCoinsUpdated
import com.ck.dev.tiptap.viewmodelfactories.FindTheNumberVmFactory
import timber.log.Timber

open class BaseActivity : AppCompatActivity() {

    val viewModel:AppViewModel by viewModels()

    override fun onResume() {
        Timber.i("onResume called")
        super.onResume()
        if(SharedPreferenceHelper.isDarkMode){
            AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_NO)
        }else{
            AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_YES)
        }
    }
}