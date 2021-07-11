package com.ck.dev.tiptap.ui

import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.ck.dev.tiptap.R
import com.ck.dev.tiptap.adapters.MainScreenAvtarAdapter
import com.ck.dev.tiptap.extensions.fetchDrawable
import kotlinx.android.synthetic.main.activity_main.*
import timber.log.Timber

class MainActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        Timber.i("onCreate called")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val listOfAvtarDrawables = getAvtarDrawableList()
        avatar_rv.adapter = MainScreenAvtarAdapter(listOfAvtarDrawables)
        avatar_rv.setHasFixedSize(true)
        avatar_rv.layoutManager = LinearLayoutManager(this)

        setListeners()
    }

    private fun getAvtarDrawableList(): ArrayList<Drawable> {
        Timber.i("getAvtarDrawableList called")
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
        Timber.i("setListeners called")
        root?.let {
            it.setOnClickListener {
                Timber.i("root.setOnClickListener is called")
                if (login_input.editText!!.hasFocus()) {
                    login_input.editText!!.clearFocus()
                }
            }
            start_btn.setOnClickListener {
                Timber.i("start_btn.setOnClickListener is called")
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