package com.video.download.vidlink.Adapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.media.ThumbnailUtils
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.addsdemo.mysdk.utils.UtilsClass
import com.addsdemo.mysdk.utils.UtilsClass.startSpecialActivity
import com.video.download.vidlink.Activity.VideoShowActivity
import com.video.download.vidlink.Model.Model_Directory
import com.video.download.vidlink.R
import kotlin.jvm.java

class RecentVideoAdapter(
    private val activity: Context,
    public var videoPaths: List<Model_Directory>
) :
    RecyclerView.Adapter<RecentVideoAdapter.VideoViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoViewHolder {
        val view =
            LayoutInflater.from(activity).inflate(R.layout.recentvideo_adapter, parent, false)
        return VideoViewHolder(view)
    }

//    interface VideoItemClickListener {
//        fun onVideoDelete(position: Int)
//    }

    override fun onBindViewHolder(holder: VideoViewHolder, position: Int) {
        val videoPath = videoPaths[position]
        holder.videoTitle.text = videoPath.name
        holder.videoThumbnail.setImageBitmap(
            ThumbnailUtils.createVideoThumbnail(
                videoPaths[position].path,
                MediaStore.Video.Thumbnails.MINI_KIND
            )
        )

        holder.itemView.setOnClickListener {



            val intent = Intent(activity, VideoShowActivity::class.java).apply {
                putExtra("Video", videoPaths[position].path)
                putExtra("videoResolution", videoPaths[position].resolution)
            }
            startSpecialActivity(activity as Activity?, intent, false)
        }
    }

    override fun getItemCount(): Int = videoPaths.size

    class VideoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val videoThumbnail: ImageView = itemView.findViewById(R.id.videoThumbnail)
        val videoTitle: TextView = itemView.findViewById(R.id.videoTitle)
    }


//    private fun showCustomDialog(position: Int) {
//        val dialogView = LayoutInflater.from(activity).inflate(R.layout.view_dialog, null)
//        val tvCopy = dialogView.findViewById<TextView>(R.id.tvCopy)
//        val tvShare = dialogView.findViewById<TextView>(R.id.tvShare)
//        val tvDelete = dialogView.findViewById<TextView>(R.id.tvDelete)
//        val tvVideoPlay = dialogView.findViewById<TextView>(R.id.tvVideoPlay)
//
//        val dialog = AlertDialog.Builder(activity)
//            .setView(dialogView)
//            .setCancelable(true)
//            .create()
//
//        // todo Copy to Clipboard
//        tvCopy.setOnClickListener {
//            val clipboard = activity.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
//            val clip = ClipData.newPlainText("Copied Text", videoPaths[position].dis)
//            clipboard.setPrimaryClip(clip)
//            Toast.makeText(activity, "Copied to clipboard", Toast.LENGTH_SHORT).show()
//            dialog.dismiss()
//        }
//
//        // todo Share Text
//        tvShare.setOnClickListener {
//            val videoUri: Uri = videoPaths.get(position).path.toUri()
//            Utils.shareVideo(videoUri, activity)
//
//            dialog.dismiss()
//        }
//
//        // todo Delete (Just show a message here)
//        tvDelete.setOnClickListener {
//            AlertDialog.Builder(activity)
//                .setTitle("Delete Video")
//                .setMessage("Are you sure you want to delete this video?")
//                .setPositiveButton("Delete") { _, _ ->
//
//                    Log.e("checjjjjj", "Error: ${position}")
//                    listener.onVideoDelete(position)
//                    notifyDataSetChanged()
//                    notifyItemRemoved(position) // Refresh RecyclerView
//                }
//                .setNegativeButton("Cancel", null)
//                .show()
//            true
//            Toast.makeText(activity, "Deleted Successfully", Toast.LENGTH_SHORT).show()
//            dialog.dismiss()
//        }
//
//        // todo Cancel
//        tvVideoPlay.setOnClickListener {
//            val intent = Intent(activity, VideoShowActivity::class.java).apply {
//                putExtra("Video", videoPaths[position].path)
//                putExtra("videoResolution", videoPaths[position].resolution)
//            }
//            activity.startActivity(intent)
//            dialog.dismiss()
//        }
//
//        dialog.show()
//    }

}
