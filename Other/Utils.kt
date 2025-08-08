package com.video.download.vidlink.Other

import android.Manifest
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Shader
import android.media.MediaScannerConnection
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Environment
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresPermission
import com.addsdemo.mysdk.ADPrefrences.MyApp
import com.video.download.vidlink.Language.Model_Language
import com.video.download.vidlink.R
import java.io.File
import java.io.FileOutputStream
import java.io.IOException


object Utils {
    const val API_URL: String = "https://api.alldownloader.app/v1/external/"

    @RequiresPermission(Manifest.permission.ACCESS_NETWORK_STATE)
    fun isInternetAvailable(activity: Context): Boolean {
        val connectivityManager =
            activity.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (connectivityManager != null) {
            val networkInfo = connectivityManager.activeNetworkInfo
            return networkInfo != null && networkInfo.isConnected
        }
        return false
    }


    fun saveVideo(videoData: ByteArray?, context: Context?): String? {
        val directory = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES),
            "MyVideos"
        )
        if (!directory.exists()) {
            directory.mkdirs()
        }

        val fileName = "video_" + System.currentTimeMillis() + ".mp4"
        val videoFile = File(directory, fileName)

        try {
            FileOutputStream(videoFile).use { fos ->
                fos.write(videoData)
                fos.flush()

                MediaScannerConnection.scanFile(
                    context, arrayOf(videoFile.absolutePath), arrayOf("video/mp4")
                ) { path: String, uri: Uri? ->
                    Log.d(
                        "Gallery",
                        "Video added: $path"
                    )
                }
                return videoFile.absolutePath
            }
        } catch (e: IOException) {
            Log.e("TAG", "Error saving video: " + e.message)
            return null
        }
    }

    fun shareVideo(videoUri: Uri, context: Context) {
        context.startActivity(Intent.createChooser(Intent(Intent.ACTION_SEND).apply {
            type = "video/*"
            putExtra(Intent.EXTRA_STREAM, videoUri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }, "Share Video Via"))
    }

    fun shareApp(context: Context) {
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.type = "text/plain"
        shareIntent.putExtra(
            Intent.EXTRA_TEXT,
            "Hey, check out this awesome app! ${
                context.packageManager.getPackageInfo(
                    context.packageName,
                    0
                ).applicationInfo?.loadLabel(context.packageManager)
            } https://play.google.com/store/apps/details?id=${context.packageName}"
        )
        context.startActivity(Intent.createChooser(shareIntent, "Share via"))
    }

    fun rateUs(context: Context) {
        val packageName = context.packageName
        val uri = Uri.parse("https://play.google.com/store/apps/details?id=$packageName")
        val intent = Intent(Intent.ACTION_VIEW, uri)
        try {
            context.startActivity(intent)
        } catch (e: Exception) {
            context.startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/apps/details?id=$packageName")
                )
            )
        }
    }


    fun openPrivacy(context: Context) {

        val intent = Intent(Intent.ACTION_VIEW)
        val configPref = MyApp.ad_preferences.getRemoteConfig()

        if (configPref?.privacyPolicy?.isNotEmpty() == true) {

            intent.data = Uri.parse(configPref.privacyPolicy)
            try {
                context.startActivity(intent)
            } catch (e: Exception) {
            }
        } else {
            Toast.makeText(context, "Unable to load!", Toast.LENGTH_SHORT).show()
        }


    }

    fun TextColor(textView: TextView){

        val text = textView.text.toString()

        val paint = textView.paint
        val width = paint.measureText(text)

// Create a left-to-right horizontal gradient
        val shader = LinearGradient(
            0f, 0f, width, textView.textSize,
            intArrayOf(
                Color.parseColor("#00C6FF"), // Blue-ish
                Color.parseColor("#0072FF"), // Slightly deeper blue
                Color.parseColor("#8E2DE2"), // Purple
                Color.parseColor("#FF00D4")  // Pink
            ),
            null,
            Shader.TileMode.CLAMP
        )

        textView.paint.shader = shader
    }

    val languages_list = listOf(
        Model_Language("English(US)", "en", R.drawable.us),
        Model_Language("English(UK)", "en", R.drawable.united_kingdom),
        Model_Language("Hindi", "hi", R.drawable.hindi),
        Model_Language("Spanish", "es", R.drawable.spain),
        Model_Language("French", "fr", R.drawable.france),
        Model_Language("German", "de", R.drawable.germany),
        Model_Language("Portuguese", "pt", R.drawable.portuguese),
        Model_Language("Arabic", "ar", R.drawable.arabic),
        Model_Language("Russian", "ru", R.drawable.russian),
        Model_Language("Turkish", "tr", R.drawable.turkish),
    )

}
