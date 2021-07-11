package com.ck.dev.tiptap.ui.games.rememberthecard

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.ck.dev.tiptap.R
import com.ck.dev.tiptap.extensions.fetchColor
import kotlinx.android.synthetic.main.activity_find_the_numbers.*

class RememberTheCardActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_remember_the_card)
        header.setBackgroundColor(fetchColor(R.color.primaryLightColor))
    }
}