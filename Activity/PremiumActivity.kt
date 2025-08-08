package com.video.download.vidlink.Activity

import android.content.Context
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.video.download.vidlink.Other.LocaleHelper
import com.video.download.vidlink.Other.PreferencesHelper11
import com.video.download.vidlink.Other.Utils
import com.video.download.vidlink.R

class PremiumActivity : AppCompatActivity() {

    lateinit var txtHeader : TextView
    override fun attachBaseContext(newBase: Context) {
        val langCode = PreferencesHelper11(newBase).selectedLanguage ?: "en"
        super.attachBaseContext(LocaleHelper.setLocale(newBase, langCode))
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_premium)
        txtHeader = findViewById(R.id.txtHeader)


        Utils.TextColor(txtHeader)
    }
}