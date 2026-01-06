package com.example.grupo_pdm.ui

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.grupo_pdm.R
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val langBtn = findViewById<FloatingActionButton>(R.id.lang_btn)

        val iconRes = if (AppCompatDelegate.getApplicationLocales().toLanguageTags().startsWith("pt")) R.drawable.pt_flag_icon else R.drawable.eng_flag_icon
        langBtn.setImageResource(iconRes)

        langBtn.setOnClickListener {
            val currentTags = AppCompatDelegate.getApplicationLocales().toLanguageTags()
            val isPt = currentTags.startsWith("pt")


            val newTags = if (isPt) "en" else "pt-PT"
            AppCompatDelegate.setApplicationLocales(
                LocaleListCompat.forLanguageTags(newTags)
            )

        }


    }
}