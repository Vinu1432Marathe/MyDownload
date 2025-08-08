package com.video.download.vidlink.Other

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.viewpager2.widget.ViewPager2
import com.addsdemo.mysdk.ADPrefrences.NativeAds_Class
import com.addsdemo.mysdk.utils.UtilsClass.startSpecialActivity
import com.video.download.vidlink.Activity.MainActivity
import com.video.download.vidlink.Adapter.SlideViewPagerAdapter
import com.video.download.vidlink.Model.Model_slide
import com.video.download.vidlink.Other.LocaleHelper.setLocale
import com.video.download.vidlink.R
import kotlin.jvm.java

class AppSlideActivity : AppCompatActivity() {

    lateinit var viewPager: ViewPager2

    lateinit var imgNext: ImageView
    lateinit var indicatorLayout: LinearLayout

    val lstSlide = mutableListOf<Model_slide?>()


    override fun attachBaseContext(newBase: Context) {
        val langCode = PreferencesHelper11(newBase).selectedLanguage ?: "en"
        super.attachBaseContext(LocaleHelper.setLocale(newBase, langCode))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_app_slide)

        viewPager = findViewById(R.id.viewPager)
//
        imgNext = findViewById(R.id.imgNext)
        indicatorLayout = findViewById(R.id.indicatorLayout)


        // todo Ads code ....
        val llline_full = findViewById<LinearLayout>(com.addsdemo.mysdk.R.id.llline_full)
        val llnative_full = findViewById<LinearLayout>(com.addsdemo.mysdk.R.id.llnative_full)
        NativeAds_Class.NativeFull_Show(this, llnative_full, llline_full, "large")



        lstSlide.add(
            Model_slide(
                R.drawable.show1,
                getString(R.string.no_login_secure_fast),
                getString(R.string.download_videos_securely_and_instantly_without_logging_in_fast_private_and_hassle_free),
                0
            )
        )
        lstSlide.add(
            Model_slide(
                R.drawable.show2,
                getString(R.string.one_click_video_download),
                getString(R.string.easily_download_any_video_with_just_one_click_paste_the_link_tap_download_and_enjoy_your_video_instantly),
                0
            )
        )
        lstSlide.add(
            Model_slide(
                R.drawable.show3,
                getString(R.string.effortless_video_save),
                getString(R.string.the_easiest_and_fastest_way_to_save_any_video_effortlessly_enjoy_smooth_hassle_free_downloads_anytime),
                0
            )
        )


        viewPager.adapter = SlideViewPagerAdapter(lstSlide as List<Model_slide>)

        setupIndicators()
        setCurrentIndicator(0)
//        TabLayoutMediator(tabDots, viewPager) { _, _ -> }.attach()

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                setCurrentIndicator(position)
            }
        })

        imgNext.setOnClickListener {
            val current = viewPager.currentItem
            if (current < lstSlide.size - 1) {
                viewPager.setCurrentItem(current + 1, true)

            }else{
                // Navigate to next activity
                navigateNext()
            }
        }

    }

    private fun setupIndicators() {
        val indicators = arrayOfNulls<ImageView>(lstSlide.size)
        for (i in indicators.indices) {
            indicators[i] = ImageView(this)
            val layoutParams = LinearLayout.LayoutParams(
                if (i == 0) 12.dpToPx() else 24.dpToPx(),
                if (i == 0) 12.dpToPx() else 8.dpToPx()
            )
            layoutParams.setMargins(8, 0, 8, 0)
            indicators[i]!!.layoutParams = layoutParams
            indicators[i]!!.setImageDrawable(
                ContextCompat.getDrawable(
                    this,
                    if (i == 0) R.drawable.active_dot else R.drawable.inactive_dot
                )
            )
            indicatorLayout.addView(indicators[i])
        }
    }

    private fun setCurrentIndicator(index: Int) {
        val childCount = indicatorLayout.childCount
        for (i in 0 until childCount) {
            val imageView = indicatorLayout.getChildAt(i) as ImageView
            val layoutParams = LinearLayout.LayoutParams(
                if (i == index) 8.dpToPx() else 15.dpToPx(),
                if (i == index) 8.dpToPx() else 6.dpToPx()
            )
            layoutParams.setMargins(5, 0, 5, 0)
            imageView.layoutParams = layoutParams

            imageView.setImageDrawable(
                ContextCompat.getDrawable(
                    this,
                    if (i == index) R.drawable.active_dot else R.drawable.inactive_dot
                )
            )

            imageView.animate().scaleX(1f).scaleY(1f).setDuration(300).start()
        }
    }

    fun Int.dpToPx(): Int {
        return (this * Resources.getSystem().displayMetrics.density).toInt()
    }
    fun navigateNext() {
        if (!NotificationPermissionHelper.isNotificationPermissionGranted(this)) {
            NotificationPermissionHelper.requestNotificationPermission(this)
        } else {
            goToMain()

        }
    }

    private fun goToMain() {
        SharePref.setOnboarding(this, true)

        startSpecialActivity(this, Intent(this, MainActivity::class.java), false)
//        val intent = Intent(this, MainActivity::class.java)
//        startActivity(intent)
        finish() // Optional: if you want to finish the current activity
    }
    // ✅ Handle result from permission request
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == NotificationPermissionHelper.NOTIFICATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                goToMain()
            } else {
                Toast.makeText(this, "Notification permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

}