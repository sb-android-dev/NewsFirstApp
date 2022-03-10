package com.sbdev.project.newsfirstapp.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.sbdev.project.newsfirstapp.databinding.ActivityLogInBinding

class LogIn : AppCompatActivity() {

    lateinit var binding: ActivityLogInBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLogInBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}