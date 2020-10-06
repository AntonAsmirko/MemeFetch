package com.example.memes.activities.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.memes.activities.data.MemWithBitmap

class ImagesViewModel : ViewModel() {
    val memes = MutableLiveData<String>()
    val memesWithImages = MutableLiveData<List<MemWithBitmap>?>()

}