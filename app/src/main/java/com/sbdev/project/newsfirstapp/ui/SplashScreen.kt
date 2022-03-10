package com.sbdev.project.newsfirstapp.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.coroutineScope
import com.sbdev.project.newsfirstapp.databinding.ActivitySplashScreenBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@SuppressLint("CustomSplashScreen")
class SplashScreen : AppCompatActivity() {

    lateinit var binding: ActivitySplashScreenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        lifecycle.coroutineScope.launch(Dispatchers.Main) {
            delay(2000)
            openNextActivity()
        }
    }

    private fun openNextActivity() {
        Intent(this, MainActivity::class.java)
            .also {
                startActivity(it)
            }
        finish()
    }
}