package com.video.download.vidlink.Activity

import android.app.Activity
import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.core.content.ContextCompat
import androidx.viewpager2.widget.ViewPager2
import com.addsdemo.mysdk.utils.UtilsClass.startSpecialActivity
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.video.download.vidlink.Adapter.ViewPagerAdapter
import com.video.download.vidlink.Other.LocaleHelper
import com.video.download.vidlink.Other.PreferencesHelper
import com.video.download.vidlink.Other.PreferencesHelper11
import com.video.download.vidlink.Other.SettingActivity
import com.video.download.vidlink.Other.SharePref
import com.video.download.vidlink.Other.Utils
import com.video.download.vidlink.R

class MainActivity : BaseActivity() {

    private lateinit var imgPre: ImageView
    private lateinit var viewPager: ViewPager2
    private lateinit var tabLayout: TabLayout
    private lateinit var adapter: ViewPagerAdapter
    private lateinit var imgMenu: ImageView
    private lateinit var txtHeader: TextView



    private var hasRated = false
    override fun attachBaseContext(newBase: Context) {
        val langCode = PreferencesHelper11(newBase).selectedLanguage ?: "en"
        super.attachBaseContext(LocaleHelper.setLocale(newBase, langCode))
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        viewPager = findViewById(R.id.viewPager)
        tabLayout = findViewById(R.id.tabLayout)
        imgMenu = findViewById(R.id.imgMenu)
        imgPre = findViewById(R.id.imgPre)
        txtHeader = findViewById(R.id.txtHeader)


        Utils.TextColor(txtHeader)

        hasRated = SharePref.isRate(this)

        imgPre.setOnClickListener {
            val intent = Intent(this, PremiumActivity::class.java)
            startActivity(intent)
        }

        setupViewPager()

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.customView = getTabView(position, position == 0)
        }.attach()

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                updateTabStyle(tab, true)
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {
                updateTabStyle(tab, false)
            }

            override fun onTabReselected(tab: TabLayout.Tab) {}
        })

        imgMenu.setOnClickListener {

            val intent = Intent(this, SettingActivity::class.java)
            startActivity(intent)

        }

    }

    private fun getTabView(position: Int, isSelected: Boolean): View {
        val view = LayoutInflater.from(this).inflate(R.layout.tab_layout, null)
        val text = view.findViewById<TextView>(R.id.tabText)
        val icon = view.findViewById<ImageView>(R.id.tab_Icon)

        when (position) {
            0 -> {
                text.text = getString(R.string.home)
                icon.setImageResource(R.drawable.home)
            }

            1 -> {
                text.text = getString(R.string.download)
                icon.setImageResource(R.drawable.download)
            }
        }
        updateTabColors(view, isSelected)
        return view
    }

    private fun updateTabStyle(tab: TabLayout.Tab, isSelected: Boolean) {
        tab.customView?.let { updateTabColors(it, isSelected) }
    }

    private fun updateTabColors(view: View, isSelected: Boolean) {
        val layout = view.findViewById<LinearLayout>(R.id.tabLayoutContainer)
        val text = view.findViewById<TextView>(R.id.tabText)
        val icon = view.findViewById<ImageView>(R.id.tab_Icon)

        if (isSelected) {
            layout.setBackgroundResource(R.drawable.bottom_bg)
            text.setTextColor(Color.WHITE)
            icon.setColorFilter(Color.WHITE)
        } else {
            layout.setBackgroundColor(Color.WHITE)
            text.setTextColor(Color.parseColor("#6B7280"))
            icon.setColorFilter(Color.parseColor("#6B7280"))
        }
    }

    private fun setupViewPager() {
        adapter = ViewPagerAdapter(this)
        viewPager.adapter = adapter
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun onBackPressed() {


        Log.e("checkBoolen", "CheckBAck :: $hasRated")
//        if (SharePref.isRate(this)) {
        if (hasRated) {
            ExitDialog(this)
        } else {
            RateDialog()
        }
    }


    private fun ExitDialog(context: Context) {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.exit_dialog, null)

        val downloadDialog = AlertDialog.Builder(context)
            .setView(dialogView)
            .setCancelable(true)
            .create()


        val txtYes = dialogView.findViewById<TextView>(R.id.txtExit)
        val txtCancel = dialogView.findViewById<TextView>(R.id.txtCancel)

        txtCancel.setOnClickListener {
            hasRated = true
            downloadDialog.dismiss() }
        txtYes.setOnClickListener {
            downloadDialog.dismiss()
            if (context is Activity) {
                context.finishAffinity()
            }
        }

        downloadDialog.show()
    }

    private fun RateDialog() {
        val dialogView = layoutInflater.inflate(R.layout.rate_dialog, null)
        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(true)
            .create()


        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val star1 = dialogView.findViewById<ImageView>(R.id.star1)
        val star2 = dialogView.findViewById<ImageView>(R.id.star2)
        val star3 = dialogView.findViewById<ImageView>(R.id.star3)
        val star4 = dialogView.findViewById<ImageView>(R.id.star4)
        val star5 = dialogView.findViewById<ImageView>(R.id.star5)
        val txtYes = dialogView.findViewById<TextView>(R.id.txtYes)

        star1.setOnClickListener {

            star1.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.star_select))
            star2.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.star_unselect))
            star3.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.star_unselect))
            star4.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.star_unselect))
            star5.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.star_unselect))
            FeebBack()
            dialog.dismiss()
        }
        star2.setOnClickListener {

            star1.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.star_select))
            star2.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.star_select))
            star3.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.star_unselect))
            star4.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.star_unselect))
            star5.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.star_unselect))
            FeebBack()
            dialog.dismiss()
        }
        star3.setOnClickListener {

            star1.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.star_select))
            star2.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.star_select))
            star3.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.star_select))
            star4.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.star_unselect))
            star5.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.star_unselect))
            FeebBack()
            dialog.dismiss()
        }
        star4.setOnClickListener {

            star1.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.star_select))
            star2.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.star_select))
            star3.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.star_select))
            star4.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.star_select))
            star5.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.star_unselect))
            Utils.rateUs(this)
            hasRated = true
            SharePref.setRate(this, true)
            dialog.dismiss()

        }
        star5.setOnClickListener {

            star1.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.star_select))
            star2.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.star_select))
            star3.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.star_select))
            star4.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.star_select))
            star5.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.star_select))
            Utils.rateUs(this)
            hasRated = true
            SharePref.setRate(this, true)
            dialog.dismiss()

        }

        txtYes.setOnClickListener {  hasRated = true
            dialog.dismiss() }

        dialog.show()
    }

    fun FeebBack() {
        SharePref.setRate(this, true)
        hasRated = true
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "message/rfc822"
            setPackage("com.google.android.gm")
            putExtra(Intent.EXTRA_EMAIL, arrayOf("semicoloneclipse02@gmail.com"))
            putExtra(Intent.EXTRA_SUBJECT, "Subject")
            putExtra(Intent.EXTRA_TEXT, "Body here...")
        }

        try {
            startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(this, getString(R.string.gmail_app_is_not_installed), Toast.LENGTH_SHORT)
                .show()
        }
    }

}

