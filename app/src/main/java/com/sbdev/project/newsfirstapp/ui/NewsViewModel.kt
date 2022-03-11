package com.sbdev.project.newsfirstapp.ui

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.ConnectivityManager.*
import android.net.NetworkCapabilities.*
import android.os.Build
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseUser
import com.sbdev.project.newsfirstapp.NewsFirstApp
import com.sbdev.project.newsfirstapp.data.Response
import com.sbdev.project.newsfirstapp.data.entity.Article
import com.sbdev.project.newsfirstapp.data.entity.NewsResponse
import com.sbdev.project.newsfirstapp.data.repository.AuthRepository
import com.sbdev.project.newsfirstapp.data.repository.NewsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class NewsViewModel @Inject constructor(
    app: Application,
    private val authRepository: AuthRepository,
    private val repository: NewsRepository
) : AndroidViewModel(app) {

    val searchNews: MutableLiveData<Response<NewsResponse>> = MutableLiveData()
    var searchNewsPage = 1
    var searchNewsResponse: NewsResponse? = null

    val user = MutableLiveData<FirebaseUser>()

    init {
        getBreakingNews()
        getCurrentUser()
    }

    private fun getCurrentUser(){
        user.value = authRepository.getCurrentUser()
    }

    private fun getBreakingNews() = viewModelScope.launch {
        safeBreakingNewsCall("in")
    }

    fun searchNews(searchQuery: String) = viewModelScope.launch {
        if (searchQuery.isEmpty() || searchQuery.isBlank()) {
            safeBreakingNewsCall("in")
        } else {
            safeSearchNewsCall(searchQuery)
        }
    }

    private fun handleBreakingNewsResponse(response: retrofit2.Response<NewsResponse>): Response<NewsResponse> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                if (searchNewsResponse == null || searchNewsPage == 1) {
                    searchNewsResponse = resultResponse
                } else {
                    val oldArticles = searchNewsResponse?.articles
                    val newArticles = resultResponse.articles
                    oldArticles?.addAll(newArticles)
                }
                searchNewsPage++
                return Response.Success(searchNewsResponse ?: resultResponse)
            }
        }
        return Response.Error(response.message())
    }

    private fun handleSearchNewsResponse(response: retrofit2.Response<NewsResponse>): Response<NewsResponse> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                if (searchNewsResponse == null || searchNewsPage == 1) {
                    searchNewsResponse = resultResponse
                } else {
                    val oldArticles = searchNewsResponse?.articles
                    val newArticles = resultResponse.articles
                    oldArticles?.addAll(newArticles)
                }
                searchNewsPage++
                return Response.Success(searchNewsResponse ?: resultResponse)
            }
        }
        return Response.Error(response.message())
    }

    fun saveArticle(article: Article) = viewModelScope.launch {
        repository.saveArticle(article)
    }

    fun deleteArticle(article: Article) = viewModelScope.launch {
        repository.deleteArticle(article)
    }

    fun getSavedArticles() = repository.getSavedArticles().asLiveData()

    private suspend fun safeBreakingNewsCall(countryCode: String) {
        searchNews.postValue(Response.Loading)
        try {
            if (hasInternetConnection()) {
                val response = repository.getBreakingNews(countryCode, searchNewsPage)
                searchNews.postValue(handleBreakingNewsResponse(response))
            } else {
                searchNews.postValue(Response.Error("No internet connection"))
            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> searchNews.postValue(Response.Error("Network Failure"))
                else -> searchNews.postValue(Response.Error("Conversion Error"))
            }
        }
    }

    private suspend fun safeSearchNewsCall(searchQuery: String) {
        searchNews.postValue(Response.Loading)
        try {
            if (hasInternetConnection()) {
                val response = repository.getSearchedNews(searchQuery, "in", searchNewsPage)
                searchNews.postValue(handleSearchNewsResponse(response))
            } else {
                searchNews.postValue(Response.Error("No internet connection"))
            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> searchNews.postValue(Response.Error("Network Failure"))
                else -> searchNews.postValue(Response.Error("Conversion Error"))
            }
        }
    }

    private fun hasInternetConnection(): Boolean {
        val connectivityManager = getApplication<NewsFirstApp>().getSystemService(
            Context.CONNECTIVITY_SERVICE
        ) as ConnectivityManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val activeNetwork = connectivityManager.activeNetwork ?: return false
            val capabilities =
                connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
            return when {
                capabilities.hasTransport(TRANSPORT_WIFI) -> true
                capabilities.hasTransport(TRANSPORT_CELLULAR) -> true
                capabilities.hasTransport(TRANSPORT_ETHERNET) -> true
                else -> false
            }
        } else {
            connectivityManager.activeNetworkInfo?.run {
                return when (type) {
                    TYPE_WIFI -> true
                    TYPE_MOBILE -> true
                    TYPE_ETHERNET -> true
                    else -> false
                }
            }
        }

        return false
    }

}