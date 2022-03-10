package com.sbdev.project.newsfirstapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.sbdev.project.newsfirstapp.Constants.Companion.DATE_FORMAT
import com.sbdev.project.newsfirstapp.Constants.Companion.SERVER_DATE_FORMAT
import com.sbdev.project.newsfirstapp.data.entity.Article
import com.sbdev.project.newsfirstapp.databinding.ItemNewsBinding
import java.text.SimpleDateFormat
import java.util.*

class NewsRecyclerAdapter (private val listener: OnItemClickListener) :
    ListAdapter<Article, NewsRecyclerAdapter.ArticleViewHolder>(DiffCallback()) {

    companion object{
        val df = SimpleDateFormat(DATE_FORMAT, Locale.ROOT)
        val sdf = SimpleDateFormat(SERVER_DATE_FORMAT, Locale.ROOT)
    }

    interface OnItemClickListener {
        fun onClick(item: Article?)
    }

    inner class ArticleViewHolder(private val binding: ItemNewsBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bindView(item: Article?) {
            Glide.with(binding.ivArticleImage).load(item!!.urlToImage).into(binding.ivArticleImage)
            binding.tvSource.text = item.source.name
            binding.tvTitle.text = item.title
            binding.tvDescription.text = item.description
            val date = sdf.parse(item.publishedAt)
            binding.tvPublishedAt.text = df.format(date!!)

            itemView.setOnClickListener {
                listener.onClick(item)
            }
        }

    }

    class DiffCallback : DiffUtil.ItemCallback<Article>() {
        override fun areItemsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem.url == newItem.url
        }

        override fun areContentsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleViewHolder {
        val binding =
            ItemNewsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ArticleViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) {
        holder.bindView(getItem(position))
    }
}