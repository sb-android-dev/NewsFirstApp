package com.sbdev.project.newsfirstapp.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.sbdev.project.newsfirstapp.adapters.NewsRecyclerAdapter
import com.sbdev.project.newsfirstapp.data.entity.Article
import com.sbdev.project.newsfirstapp.databinding.FragmentBookmarksBinding
import com.sbdev.project.newsfirstapp.ui.MainActivity
import com.sbdev.project.newsfirstapp.ui.NewsDetail
import com.sbdev.project.newsfirstapp.ui.NewsViewModel

class BookmarksFragment : Fragment() {

    private lateinit var binding: FragmentBookmarksBinding
    private lateinit var viewModel: NewsViewModel

    lateinit var adapter: NewsRecyclerAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBookmarksBinding.inflate(inflater, container, false)

        viewModel = (activity as MainActivity).viewModel
        setupRecyclerView()

        val itemTouchHelperCallback =
            object : ItemTouchHelper.SimpleCallback(
                ItemTouchHelper.UP or ItemTouchHelper.DOWN,
                ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
            ) {
                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {
                    return true
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    val position = viewHolder.adapterPosition
                    val article = adapter.currentList[position]
                    viewModel.deleteArticle(article)
                    Snackbar.make(view!!, "Article deleted", Snackbar.LENGTH_SHORT).apply {
                        setAction("Undo") {
                            viewModel.saveArticle(article)
                        }
                        show()
                    }
                }
            }
        ItemTouchHelper(itemTouchHelperCallback).apply {
            attachToRecyclerView(binding.rvBookmarkNews)
        }

        viewModel.getSavedArticles().observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }

        return binding.root
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
                    article.isBookmarked = false
                    viewModel.deleteArticle(article)
                    Snackbar.make(binding.rvBookmarkNews, "Article deleted!", Snackbar.LENGTH_SHORT).apply {
                        anchorView = binding.rvBookmarkNews
                        show()
                    }
                }
            }
        })
        binding.rvBookmarkNews.adapter = adapter
    }
}