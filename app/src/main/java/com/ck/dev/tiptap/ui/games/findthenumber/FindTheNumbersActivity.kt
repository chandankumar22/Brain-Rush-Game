package com.ck.dev.tiptap.ui.games.findthenumber

import android.os.Bundle
import androidx.navigation.fragment.NavHostFragment
import com.ck.dev.tiptap.R
import com.ck.dev.tiptap.extensions.fetchColor
import com.ck.dev.tiptap.helpers.GameConstants
import com.ck.dev.tiptap.ui.BaseActivity
import kotlinx.android.synthetic.main.activity_find_the_numbers.*
import timber.log.Timber

class FindTheNumbersActivity : BaseActivity() {

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
        val destination = if(gameMode == GameConstants.FIND_THE_NUMBER_GAME_NAME_TIME_BOUND){
            R.id.gameLevelsFragment
        }else{
            R.id.infinitePlayGameFragment
        }
        navGraph.startDestination = destination
        navController.graph = navGraph
    }

}