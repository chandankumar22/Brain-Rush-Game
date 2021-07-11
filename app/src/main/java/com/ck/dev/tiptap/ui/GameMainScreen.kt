package com.ck.dev.tiptap.ui

import android.media.MediaPlayer
import android.os.Bundle
import android.util.Base64
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import com.bumptech.glide.Glide
import com.ck.dev.tiptap.R
import com.ck.dev.tiptap.adapters.GameChangerAdapter
import com.ck.dev.tiptap.helpers.AppConstants
import com.ck.dev.tiptap.helpers.SharedPreferenceHelper
import com.ck.dev.tiptap.sounds.GameSound
import com.ck.dev.tiptap.sounds.GameSound.loadSound
import com.ck.dev.tiptap.sounds.GameSound.playGameBgSound
import kotlinx.android.synthetic.main.activity_game_main_screen.*
import kotlinx.android.synthetic.main.layout_header.view.*
import kotlinx.android.synthetic.main.profile_image.view.*
import timber.log.Timber
import kotlin.math.abs

class GameMainScreen : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        Timber.i("onCreate called")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game_main_screen)
        intent.getStringExtra("userName")?.let {
            main_screen_header.user_name.text = it
        }
        intent.getStringExtra("profilePic")?.let {
            val image = Base64.decode(it, Base64.DEFAULT)
            Glide.with(this).load(image).into(main_screen_header.profile_img.profile_pic_iv)
        }
        main_screen_header.header_coins.text = SharedPreferenceHelper.coins.toString()
        main_screen_header.user_game_rating.rating =
                SharedPreferenceHelper.currentUserRating.toFloat()
        val comp = CompositePageTransformer().also {
            it.addTransformer(MarginPageTransformer(40))
            it.addTransformer { page, position ->
                val r = 1 - abs(position)
                page.scaleY = 0.50f + r * 0.35f
            }
        }
        lifecycleScope.launchWhenCreated {
           // playGameBgSound()
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

        GameApp.hasCoinsUpdated.observe(this, Observer {
            main_screen_header.header_coins.text = SharedPreferenceHelper.coins.toString()
            main_screen_header.user_game_rating.rating =
                    SharedPreferenceHelper.currentUserRating.toFloat()

        })
        GameApp.hasNameOrAvtarUpdated.observe(this, {
            main_screen_header.user_name.text = SharedPreferenceHelper.userName
            val image = Base64.decode(SharedPreferenceHelper.profilePic, Base64.DEFAULT)
            Glide.with(this).load(image).into(main_screen_header.profile_img.profile_pic_iv)

        })
        /*AppConstants.darkModeChanged.observe(this, {
           *//* if(it){
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }else{
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            }*//*
        })*/
    }

}