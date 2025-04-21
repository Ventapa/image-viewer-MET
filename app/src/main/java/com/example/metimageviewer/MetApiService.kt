package com.example.metimageviewer

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

data class SearchResponse(
  val total: Int,
  val objectIDs: List<Int>?
)

interface MetApiService {
  @GET("search")
  suspend fun search(
    @Query("hasImages") hasImages: Boolean = true,
    @Query("q") query: String
  ): SearchResponse

  @GET("objects/{id}")
  suspend fun getObject(@Path("id") id: Int): MetImage
}
