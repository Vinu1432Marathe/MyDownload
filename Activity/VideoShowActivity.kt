package com.video.download.vidlink.Activity

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.ui.PlayerView
import com.video.download.vidlink.Other.LocaleHelper
import com.video.download.vidlink.Other.PreferencesHelper11
import com.video.download.vidlink.R

class VideoShowActivity : AppCompatActivity() {

    private lateinit var player: SimpleExoPlayer
    private lateinit var videoView: PlayerView
    var videoPath: String? = null
    var videoResolution: String? = null
    lateinit var trackSelector: DefaultTrackSelector

    override fun attachBaseContext(newBase: Context) {
        val langCode = PreferencesHelper11(newBase).selectedLanguage ?: "en"
        super.attachBaseContext(LocaleHelper.setLocale(newBase, langCode))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_show)


        videoView = findViewById(R.id.playerView)
        videoPath = intent.getStringExtra("Video")
        videoResolution = intent.getStringExtra("videoResolution")

        Log.e("checkVideo","Resolution"+videoResolution)

        trackSelector = DefaultTrackSelector(this)

        codeResolution()

        player = SimpleExoPlayer.Builder(this)
            .setTrackSelector(trackSelector)
            .build()

        videoView.player = player

        // Load video
        val videoUri = Uri.parse(videoPath)
        val mediaItem = MediaItem.fromUri(videoUri)
        player.setMediaItem(mediaItem)
        player.prepare()
        player.play()
    }

    override fun onDestroy() {
        super.onDestroy()
        player.release()
    }

    // 🔹 Function to Set Resolution
    private fun codeResolution() {
        val resolutionMap = mapOf(
            "4" to Pair(720, 480),
            "3" to Pair(1280, 720),
            "2" to Pair(1920, 1080),
            "1" to Pair(2560, 1440),
            "0" to Pair(3840, 2160)
        )

        resolutionMap[videoResolution]?.let { (width, height) ->
            setResolution(width, height)
        } ?: run {
            Log.e("Resolution", "Invalid resolution selected. Using default.")
        }
    }

    // 🔹 Function to Apply Resolution to ExoPlayer
    private fun setResolution(width: Int, height: Int) {
        val parameters = trackSelector.buildUponParameters()
            .setMaxVideoSize(width, height)
            .build()
        trackSelector.setParameters(parameters)
        Log.e("Resolution", "Resolution set to ${width}x$height")
    }

}