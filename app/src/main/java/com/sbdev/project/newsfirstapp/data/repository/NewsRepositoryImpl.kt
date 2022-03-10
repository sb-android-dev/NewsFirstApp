package com.sbdev.project.newsfirstapp.data.repository

import com.sbdev.project.newsfirstapp.data.entity.Article
import com.sbdev.project.newsfirstapp.data.entity.NewsResponse
import com.sbdev.project.newsfirstapp.data.local.ArticleDAO
import com.sbdev.project.newsfirstapp.data.remote.RetrofitInstance
import kotlinx.coroutines.flow.Flow
import retrofit2.Response
import javax.inject.Inject

class NewsRepositoryImpl @Inject constructor(
    private val dao: ArticleDAO
) : NewsRepository {
    override suspend fun getBreakingNews(
        countryCode: String,
        pageNumber: Int
    ): Response<NewsResponse> = RetrofitInstance.api.getBreakingNews(countryCode, pageNumber)

    override suspend fun getSearchedNews(
        searchQuery: String,
        pageNumber: Int
    ): Response<NewsResponse> = RetrofitInstance.api.searchForNews(searchQuery, pageNumber)

    override suspend fun saveArticle(article: Article): Long = dao.saveArticle(article)

    override suspend fun deleteArticle(article: Article): Int = dao.deleteArticle(article)

    override fun getSavedArticles(): Flow<List<Article>> = dao.getAllArticles()
}