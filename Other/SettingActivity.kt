package com.video.download.vidlink.Other

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.addsdemo.mysdk.utils.UtilsClass.startSpecialActivity
import com.video.download.vidlink.Activity.GuideActivity
import com.video.download.vidlink.Activity.PremiumActivity
import com.video.download.vidlink.Language.LanguageActivity
import com.video.download.vidlink.R

class SettingActivity : AppCompatActivity() {

    lateinit var cd_HowTo: CardView
    lateinit var cd_Language: CardView
    lateinit var cd_Share: CardView
    lateinit var cd_Rate: CardView
    lateinit var cd_Privacy: CardView
//    lateinit var cd_About: CardView
    lateinit var rl_Prime: RelativeLayout
    lateinit var txtHeader: TextView

    override fun attachBaseContext(newBase: Context) {
        val langCode = PreferencesHelper11(newBase).selectedLanguage ?: "en"
        super.attachBaseContext(LocaleHelper.setLocale(newBase, langCode))
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_setting)


        cd_HowTo = findViewById(R.id.cd_HowTo)
        cd_Language = findViewById(R.id.cd_Language)
        cd_Share = findViewById(R.id.cd_Share)
        cd_Rate = findViewById(R.id.cd_Rate)
        cd_Privacy = findViewById(R.id.cd_Privacy)
//        cd_About = findViewById(R.id.cd_About)
        rl_Prime = findViewById(R.id.rl_Prime)
        txtHeader = findViewById(R.id.txtHeader)


        Utils.TextColor(txtHeader)

        rl_Prime.setOnClickListener {
            val intent = Intent(this, PremiumActivity::class.java)
            startActivity(intent)
        }

        cd_HowTo.setOnClickListener {
//            startSpecialActivity(this, Intent(this, GuideActivity::class.java), false)

            val intent = Intent(this, GuideActivity::class.java)
            startActivity(intent)
        }
        cd_Language.setOnClickListener {

//            startSpecialActivity(this, Intent(this, LanguageActivity::class.java), false)


            val intent = Intent(this, LanguageActivity::class.java)
            startActivity(intent)
        }
        cd_Share.setOnClickListener {

            Utils.shareApp(this)

        }
        cd_Rate.setOnClickListener {

            Utils.rateUs(this)

        }
        cd_Privacy.setOnClickListener {
            Utils.openPrivacy(this)

        }
//        cd_About.setOnClickListener {
//
//        }

    }
}