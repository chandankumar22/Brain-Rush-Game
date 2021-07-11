package com.ck.dev.tiptap.ui.lastscreen

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.ck.dev.tiptap.R
import com.ck.dev.tiptap.helpers.AppConstants.ABOUT_SCREEN_TAG
import com.ck.dev.tiptap.helpers.AppConstants.SETTINGS_SCREEN_TAG
import com.ck.dev.tiptap.helpers.AppConstants.STATISTICS_SCREEN_TAG
import com.ck.dev.tiptap.ui.games.BaseFragment

class LastScreenActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_last_screen)
        with(intent.getStringExtra("screenName")) {
            this?.let {
                when (it) {
                    STATISTICS_SCREEN_TAG -> {
                        addFragment(GameStatisticsFragment.newInstance())
                    }
                    SETTINGS_SCREEN_TAG -> {
                        addFragment(GameSettingsFragment.newInstance())
                    }
                    ABOUT_SCREEN_TAG -> {
                        addFragment(GameStatisticsFragment.newInstance())
                    }
                }
            }
        }
    }

    private fun addFragment(fragment: BaseFragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fr_container, fragment)
            .commitAllowingStateLoss()
    }
}