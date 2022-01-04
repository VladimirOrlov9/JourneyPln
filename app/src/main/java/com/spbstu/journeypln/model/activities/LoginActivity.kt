package com.spbstu.journeypln.model.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.spbstu.journeypln.R
import com.spbstu.journeypln.presenters.activityPresenters.LoginPresenter
import com.spbstu.journeypln.views.LoginView
import moxy.MvpAppCompatActivity
import moxy.presenter.InjectPresenter

class LoginActivity : MvpAppCompatActivity() {

//    @InjectPresenter
//    lateinit var presenter: LoginPresenter

    private lateinit var googleSignInButton: SignInButton

    override fun onStart() {
        super.onStart()

//        presenter.checkIfUserLoggedIn()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
//        init()

//        presenter.setApplicationContext(applicationContext)

//        val webClientId = getString(R.string.default_web_client_id)
//        presenter.createRequest(webClientId)

//        googleSignInButton.setOnClickListener {
//            presenter.signIn()
//        }
//        presenter.checkUserInstance()

    }

    private fun init() {
        googleSignInButton = findViewById(R.id.google_signIn)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
//        if (requestCode == RC_SIGN_IN) {
//            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
//            try {
//                // Google Sign In was successful, authenticate with Firebase
//                val account = task.getResult(ApiException::class.java)!!
//                Log.d("LoginActivity", "firebaseAuthWithGoogle:" + account.id)
//                presenter.authWithGoogle(account.idToken!!)
//            } catch (e: ApiException) {
//                // Google Sign In failed, update UI appropriately
//                Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
//            }
//        }
    }

//    override fun firebaseAuthWithGoogle(credential: AuthCredential, auth: FirebaseAuth) {
//        auth.signInWithCredential(credential)
//            .addOnCompleteListener(this) { task ->
//                if (task.isSuccessful) {
//                    auth.currentUser
//                    val intent = Intent(applicationContext, MainActivity::class.java)
//                    startActivity(intent)
//                } else {
//                    // If sign in fails, display a message to the user.
//                    Toast.makeText(this, "Could not auth with Google.", Toast.LENGTH_SHORT).show()
//                }
//            }
//    }

    companion object {
        const val RC_SIGN_IN = 123
    }

//    override fun startMainActivity() {
//        val intent = Intent(applicationContext, MainActivity::class.java)
//        startActivity(intent)
//    }

//    override fun startAuthCheck(intent: Intent) {
//        startActivityForResult(intent, RC_SIGN_IN)
//    }
}