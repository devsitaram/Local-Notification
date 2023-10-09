package com.record.localnotification.features.email

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel


class EmailShareViewModel: ViewModel() {

    private val _selectedImages = MutableLiveData<MutableList<Uri>>(mutableListOf())
    val selectedImages: LiveData<MutableList<Uri>> = _selectedImages

    fun addSelectedImage(uri: Uri) {
        val currentList = _selectedImages.value ?: mutableListOf()
        currentList.add(uri)
        _selectedImages.value = currentList
    }

    fun removeSelectedImage(uri: Uri) {
        val currentList = _selectedImages.value ?: mutableListOf()
        currentList.remove(uri)
        _selectedImages.value = currentList
    }
}