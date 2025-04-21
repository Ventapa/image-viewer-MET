package com.example.metimageviewer

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory

class MainViewModel : ViewModel() {
  private val _images = MutableStateFlow<List<MetImage>>(emptyList())
  val images: StateFlow<List<MetImage>> = _images

  private val _isLoading = MutableStateFlow(false)
  val isLoading: StateFlow<Boolean> = _isLoading
  private val moshi by lazy {
    Moshi.Builder()
      .add(KotlinJsonAdapterFactory())
      .build()
  }

  // Retrofit setup
  private val service by lazy {
    Retrofit.Builder()
      .baseUrl("https://collectionapi.metmuseum.org/public/collection/v1/")
      .addConverterFactory(MoshiConverterFactory.create(moshi))
      .build()
      .create(MetApiService::class.java)
  }
  private val repo by lazy { MetRepository(service) }

  fun loadImages() {
    viewModelScope.launch {
      _isLoading.value = true
      try {
        // network on IO
        val ids = withContext(Dispatchers.IO) {
          repo.fetchObjectIds(query = "cat", count = 5)
        }
        val fetched = withContext(Dispatchers.IO) {
          repo.getImages(ids)
        }
        _images.value = fetched
        Log.d("MainViewModel", "Fetched ${fetched.size} images")
      } catch (e: Exception) {
        Log.e("MainViewModel", "Error loading images", e)
      } finally {
        _isLoading.value = false
      }
    }
  }
}
