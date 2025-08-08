package com.video.download.vidlink.Activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import com.video.download.vidlink.Language.LanguageActivity
import com.video.download.vidlink.R


class SplashActivity : AppCompatActivity() {

    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

//        progressBar = findViewById(R.id.progressBar)
        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this, LanguageActivity::class.java))
            finish() // Optional: Closes the splash screen so the user can't go back to it
        }, 3000) // 3 seconds delay
    }
}