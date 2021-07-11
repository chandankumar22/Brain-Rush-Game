package com.ck.dev.tiptap.ui

import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.ck.dev.tiptap.R
import com.ck.dev.tiptap.adapters.MainScreenAvtarAdapter
import com.ck.dev.tiptap.extensions.fetchDrawable
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val listOfAvtarDrawables = getAvtarDrawableList()
        avatar_rv.adapter = MainScreenAvtarAdapter(listOfAvtarDrawables)
        avatar_rv.setHasFixedSize(true)
        avatar_rv.layoutManager = LinearLayoutManager(this)

        setListeners()
    }

    private fun getAvtarDrawableList(): ArrayList<Drawable> {
        return arrayListOf<Drawable>().apply {
            add(fetchDrawable(R.drawable.ic_avtar_boy_1))
            add(fetchDrawable(R.drawable.ic_avtar_boy_2))
            add(fetchDrawable(R.drawable.ic_avtar_boy_3))
            add(fetchDrawable(R.drawable.ic_avtar_boy_3))
            add(fetchDrawable(R.drawable.ic_avtar_boy_3))
            add(fetchDrawable(R.drawable.ic_avtar_boy_3))
            add(fetchDrawable(R.drawable.ic_avtar_boy_3))
            add(fetchDrawable(R.drawable.ic_avtar_girl_2))
            add(fetchDrawable(R.drawable.ic_avtar_girl_3))
            add(fetchDrawable(R.drawable.ic_avtar_girl_1))
            add(fetchDrawable(R.drawable.ic_avtar_girl_1))
            add(fetchDrawable(R.drawable.ic_avtar_girl_1))
            add(fetchDrawable(R.drawable.ic_avtar_girl_1))
            add(fetchDrawable(R.drawable.ic_avtar_girl_1))
        }.shuffled() as ArrayList<Drawable>
    }

    private fun setListeners() {
        root?.let{
            it.setOnClickListener {
            if (login_input.editText!!.hasFocus()) {
                login_input.editText!!.clearFocus()
            }
        }
            start_btn.setOnClickListener {
                val text = login_input.editText?.text.toString().trim()
                if (text.isNotEmpty()) {
                    val gameScreenIntent = Intent(this, GameMainScreen::class.java)
                    gameScreenIntent.putExtra("userName", text)
                    startActivity(gameScreenIntent)
                }
            }
            /*login_input.editText!!.onFocusChangeListener =
                View.OnFocusChangeListener { v, hasFocus ->
                    if (hasFocus) {
                        login_info_container.rotationX = 10f
                    } else {
                        login_info_container.rotationX = 0f
                    }
                }*/
        }
    }
}