package com.video.download.vidlink.Fragment

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat.startActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.addsdemo.mysdk.ADPrefrences.NativeAds_Class
import com.addsdemo.mysdk.utils.UtilsClass.startSpecialActivity
import com.video.download.vidlink.Activity.GuideActivity
import com.video.download.vidlink.Activity.PremiumActivity
import com.video.download.vidlink.Adapter.RecentVideoAdapter
import com.video.download.vidlink.Model.Model_Directory
import com.video.download.vidlink.Other.AppSlideActivity
import com.video.download.vidlink.Other.SharePref
import com.video.download.vidlink.Other.Utils
import com.video.download.vidlink.Other.Utils.isInternetAvailable
import com.video.download.vidlink.R
import kotlinx.coroutines.launch
import okhttp3.Call
import okhttp3.Callback
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okio.IOException
import org.json.JSONException
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.util.concurrent.Executors


class HomeFragment : Fragment() {
    var videoList: MutableList<Model_Directory> = mutableListOf()
    lateinit var recyclerView: RecyclerView

    private lateinit var adapter: RecentVideoAdapter

    private lateinit var rl_Prime: RelativeLayout
    private lateinit var ll_Recent: LinearLayout
    private lateinit var txtURL: EditText
    private lateinit var txtDownload: TextView
    private lateinit var txtSeeMore: TextView
    private lateinit var progressBar: FrameLayout
    private lateinit var imgTextClear: ImageView
    private var shareVideo: String? = null
    private var videoResolution: String? = null

