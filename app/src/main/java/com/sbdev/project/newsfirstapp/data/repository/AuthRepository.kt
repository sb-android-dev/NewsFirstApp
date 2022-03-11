package com.sbdev.project.newsfirstapp.data.repository

import com.google.firebase.auth.FirebaseUser
import com.sbdev.project.newsfirstapp.data.Response
import com.sbdev.project.newsfirstapp.data.remote.FirebaseAuthSource
import com.sbdev.project.newsfirstapp.data.remote.FirebaseAuthStateObserver
import javax.inject.Inject

class AuthRepository @Inject constructor(
    private val authSource: FirebaseAuthSource
) {
    fun getAuthId(): String? {
        return authSource.getAuthId()
    }

    fun getCurrentUser(): FirebaseUser? {
        return authSource.getCurrentUser()
    }

    fun observeAuthState(
        stateObserver: FirebaseAuthStateObserver,
        b: ((Response<String>) -> Unit)
    ) {
        authSource.attachAuthStateObserver(stateObserver, b)
    }

    fun verifyUser(verificationId: String, otp: String, b: ((Response<String>) -> Unit)) {
        b.invoke(Response.Loading)
        authSource.signIn(verificationId, otp)
            .addOnSuccessListener {
                b.invoke(Response.Success(it.user?.uid))
            }
            .addOnCanceledListener {
                b.invoke(Response.Error("Sign-in is cancelled"))
            }
            .addOnFailureListener {
                b.invoke(Response.Error(it.message))
            }
    }

    fun signOutUser() {
        authSource.signOut()
    }

}