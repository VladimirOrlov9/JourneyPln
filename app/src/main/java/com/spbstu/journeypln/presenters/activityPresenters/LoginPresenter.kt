package com.spbstu.journeypln.presenters.activityPresenters

import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.spbstu.journeypln.R
import com.spbstu.journeypln.model.activities.LoginActivity
import com.spbstu.journeypln.model.activities.MainActivity
import com.spbstu.journeypln.views.LoginView
import moxy.MvpPresenter

class LoginPresenter: MvpPresenter<LoginView>() {

    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var auth: FirebaseAuth
    private lateinit var applicationContext: Context

    fun setApplicationContext(context: Context) {
        applicationContext = context
    }

    fun checkIfUserLoggedIn() {
        val user = auth.currentUser
        if (user != null) {
            viewState.startMainActivity()
        }
    }

    fun createRequest(webClientId: String) {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(webClientId)
                .requestEmail()
                .build()

        googleSignInClient = GoogleSignIn.getClient(applicationContext, gso)
    }

    fun signIn() {
        val signInIntent = googleSignInClient.signInIntent
        viewState.startAuthCheck(signInIntent)
    }

    fun authWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        viewState.firebaseAuthWithGoogle(credential, auth)
    }

    fun checkUserInstance() {
        auth = FirebaseAuth.getInstance()
    }

}