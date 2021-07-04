package com.spbstu.journeypln.model.activities

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupWithNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.spbstu.journeypln.R
import com.spbstu.journeypln.presenters.activityPresenters.MainPresenter
import com.spbstu.journeypln.views.MainView
import com.squareup.picasso.Picasso
import kotlinx.coroutines.*
import moxy.MvpAppCompatActivity
import moxy.presenter.InjectPresenter
import java.io.IOException
import java.net.URL


class MainActivity : MvpAppCompatActivity(), MainView {

    @InjectPresenter
    lateinit var presenter: MainPresenter

    private lateinit var navController: NavController
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var navigationView: NavigationView
    private lateinit var toolbar: Toolbar

    private lateinit var userImage: ImageView
    private lateinit var email: TextView
    private lateinit var logoutButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        init()

        setSupportActionBar(toolbar)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragment) as NavHostFragment
        navController = navHostFragment.navController

        navigationView.setupWithNavController(navController)
        initNavViewHeader()

        navigationView.setNavigationItemSelectedListener { item: MenuItem ->
            when (item.itemId) {
                R.id.allTripsFragment ->
                    navController.navigate(R.id.allTripsFragment)
                R.id.creationNewTripFragment ->
                    navController.navigate(R.id.creationNewTripFragment)
                R.id.thisTripFragment ->
                    presenter.getCurrentTrip()
                R.id.tripsFragment ->
                    navController.navigate(R.id.tripsFragment)
                R.id.hotelsFragment ->
                    navController.navigate(R.id.hotelsFragment)
                R.id.aboutFragment ->
                    navController.navigate(R.id.aboutFragment)
            }
            drawerLayout.closeDrawer(GravityCompat.START)
            return@setNavigationItemSelectedListener true
        }

        logoutButton.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(applicationContext, LoginActivity::class.java)
            startActivity(intent)
        }

        appBarConfiguration = AppBarConfiguration(navController.graph, drawerLayout)
        toolbar.setupWithNavController(navController, appBarConfiguration)
    }

    private fun init() {
        toolbar = findViewById(R.id.toolbar)
        logoutButton = findViewById(R.id.signOut)
        drawerLayout = findViewById(R.id.drawer_layout)
        navigationView = findViewById(R.id.navigationView)
    }

    private fun initNavViewHeader() {
        val signInAccount = GoogleSignIn.getLastSignedInAccount(this)
        val hView = navigationView.getHeaderView(0)
        userImage = hView.findViewById(R.id.user_photo)
        email = hView.findViewById(R.id.user_email)

        if (signInAccount != null) {
            email.text = signInAccount.displayName
            Picasso.with(this)
                .load(signInAccount.photoUrl)
                .into(userImage)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        println(requestCode)
        supportFragmentManager.primaryNavigationFragment?.childFragmentManager?.fragments?.forEach { fragment ->
            fragment.onActivityResult(requestCode, resultCode, data)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        Toast.makeText(this, "activity permission good", Toast.LENGTH_SHORT).show()

        supportFragmentManager.primaryNavigationFragment?.childFragmentManager?.fragments?.forEach { fragment ->
            fragment.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    private fun URL.toBitmap(): Bitmap?{
        return try {
            BitmapFactory.decodeStream(openStream())
        }catch (e: IOException){
            null
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.fragment)

        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun startCurrentTripPage(key: String) {
        val bundle = Bundle()
        bundle.putString("id", key)
        navController.navigate(R.id.thisTripFragment, bundle)
    }

}