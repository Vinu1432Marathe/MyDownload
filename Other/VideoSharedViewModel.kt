package com.video.download.vidlink.Other

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class VideoSharedViewModel: ViewModel() {
    val refreshTrigger = MutableLiveData<Unit>()

    fun triggerRefresh() {
        refreshTrigger.value = Unit
    }
}