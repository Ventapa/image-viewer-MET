package com.example.metimageviewer

class MetRepository(private val api: MetApiService) {
  /**
   * Search for objects matching [query] and return a random sample of [count] IDs.
   */
  suspend fun fetchObjectIds(query: String, count: Int): List<Int> {
    val allIds = api.search(query = query).objectIDs.orEmpty()
    return allIds
      .shuffled()       // shuffle the full list
      .take(count)      // then take the first [count] entries
  }

  /** Fetch the full MetImage data for each ID. */
  suspend fun getImages(ids: List<Int>): List<MetImage> =
    ids.mapNotNull { runCatching { api.getObject(it) }.getOrNull() }
}
