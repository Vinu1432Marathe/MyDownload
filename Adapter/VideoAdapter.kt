package com.video.download.vidlink.Adapter

import android.app.Activity
import android.app.AlertDialog
import android.content.ClipData

import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.media.ThumbnailUtils
import android.net.Uri
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.addsdemo.mysdk.ADPrefrences.Ads_Listing_Adapter
import com.addsdemo.mysdk.utils.UtilsClass
import com.addsdemo.mysdk.utils.UtilsClass.startSpecialActivity
import com.video.download.vidlink.Activity.VideoShowActivity
import com.video.download.vidlink.Model.Model_Directory
import com.video.download.vidlink.Other.Utils
import com.video.download.vidlink.R

// VideoAdapter.kt
class VideoAdapter(
    private val activity: Context,
    private var videoPaths: List<Model_Directory>,
    private val listener: VideoItemClickListener
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val mergedList = ArrayList<Any>()
    private val TYPE_NORMAL = 0
    private val TYPE_AD = 1
    private val SHOW_AD = "SHOW_AD"
    private var nextAdPosition = 3

    init {
        mergeDatatoList(videoPaths)
    }

    fun updateList(newList: List<Model_Directory>) {
        videoPaths = newList
        mergedList.clear()
        nextAdPosition = 3
        mergeDatatoList(videoPaths)
        notifyDataSetChanged()
    }

    private fun mergeDatatoList(newItems: List<Model_Directory>) {
        for (item in newItems) {
            mergedList.add(item)
            if (mergedList.size == nextAdPosition) {
                mergedList.add(SHOW_AD)
                nextAdPosition += 6
            }
        }
    }

    interface VideoItemClickListener {
        fun onVideoDelete(position: Int, list: ArrayList<Any>)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == TYPE_AD) {
            val view =
                LayoutInflater.from(parent.context).inflate(R.layout.ad_layout, parent, false)
            AdViewHolder(view)
        } else {
            val view = LayoutInflater.from(activity).inflate(R.layout.video_adapter, parent, false)
            VideoViewHolder(view)
        }
    }

    override fun getItemCount(): Int = mergedList.size

    override fun getItemViewType(position: Int): Int {
        return if (mergedList[position] is String) TYPE_AD else TYPE_NORMAL
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is VideoViewHolder) {
            val item = mergedList[position] as Model_Directory
            holder.bind(item)
            holder.imgMore.setOnClickListener {
                listener.onVideoDelete(position, mergedList)
            }
        } else if (holder is AdViewHolder) {
            // Ad rendering logic here
            if (UtilsClass.remoteConfigModel != null) {
                val adHolder = holder as AdViewHolder
                adHolder.llline_full.layoutParams.height = 300
                Ads_Listing_Adapter.NativeFull_Show(
                    activity,
                    adHolder.llnative_full,
                    adHolder.llline_full,
                    "small",
                    position
                )
            }
        }
    }

    inner class VideoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val videoTitle: TextView = itemView.findViewById(R.id.videoTitle)
        val videoDis: TextView = itemView.findViewById(R.id.videoDis)
        val videoThumbnail: ImageView = itemView.findViewById(R.id.videoThumbnail)
        val imgMore: ImageView = itemView.findViewById(R.id.imgMore)

        fun bind(item: Model_Directory) {
            videoTitle.text = item.name
            videoDis.text = item.dis
            videoThumbnail.setImageBitmap(
                ThumbnailUtils.createVideoThumbnail(
                    item.path,
                    MediaStore.Video.Thumbnails.MINI_KIND
                )

            )

            itemView.setOnClickListener {
                val intent = Intent(activity, VideoShowActivity::class.java).apply {
                    putExtra("Video", item.path)
                    putExtra("videoResolution", item.resolution)
                }
                startSpecialActivity(activity as Activity?, intent, false)
            }

//            imgMore.setOnClickListener {
//                showCustomDialog(position)
//            }
        }
    }

    inner class AdViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var llline_full: LinearLayout = itemView.findViewById(R.id.llline_full)
        var llnative_full: LinearLayout = itemView.findViewById(R.id.llnative_full)
    }



}