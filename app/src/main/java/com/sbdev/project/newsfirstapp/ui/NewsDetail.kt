package com.sbdev.project.newsfirstapp.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.webkit.WebViewClient
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.sbdev.project.newsfirstapp.data.entity.Article
import com.sbdev.project.newsfirstapp.databinding.ActivityNewsDetailBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NewsDetail : AppCompatActivity() {

    private lateinit var binding: ActivityNewsDetailBinding
    private val viewModel: NewsViewModel by viewModels()

    private var article: Article? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewsDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        intent?.let {
            article = it.getSerializableExtra("article") as Article
        }

        binding.webView.apply {
            webViewClient = WebViewClient()
            article?.let {
                loadUrl(it.url)
            }
        }

        binding.fabBookmark.setOnClickListener {
            article?.let { article ->
                viewModel.saveArticle(article)
                Snackbar.make(it, "News bookmarked!", Snackbar.LENGTH_SHORT).apply {
                    anchorView = binding.fabBookmark
                    show()
                }
            }
        }

        binding.ivBack.setOnClickListener {
            onBackPressed()
        }

        binding.tvViewInBrowser.setOnClickListener {
            article?.let { article ->
                Intent(Intent.ACTION_VIEW).apply {
                    data = Uri.parse(article.url)
                }.also {
                    startActivity(it)
                }
            }
        }
    }
}