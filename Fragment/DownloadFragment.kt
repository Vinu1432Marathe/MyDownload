package com.video.download.vidlink.Fragment

import android.app.Activity
import android.app.AlertDialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.addsdemo.mysdk.utils.UtilsClass.startSpecialActivity
import com.video.download.vidlink.Activity.VideoShowActivity
import com.video.download.vidlink.Adapter.VideoAdapter
import com.video.download.vidlink.Model.Model_Directory
import com.video.download.vidlink.Other.SharePref
import com.video.download.vidlink.Other.Utils
import com.video.download.vidlink.R

class DownloadFragment : Fragment(), VideoAdapter.VideoItemClickListener {

    private lateinit var recyclerView: RecyclerView
    private lateinit var rlNoData: RelativeLayout
    private lateinit var adapter: VideoAdapter
    private var videoList: MutableList<Model_Directory> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_download, container, false)

        recyclerView = view.findViewById(R.id.recyclerView)
        rlNoData = view.findViewById(R.id.rlNoData)

        videoList = SharePref.getVideoList(requireContext()).toMutableList()
        adapter = VideoAdapter(requireContext(), videoList, this)

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        updateEmptyView()

        return view
    }

    private fun updateEmptyView() {
        rlNoData.visibility = if (videoList.isEmpty()) View.VISIBLE else View.GONE
        recyclerView.visibility = if (videoList.isEmpty()) View.GONE else View.VISIBLE
    }

    override fun onResume() {
        super.onResume()
        refreshData()
    }

    fun refreshData() {
        videoList = SharePref.getVideoList(requireContext()).toMutableList()
        if (::adapter.isInitialized) {
            adapter.updateList(videoList)
        }
        updateEmptyView()
    }

    override fun onVideoDelete(position: Int, list: ArrayList<Any>) {
        showCustomDialog(requireContext(), position)

    }


    private fun showCustomDialog(context: Context, position: Int) {
        val dialogView = LayoutInflater.from(activity).inflate(R.layout.view_dialog, null)
        val tvCopy = dialogView.findViewById<TextView>(R.id.tvCopy)
        val tvShare = dialogView.findViewById<TextView>(R.id.tvShare)
        val tvDelete = dialogView.findViewById<TextView>(R.id.tvDelete)
        val tvVideoPlay = dialogView.findViewById<TextView>(R.id.tvVideoPlay)

        val dialog = AlertDialog.Builder(activity, R.style.ExitDialog)
            .setView(dialogView)
            .setCancelable(true)
            .create()

        // todo Copy to Clipboard
        tvCopy.setOnClickListener {
            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("Copied Text", videoList[position].dis)
            clipboard.setPrimaryClip(clip)
            Toast.makeText(
                activity,
                getString(R.string.copied_to_clipboard), Toast.LENGTH_SHORT
            ).show()
            dialog.dismiss()
        }

        // todo Share Text
        tvShare.setOnClickListener {
            val videoUri: Uri = videoList.get(position).path.toUri()
            Utils.shareVideo(videoUri, context)

            dialog.dismiss()
        }

        // todo Delete (Just show a message here)
        tvDelete.setOnClickListener {
            AlertDialog.Builder(context)
                .setTitle(getString(R.string.delete_video))
                .setMessage(getString(R.string.are_you_sure_you_want_to_delete_this_video))
                .setPositiveButton(getString(R.string.delete)) { _, _ ->

                    SharePref.removeVideoAt(position, requireContext())
                    refreshData()
                    Toast.makeText(
                        activity,
                        getString(R.string.deleted_successfully), Toast.LENGTH_SHORT
                    ).show()

                }
                .setNegativeButton(getString(R.string.cancel), null)
                .show()
            true
            dialog.dismiss()
        }

        // todo Cancel
        tvVideoPlay.setOnClickListener {
            val intent = Intent(activity, VideoShowActivity::class.java).apply {
                putExtra("Video", videoList[position].path)
                putExtra("videoResolution", videoList[position].resolution)
            }
            startSpecialActivity(activity, intent, false)
            dialog.dismiss()
        }

        dialog.show()
    }
}