    private lateinit var downloadDialog: Dialog
    private lateinit var progressBarDialog: ProgressBar
    private lateinit var percentageText: TextView
    private lateinit var txtAutoDetect: TextView
    private lateinit var timeLeftText: TextView
    private val handler = Handler(Looper.getMainLooper())

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_home, container, false)

        txtAutoDetect = view.findViewById(R.id.txtAutoDetect)
        txtDownload = view.findViewById(R.id.txtDownload)
        txtURL = view.findViewById(R.id.txtURL)
        imgTextClear = view.findViewById(R.id.imgTextClear)
        recyclerView = view.findViewById(R.id.rclRecentDownload)
        ll_Recent = view.findViewById(R.id.ll_Recent)
        progressBar = view.findViewById(R.id.loadingOverlay)
        txtSeeMore = view.findViewById(R.id.txtSeeMore)
        rl_Prime = view.findViewById(R.id.rl_Prime)
        downloadDialog = Dialog(requireContext())


        rl_Prime.setOnClickListener {
            val intent = Intent(requireContext(), PremiumActivity::class.java)
//            startSpecialActivity(requireActivity(), intent, false)
            startActivity(intent)
        }

        txtAutoDetect.setOnClickListener {
            // Access clipboard
            val clipboard =
                requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

            // Check if clipboard has data
            if (clipboard.hasPrimaryClip() && clipboard.primaryClipDescription?.hasMimeType("text/plain") == true) {
                val clipData = clipboard.primaryClip
                val pastedText = clipData?.getItemAt(0)?.text.toString()

                // Auto-paste into EditText
                txtURL.setText(pastedText)
            }
        }

        // Download Button Click Listener
        txtDownload.setOnClickListener @androidx.annotation.RequiresPermission(android.Manifest.permission.ACCESS_NETWORK_STATE) {

            if (!txtURL.text.toString().isEmpty()) {
                showSingleSelectionCheckBoxDialog()
            } else {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.first_paste_any_url), Toast.LENGTH_SHORT
                ).show()
            }

        }


        // Access clipboard
        val clipboard =
            requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

        // Check if clipboard has data
        if (clipboard.hasPrimaryClip() && clipboard.primaryClipDescription?.hasMimeType("text/plain") == true) {
            val clipData = clipboard.primaryClip
            val pastedText = clipData?.getItemAt(0)?.text.toString()

            // Auto-paste into EditText
            txtURL.setText(pastedText)
        }

        txtURL.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

                requireActivity().runOnUiThread {
                    imgTextClear.visibility = View.GONE
                }
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                requireActivity().runOnUiThread {
                    imgTextClear.visibility = View.VISIBLE
                }
            }
        })

        txtSeeMore.setOnClickListener {

            val intent = Intent(requireContext(), GuideActivity::class.java)
            startSpecialActivity(requireActivity(), intent, false)
        }

        imgTextClear.setOnClickListener {

            if (!txtURL.text.toString().isEmpty()) {
                txtURL.text.clear()
                imgTextClear.visibility = View.GONE
            } else {
                imgTextClear.visibility = View.VISIBLE
            }

        }
        setupRecyclerView()
        return view
    }

    private fun setupRecyclerView() {
        videoList = SharePref.getVideoList(requireContext()) // Load data
        val limitedList = if (videoList.size >= 2) videoList.takeLast(2) else videoList
        Log.e("CheckFinal", "Video List  ${videoList.size}")

        if (limitedList.isEmpty()) {
            ll_Recent.visibility = View.GONE
        } else {
            ll_Recent.visibility = View.VISIBLE
            adapter = RecentVideoAdapter(requireContext(), limitedList)
            recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
            recyclerView.adapter = adapter
            adapter.notifyDataSetChanged()
        }

    }

    fun refreshAllFragments() {
        for (fragment in parentFragmentManager.fragments) {
            parentFragmentManager.beginTransaction().detach(fragment).attach(fragment).commit()
        }
    }


    private fun fetchData(link: String) {

        val client = OkHttpClient()
        val requestBody = FormBody.Builder().add("link", link).build()
        val request = Request.Builder().url(Utils.API_URL)
            .header("Content-Type", "application/x-www-form-urlencoded").post(requestBody).build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Handler(Looper.getMainLooper()).post {
//                    progressBar.visibility = View.GONE
                    progressBar.visibility = View.GONE
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.this_video_not_download), Toast.LENGTH_SHORT
                    )
                        .show()
                    txtURL.text.clear()
                }
                Log.e("TAG", "Request failed: ${e.message}")
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    response.body?.string()?.let { responseData ->
                        try {
                            val json = JSONObject(responseData)
                            val formatted = json.getJSONObject("formatted")

                            val videoTitle = formatted.getString("title")
                            val videoCaption = formatted.getString("description")
                            val formatsArray = formatted.getJSONArray("formats")

                            for (i in 0 until formatsArray.length()) {
                                val formatObj = formatsArray.getJSONObject(i)
                                val videoUrl = formatObj.getString("url")

                                if (formatObj.getString("vbr") == "null" && formatObj.getString("abr") == "null" && formatObj.getString(
                                        "tbr"
                                    ) == "null"
                                ) {

                                    Log.d("CheckAPI", "Name : $videoTitle")
                                    Log.d("CheckAPI", "Caption : $videoCaption")
                                    Log.d("CheckAPI", "Download URL : $videoUrl")

                                    Handler(Looper.getMainLooper()).post {
                                        progressBar.visibility = View.GONE
                                        showDownloadDialog(requireContext(), videoTitle)
                                        downloadVideo(
                                            videoUrl,
                                            videoTitle,
                                            videoCaption,
                                            videoResolution.toString()
                                        )
                                    }
                                    break
                                }
                            }
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }
                    }
                } else {
                    txtURL.text.clear()
                    Handler(Looper.getMainLooper()).post {
//                        progressBar.visibility = View.GONE
                        downloadDialog.dismiss()
                        Toast.makeText(
                            requireContext(),
                            getString(R.string.this_video_not_download),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    Log.e("TAG", "Error: ${response.code}")
                }
            }

        })
    }


    private fun downloadVideo(
        videoUrl: String, videoName: String, videoDis: String, videoResolu: String) {


        val client = OkHttpClient()
        val request = Request.Builder().url(videoUrl).build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                handler.post {
                    downloadDialog.dismiss()
                    txtURL.text.clear()
                    Toast.makeText(
                        requireContext(), "Download failed: ${e.message}", Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                val body = response.body
                if (!response.isSuccessful || body == null) {
                    handler.post {
                        downloadDialog.dismiss()
                        txtURL.text.clear()
                        Toast.makeText(
                            requireContext(),
                            getString(R.string.download_failed),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    return
                }

                val totalSize = body.contentLength()
                val inputStream = body.byteStream()
                val outputStream = ByteArrayOutputStream()
                val buffer = ByteArray(8192)

                var downloaded = 0L
                var read: Int
                val startTime = System.currentTimeMillis()

                try {
                    while (inputStream.read(buffer).also { read = it } != -1) {
                        outputStream.write(buffer, 0, read)
                        downloaded += read

                        val progress = (100 * downloaded / totalSize).toInt()
                        val elapsed = (System.currentTimeMillis() - startTime) / 1000
                        val remainingTime =
                            ((100 - progress) * elapsed / (progress + 1)).coerceAtLeast(1)

                        handler.post {
                            progressBarDialog.progress = progress
                            percentageText.text = "$progress%"
                            timeLeftText.text = "$remainingTime Seconds Left"
                        }
                    }

                    val videoData = outputStream.toByteArray()
                    val savedPath = Utils.saveVideo(videoData, requireContext())

                    handler.post {
                        downloadDialog.dismiss()
                        if (savedPath != null) {
                            SharePref.saveVideoPath(
                                videoName, videoDis, savedPath, videoResolu, requireContext()
                            )
                            shareVideo = savedPath
                            txtURL.text.clear()
                            refreshAllFragments()
                            setupRecyclerView()
                            Toast.makeText(
                                requireContext(),
                                getString(R.string.video_saved),
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            Toast.makeText(
                                requireContext(),
                                getString(R.string.error_saving_video),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                } catch (e: Exception) {
                    handler.post {
                        downloadDialog.dismiss()
                        Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_SHORT)
                            .show()
                    }
                } finally {
                    inputStream.close()
                    outputStream.close()
                }
            }
        })
    }


    private fun showDownloadDialog(context: Context, fileName: String) {

        downloadDialog.setContentView(R.layout.dialog_download_progress)
        downloadDialog.setCancelable(false)

        val fileNameText = downloadDialog.findViewById<TextView>(R.id.fileNameTextView)
        progressBarDialog = downloadDialog.findViewById(R.id.progressBar)
        percentageText = downloadDialog.findViewById(R.id.percentageTextView)
        timeLeftText = downloadDialog.findViewById(R.id.timeLeftTextView)

//        // todo Ads code ....
        val llline_full =
            downloadDialog.findViewById<LinearLayout>(com.addsdemo.mysdk.R.id.llline_full)
        val llnative_full =
            downloadDialog.findViewById<LinearLayout>(com.addsdemo.mysdk.R.id.llnative_full)
        NativeAds_Class.NativeFull_Show(context, llnative_full, llline_full, "large")

        fileNameText.text = fileName
        progressBarDialog.progress = 0
        percentageText.text = "0%"
        timeLeftText.text = "10 Seconds Left"

        downloadDialog.show()
    }

    private fun showSingleSelectionCheckBoxDialog() {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.new_dialog, null)
        val dialog = AlertDialog.Builder(requireContext()).setView(dialogView).create()

        val cards = listOf(
            dialogView.findViewById<RelativeLayout>(R.id.card4k),
            dialogView.findViewById(R.id.card2k),
            dialogView.findViewById(R.id.card1080),
            dialogView.findViewById(R.id.card720),
            dialogView.findViewById(R.id.card480)
        )

        val checkBoxes = listOf(
            dialogView.findViewById<CheckBox>(R.id.checkBox1),
            dialogView.findViewById(R.id.checkBox2),
            dialogView.findViewById(R.id.checkBox3),
            dialogView.findViewById(R.id.checkBox4),
            dialogView.findViewById(R.id.checkBox5)
        )

        val images = listOf(
            dialogView.findViewById<ImageView>(R.id.img1),
            dialogView.findViewById(R.id.img2),
            dialogView.findViewById(R.id.img3),
            dialogView.findViewById(R.id.img4),
            dialogView.findViewById(R.id.img5)
        )

        fun toggleSelection(selectedIndex: Int) {
            checkBoxes.forEachIndexed { index, checkBox ->
                checkBox.isChecked = index == selectedIndex
            }

            cards.forEachIndexed { index, card ->
                card.setBackgroundResource(if (index == selectedIndex) R.drawable.btn_bg else R.drawable.card_bg1)
            }

            images.forEachIndexed { index, img ->
                img.setImageResource(if (index == selectedIndex) R.drawable.selected else R.drawable.unselect)
            }
        }

        cards.forEachIndexed { index, card ->
            card.setOnClickListener { toggleSelection(index) }
        }

        dialogView.findViewById<TextView>(R.id.btnApply).setOnClickListener {
            val selectedIndex = checkBoxes.indexOfFirst { it.isChecked }
            if (selectedIndex != -1) {


                val urlName = txtURL.text.toString()
                if (isInternetAvailable(requireContext())) {
                    Log.e("URLLL", "URL :$urlName")
                    // Update UI safely using lifecycleScope
                    viewLifecycleOwner.lifecycleScope.launch {
                        progressBar.visibility = View.VISIBLE
                    }
                    videoResolution = selectedIndex.toString()  //todo for set video resolution
                    Log.e("ChackChip", "Select =" + videoResolution)
                    Executors.newSingleThreadExecutor().execute { fetchData(urlName) }
                } else {
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.no_internet_connection), Toast.LENGTH_SHORT
                    )
                        .show()
                }

            } else {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.no_selection_made), Toast.LENGTH_SHORT
                ).show()
            }
            dialog.dismiss()
        }

        dialog.show()
    }



    override fun onResume() {
        super.onResume()
        setupRecyclerView()
        // Access clipboard
        val clipboard =
            requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

        if (clipboard.hasPrimaryClip() && clipboard.primaryClipDescription?.hasMimeType("text/plain") == true) {
            val clipData = clipboard.primaryClip
            val pastedText = clipData?.getItemAt(0)?.text.toString()

            // Auto-paste into EditText
            txtURL.setText(pastedText)
        }

    }
}