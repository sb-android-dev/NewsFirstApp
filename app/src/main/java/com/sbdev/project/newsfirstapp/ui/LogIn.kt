package com.sbdev.project.newsfirstapp.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.sbdev.project.newsfirstapp.Constants.Companion.RC_GOOGLE_SIGN_IN
import com.sbdev.project.newsfirstapp.R
import com.sbdev.project.newsfirstapp.databinding.ActivityLogInBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LogIn : AppCompatActivity() {

    lateinit var binding: ActivityLogInBinding
    private val viewModel: LogInViewModel by viewModels()

    private lateinit var googleSignInClient: GoogleSignInClient


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLogInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Configure Google Sign In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        binding.tvSignInWithGoogle.setOnClickListener {
            val signInIntent = googleSignInClient.signInIntent
            startActivityForResult(signInIntent, RC_GOOGLE_SIGN_IN)
        }
    }

    override fun onStart() {
        super.onStart()
        val currentUser = viewModel.auth.currentUser
        updateUI(currentUser)
    }

    private fun updateUI(user: FirebaseUser?) {
        if(user != null){
            Intent(this, MainActivity::class.java)
                .also {
                    startActivity(it)
                    finishAffinity()
                }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        viewModel.auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("LogIn", "signInWithCredential:success")
                    val user = viewModel.auth.currentUser
                    updateUI(user)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w("LogIn", "signInWithCredential:failure", task.exception)
                    updateUI(null)
                }
            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_GOOGLE_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)!!
                Log.d("LogIn", "firebaseAuthWithGoogle:" + account.id)
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Log.w("LogIn", "Google sign in failed", e)
            }
        }
    }
}