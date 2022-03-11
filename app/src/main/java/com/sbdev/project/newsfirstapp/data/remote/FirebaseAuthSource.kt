package com.sbdev.project.newsfirstapp.data.remote

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.PhoneAuthProvider
import com.sbdev.project.newsfirstapp.data.Response
import javax.inject.Inject

class FirebaseAuthSource @Inject constructor(
    val auth: FirebaseAuth
) {

    fun getAuthId(): String? {
        return auth.uid
    }

    fun getCurrentUser(): FirebaseUser? {
        return auth.currentUser
    }

    private fun attachAuthObserver(b: ((Response<String>) -> Unit)): FirebaseAuth.AuthStateListener {
        return FirebaseAuth.AuthStateListener {
            if (it.uid == null) {
                b.invoke(Response.Error("No user"))
            } else {
                b.invoke(Response.Success(it.uid!!))
            }
        }
    }

    fun attachAuthStateObserver(
        stateObserver: FirebaseAuthStateObserver,
        b: ((Response<String>) -> Unit)
    ) {
        val listener = attachAuthObserver(b)
        stateObserver.start(listener, auth)
    }

    fun signIn(verificationId: String, otp: String): Task<AuthResult> {
        val credentials = PhoneAuthProvider.getCredential(verificationId, otp)
        return auth.signInWithCredential(credentials)
    }

    fun signOut() {
        auth.signOut()
    }

}

class FirebaseAuthStateObserver {

    private var authListener: FirebaseAuth.AuthStateListener? = null
    private var instance: FirebaseAuth? = null

    fun start(authStateListener: FirebaseAuth.AuthStateListener, instance: FirebaseAuth) {
        this.authListener = authStateListener
        this.instance = instance
        this.instance?.addAuthStateListener(authListener!!)
    }

    fun clear() {
        authListener?.let {
            instance?.removeAuthStateListener(it)
        }
    }

}