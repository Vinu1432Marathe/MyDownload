package com.video.download.vidlink.Activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.addsdemo.mysdk.ADPrefrences.Ads_Interstitial
import com.addsdemo.mysdk.ADPrefrences.Ads_Interstitial.remoteConfigModel
import com.addsdemo.mysdk.ADPrefrences.MyApp
import com.addsdemo.mysdk.model.RemoteConfigModel
import com.addsdemo.mysdk.utils.CustomTabLinkOpen
import com.addsdemo.mysdk.utils.UtilsClass

open class BaseActivity : AppCompatActivity() {

    companion object {
//        val remoteConfigModel: RemoteConfigModel? = MyApp.ad_preferences.getRemoteConfig()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    @SuppressLint("MissingSuperCall")
    override fun onBackPressed() {
        Ads_Interstitial.BackshowAds_full(this, object : Ads_Interstitial.OnFinishAds {
            override fun onFinishAds(b: Boolean) {
                finish()
                Log.d("TAG", "onFinishAds5424: "+b+" "+remoteConfigModel?.isOnAdRedirect )
                if (b && remoteConfigModel?.isOnAdRedirect == true) {
                    CustomTabLinkOpen.openLink(
                        this@BaseActivity,
                        UtilsClass.getRandomRedirectLink(MyApp.ad_preferences.getRemoteConfig()!!.customLinks!!.interRedirectLink),
                        "inter_click"
                    )
                }
            }
        })
    }

}
