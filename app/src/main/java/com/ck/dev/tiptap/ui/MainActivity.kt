package com.ck.dev.tiptap.ui

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Base64
import androidx.core.graphics.drawable.toBitmap
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.ck.dev.tiptap.R
import com.ck.dev.tiptap.adapters.MainScreenAvtarAdapter
import com.ck.dev.tiptap.extensions.fetchDrawable
import com.ck.dev.tiptap.helpers.SharedPreferenceHelper
import com.ck.dev.tiptap.models.RememberTheCardData
import com.ck.dev.tiptap.ui.custom.OnItemClick
import kotlinx.android.synthetic.main.activity_main.*
import timber.log.Timber
import java.io.ByteArrayOutputStream

class MainActivity : BaseActivity() {

    private lateinit var listForCurrentScreen: java.util.ArrayList<Drawable>
    private var currentPicSelPos = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        Timber.i("onCreate called")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if(SharedPreferenceHelper.isLoggedIn){
            val gameScreenIntent = Intent(this, GameMainScreen::class.java)
            viewModel.profilePic = SharedPreferenceHelper.profilePic!!
            viewModel.name = SharedPreferenceHelper.userName!!
            gameScreenIntent.putExtra("userName",  SharedPreferenceHelper.userName)
            gameScreenIntent.putExtra("profilePic", SharedPreferenceHelper.profilePic)
            startActivity(gameScreenIntent)
        }else{
            val listOfAvtarDrawables = getAvtarDrawableList()
            Glide.with(this@MainActivity).load(listForCurrentScreen[currentPicSelPos]).into(profile_pic_iv)
            avatar_rv.adapter = MainScreenAvtarAdapter(listOfAvtarDrawables, object : OnItemClick {
                override fun onPicSelected(position: Int) {
                    currentPicSelPos = position
                    Glide.with(this@MainActivity).load(listForCurrentScreen[position]).into(profile_pic_iv)
                }

                override fun onPicSelectedEndless(
                    rememberTheCardData: RememberTheCardData,
                    position: Int
                ) {}

            })
            avatar_rv.setHasFixedSize(true)
            avatar_rv.layoutManager = LinearLayoutManager(this)
        }
        setListeners()
    }

    private fun getAvtarDrawableList(): ArrayList<Drawable> {
        Timber.i("getAvtarDrawableList called")
        listForCurrentScreen = ArrayList<Drawable>()
        with(listForCurrentScreen) {
            add(fetchDrawable(R.drawable.ic_avtar_boy_1))
            add(fetchDrawable(R.drawable.ic_avtar_boy_2))
            add(fetchDrawable(R.drawable.ic_avtar_boy_3))
            add(fetchDrawable(R.drawable.ic_avtar_girl_2))
            add(fetchDrawable(R.drawable.ic_avtar_girl_3))
            add(fetchDrawable(R.drawable.ic_avtar_girl_1))
        }
        listForCurrentScreen.shuffled()
        return listForCurrentScreen
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
                    SharedPreferenceHelper.isLoggedIn = true
                    SharedPreferenceHelper.userName = text
                    val bos = ByteArrayOutputStream()
                    listForCurrentScreen[currentPicSelPos].toBitmap().compress(Bitmap.CompressFormat.PNG,100,bos)
                  val profilePic = Base64.encodeToString(bos.toByteArray(), Base64.DEFAULT)
                    SharedPreferenceHelper.profilePic = profilePic
                    viewModel.name = text
                    viewModel.profilePic = profilePic
                    gameScreenIntent.putExtra("profilePic", profilePic)
                    startActivity(gameScreenIntent)
                }
            }
        }
    }
}