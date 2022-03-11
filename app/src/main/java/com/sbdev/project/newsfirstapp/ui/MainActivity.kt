package com.sbdev.project.newsfirstapp.ui

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.sbdev.project.newsfirstapp.R
import com.sbdev.project.newsfirstapp.databinding.ActivityMainBinding
import com.sbdev.project.newsfirstapp.setGreeting
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    val viewModel: NewsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.mainNavHostFragment) as NavHostFragment
        binding.bottomNavMenu.setupWithNavController(navHostFragment.navController)

        viewModel.user.observe(this) { user ->
            supportActionBar?.let {
                it.title = "Welcome, ${user.displayName ?: ""}"
//                it.title = setGreeting(
//                    user.displayName ?: ""
//                )
            }
        }
    }
}