package com.ck.dev.tiptap.ui.games.jumbledwords

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import com.ck.dev.tiptap.R
import com.ck.dev.tiptap.extensions.fetchColor
import com.ck.dev.tiptap.helpers.GameConstants
import kotlinx.android.synthetic.main.activity_jumbled_words.*
import timber.log.Timber

class JumbledWordsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        Timber.i("onCreate called")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_jumbled_words)
        header.setBackgroundColor(fetchColor(R.color.primaryLightColor))
        setStartDestination()
    }

    private fun setStartDestination(){
        Timber.i("setStartDestination called")
        val navHostFragment = supportFragmentManager
                .findFragmentById(R.id.jumbled_words_nav_host_fragment) as NavHostFragment
        val graphInflater = navHostFragment.navController.navInflater
        val navGraph = graphInflater.inflate(R.navigation.jumbled_words_navigation)
        val navController = navHostFragment.navController
        val gameMode = intent.getStringExtra("gameMode")
        navGraph.startDestination = R.id.jumbledWordsLevelsFragment
        navController.setGraph( navGraph,Bundle().also {
            it.putString("gameMode",gameMode)
        })
    }
}