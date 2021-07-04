package com.spbstu.journeypln.views

import android.content.Intent
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import moxy.MvpView
import moxy.viewstate.strategy.AddToEndSingleStrategy
import moxy.viewstate.strategy.StateStrategyType

interface LoginView: MvpView {
    @StateStrategyType(value = AddToEndSingleStrategy::class)
    fun startMainActivity()

    @StateStrategyType(value = AddToEndSingleStrategy::class)
    fun startAuthCheck(intent: Intent)

    @StateStrategyType(value = AddToEndSingleStrategy::class)
    fun firebaseAuthWithGoogle(credential: AuthCredential, auth: FirebaseAuth)
}