package com.sbdev.project.newsfirstapp.data.remote

import com.sbdev.project.newsfirstapp.BuildConfig
import com.sbdev.project.newsfirstapp.data.entity.NewsResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface NewsAPI {

    @GET("v2/top-headlines")
    suspend fun getBreakingNews(
        @Query("country") countryCode: String = "in",
        @Query("page") pageNo: Int = 1,
        @Query("apiKey") key: String = BuildConfig.API_KEY
    ): Response<NewsResponse>

    @GET("v2/everything")
    suspend fun searchForNews(
        @Query("q") searchQuery: String,
        @Query("country") countryCode: String = "in",
        @Query("page") pageNo: Int = 1,
        @Query("apiKey") key: String = BuildConfig.API_KEY
    ): Response<NewsResponse>

}