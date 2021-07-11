package com.ck.dev.tiptap.ui.games.findthenumber

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import com.ck.dev.tiptap.R
import com.ck.dev.tiptap.extensions.fetchColor
import com.ck.dev.tiptap.extensions.fetchDrawable
import com.ck.dev.tiptap.helpers.GameConstants
import com.ck.dev.tiptap.ui.GameMainScreen
import kotlinx.android.synthetic.main.activity_find_the_numbers.*
import kotlinx.android.synthetic.main.activity_game_main_screen.*
import timber.log.Timber

class FindTheNumbersActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        Timber.i("onCreate called")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_find_the_numbers)
        header.setBackgroundColor(fetchColor(R.color.primaryLightColor))
        setStartDestination()
    }

    private fun setStartDestination(){
        val navHostFragment = supportFragmentManager
                .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val graphInflater = navHostFragment.navController.navInflater
        val navGraph = graphInflater.inflate(R.navigation.game_naavigation)
        val navController = navHostFragment.navController
        val gameMode = intent.getStringExtra("gameMode")
        val destination = if(gameMode == GameConstants.FIND_THE_NUMBER_GAME_NAME){
            R.id.gameLevelsFragment
        }else{
            R.id.infinitePlayGameFragment
        }
        navGraph.startDestination = destination
        navController.graph = navGraph
    }

}