package com.ck.dev.tiptap.ui.games.jumbledwords

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.ck.dev.tiptap.R
import com.ck.dev.tiptap.extensions.fetchColor
import kotlinx.android.synthetic.main.activity_jumbled_words.*

class JumbledWordsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_jumbled_words)
        header.setBackgroundColor(fetchColor(R.color.primaryLightColor))
    }
}