package com.video.download.vidlink.Activity

import android.content.Context
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.addsdemo.mysdk.ADPrefrences.NativeAds_Class
import com.video.download.vidlink.Other.LocaleHelper
import com.video.download.vidlink.Other.PreferencesHelper11
import com.video.download.vidlink.Other.Utils
import com.video.download.vidlink.R

class GuideActivity : AppCompatActivity() {

    lateinit var imgBack : ImageView
    lateinit var txtHeader : TextView
    override fun attachBaseContext(newBase: Context) {
        val langCode = PreferencesHelper11(newBase).selectedLanguage ?: "en"
        super.attachBaseContext(LocaleHelper.setLocale(newBase, langCode))
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_guide)

        imgBack = findViewById(R.id.imgBack)
        txtHeader = findViewById(R.id.txtHeader)

        Utils.TextColor(txtHeader)

        imgBack.setOnClickListener {
            finish()
        }
        // todo Ads code ....
        val llline_full = findViewById<LinearLayout>(com.addsdemo.mysdk.R.id.llline_full)
        val llnative_full = findViewById<LinearLayout>(com.addsdemo.mysdk.R.id.llnative_full)
        NativeAds_Class.NativeFull_Show(this, llnative_full, llline_full, "large")


    }
}