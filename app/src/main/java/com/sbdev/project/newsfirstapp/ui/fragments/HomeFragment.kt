package com.sbdev.project.newsfirstapp.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.sbdev.project.newsfirstapp.Constants.Companion.QUERY_PAGE_SIZE
import com.sbdev.project.newsfirstapp.Constants.Companion.SEARCH_NEWS_TIME_DELAY
import com.sbdev.project.newsfirstapp.R
import com.sbdev.project.newsfirstapp.adapters.NewsRecyclerAdapter
import com.sbdev.project.newsfirstapp.data.Response
import com.sbdev.project.newsfirstapp.data.entity.Article
import com.sbdev.project.newsfirstapp.databinding.FragmentHomeBinding
import com.sbdev.project.newsfirstapp.ui.MainActivity
import com.sbdev.project.newsfirstapp.ui.NewsDetail
import com.sbdev.project.newsfirstapp.ui.NewsViewModel
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.WithFragmentBindings
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var viewModel: NewsViewModel

    lateinit var adapter: NewsRecyclerAdapter

    var isLoading = false
    var isLastPage = false
    var isScrolling = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        viewModel = (activity as MainActivity).viewModel
        setupRecyclerView()

        var job: Job? = null
        binding.etSearch.addTextChangedListener { editable ->
            job?.cancel()
            job = MainScope().launch {
                delay(SEARCH_NEWS_TIME_DELAY)
                editable?.let {
//                    if(editable.toString()) {
                        viewModel.searchNewsPage = 1
                        viewModel.searchNews(editable.toString())
//                    }
                }
            }
        }

        viewModel.searchNews.observe(viewLifecycleOwner) { response ->
            when (response) {
                is Response.Success -> {
                    hideProgressBar()
                    response.data?.let { newsResponse ->
                        viewModel.savedArticles.value?.let { saveArticles ->
                            for(article in newsResponse.articles) {
                                val a = saveArticles.find { savedArticle ->
                                    article.url == savedArticle.url
                                }
                                article.isBookmarked = a != null
                            }
                        }

                        adapter.submitList(newsResponse.articles.toList())

                        val totalPages = newsResponse.totalResults / QUERY_PAGE_SIZE + 2
                        isLastPage = viewModel.searchNewsPage == totalPages

                        if(isLastPage){
                            binding.rvSearchNews.setPadding(0, 0, 0, 0)
                        }
                    }
                }
                is Response.Error -> {
                    hideProgressBar()
                    response.msg?.let { message ->
                        Toast.makeText(activity, "An error occurred: $message", Toast.LENGTH_LONG)
                            .show()
                    }
                }
                is Response.Loading -> {
                    showProgressBar()
                }
            }
        }

        return binding.root
    }

    private fun hideProgressBar() {
        binding.paginationProgressBar.visibility = View.INVISIBLE
        isLoading = false
    }

    private fun showProgressBar() {
        binding.paginationProgressBar.visibility = View.VISIBLE
        isLoading = true
    }

    private val scrollListener = object: RecyclerView.OnScrollListener() {
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            if(newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                isScrolling = true
            }
        }

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            val layoutManager = recyclerView.layoutManager as LinearLayoutManager
            val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
            val visibleItemCount = layoutManager.childCount
            val totalItemCount = layoutManager.itemCount

            val isNotLoadingAndNotLastPage = !isLoading && !isLastPage
            val isAtLastItem = firstVisibleItemPosition + visibleItemCount >= totalItemCount
            val isNotAtBeginning = firstVisibleItemPosition >= 0
            val isTotalMoreThanVisible = totalItemCount >= QUERY_PAGE_SIZE

            val shouldPaginate = isNotLoadingAndNotLastPage && isAtLastItem && isNotAtBeginning && isTotalMoreThanVisible && isScrolling

            if(shouldPaginate){
                Log.d("HomeFragment", "onScrolled: ")
                viewModel.searchNews(binding.etSearch.text.toString())
                isScrolling = false
            } else {
                binding.rvSearchNews.setPadding(0, 0, 0, 0)
            }
        }
    }

    private fun setupRecyclerView() {
        adapter = NewsRecyclerAdapter(object : NewsRecyclerAdapter.OnItemClickListener {
            override fun onClick(item: Article?) {
                val bundle = Bundle().apply {
                    putSerializable("article", item)
                }
                Intent(requireActivity(), NewsDetail::class.java).apply {
                    putExtras(bundle)
                }.also {
                    startActivity(it)
                }
            }

            override fun onBookmark(item: Article?, position: Int) {
                item?.let { article ->
                    article.isBookmarked = !article.isBookmarked
                    if(!article.isBookmarked) {
                        viewModel.deleteArticle(article)
                        Snackbar.make(binding.rvSearchNews, "News removed from bookmarks!", Snackbar.LENGTH_SHORT).apply {
                            show()
                        }
                    } else {
                        viewModel.saveArticle(article)
                        Snackbar.make(binding.rvSearchNews, "News bookmarked!", Snackbar.LENGTH_SHORT).apply {
                            show()
                        }
                    }
                    adapter.notifyItemChanged(position)
                }
            }
        })
        binding.rvSearchNews.adapter = adapter
        binding.rvSearchNews.addOnScrollListener(scrollListener)
    }
}