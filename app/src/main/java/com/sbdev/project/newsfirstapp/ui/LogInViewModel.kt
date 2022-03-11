package com.sbdev.project.newsfirstapp.ui

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LogInViewModel @Inject constructor(
    val auth: FirebaseAuth
): ViewModel() {



}