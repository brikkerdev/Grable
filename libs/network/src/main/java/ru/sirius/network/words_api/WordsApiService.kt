package ru.sirius.network.words_api

import retrofit2.http.GET

private const val WORDS_PATH = "/v3/b/69381a00ae596e708f8d9bef"

interface WordsApiService {
    @GET(WORDS_PATH)
    suspend fun getWords(): WordsResponse
}