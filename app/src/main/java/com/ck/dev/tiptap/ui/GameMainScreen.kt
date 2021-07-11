package com.ck.dev.tiptap.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.ck.dev.tiptap.R
import com.ck.dev.tiptap.adapters.GameChangerAdapter
import kotlinx.android.synthetic.main.activity_game_main_screen.*
import kotlinx.android.synthetic.main.layout_header.view.*
import timber.log.Timber

class GameMainScreen : AppCompatActivity() {

   override fun onCreate(savedInstanceState: Bundle?) {
       Timber.i("onCreate called")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game_main_screen)
       intent.getStringExtra("userName")?.let {
           main_screen_header.user_name.text = it
       }
        view_pager.adapter = GameChangerAdapter(this)
    }
}