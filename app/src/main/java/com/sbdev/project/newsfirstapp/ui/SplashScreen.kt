package com.sbdev.project.newsfirstapp.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.core.view.ViewCompat
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
        val intent = Intent(this, LogIn::class.java)
        val animPair1: Pair<View, String> = Pair(
            binding.ivAppLogo,
            ViewCompat.getTransitionName(binding.ivAppLogo)!!
        )

        val options: ActivityOptionsCompat = ActivityOptionsCompat
            .makeSceneTransitionAnimation(
                this,
                animPair1
            )

        startActivity(intent, options.toBundle())
//        finish()
    }
}