package com.sbdev.project.newsfirstapp.data.repository

import com.sbdev.project.newsfirstapp.data.entity.Article
import com.sbdev.project.newsfirstapp.data.entity.NewsResponse
import kotlinx.coroutines.flow.Flow
import retrofit2.Response

interface NewsRepository {

    suspend fun getBreakingNews(countryCode: String, pageNumber: Int): Response<NewsResponse>

    suspend fun getSearchedNews(searchQuery: String, countryCode: String, pageNumber: Int): Response<NewsResponse>

    suspend fun saveArticle(article: Article): Long

    suspend fun deleteArticle(article: Article): Int

    fun getSavedArticles(): Flow<List<Article>>
}