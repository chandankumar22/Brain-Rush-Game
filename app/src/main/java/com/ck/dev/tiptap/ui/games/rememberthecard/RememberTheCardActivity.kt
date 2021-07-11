package com.ck.dev.tiptap.ui.games.rememberthecard

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import com.ck.dev.tiptap.R
import com.ck.dev.tiptap.extensions.fetchColor
import kotlinx.android.synthetic.main.activity_find_the_numbers.*
import timber.log.Timber

class RememberTheCardActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        Timber.i("onCreate called")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_remember_the_card)
        header.setBackgroundColor(fetchColor(R.color.primaryLightColor))
        setStartDestination()
    }

    private fun setStartDestination(){
        Timber.i("setStartDestination called")
        val navHostFragment = supportFragmentManager
                .findFragmentById(R.id.rem_the_card_nav_host_fragment) as NavHostFragment
        val graphInflater = navHostFragment.navController.navInflater
        val navGraph = graphInflater.inflate(R.navigation.remember_the_card_navigation)
        val navController = navHostFragment.navController
        val gameMode = intent.getStringExtra("gameMode")
        navGraph.startDestination = R.id.rememberTheCardGameLevelsFragment
        navController.setGraph( navGraph,Bundle().also {
            it.putString("gameName",gameMode)
        })
    }
}