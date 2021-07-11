package com.ck.dev.tiptap.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import com.ck.dev.tiptap.R
import com.ck.dev.tiptap.adapters.GameChangerAdapter
import kotlinx.android.synthetic.main.activity_game_main_screen.*
import kotlinx.android.synthetic.main.layout_header.view.*
import timber.log.Timber
import kotlin.math.abs

class GameMainScreen : AppCompatActivity() {

   override fun onCreate(savedInstanceState: Bundle?) {
       Timber.i("onCreate called")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game_main_screen)
       intent.getStringExtra("userName")?.let {
           main_screen_header.user_name.text = it
       }
       val comp = CompositePageTransformer().also {
           it.addTransformer(MarginPageTransformer(40))
           it.addTransformer { page, position->
               val r = 1- abs(position)
               page.scaleY = 0.65f + r*0.35f
           }
       }

       view_pager.apply {
           adapter = GameChangerAdapter(this@GameMainScreen)
           clipToPadding = false
           clipChildren = false
           currentItem = 1
           offscreenPageLimit = 3
           getChildAt(0).overScrollMode = RecyclerView.OVER_SCROLL_NEVER
           setPageTransformer(comp)
       }
       // view_pager.adapter =
    }
}