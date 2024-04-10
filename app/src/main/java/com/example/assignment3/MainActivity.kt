package com.example.assignment3

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI

class MainActivity : AppCompatActivity() {
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //sets title for toolbar
        this.title = "BetterMail"

        //sets content view
        setContentView(R.layout.activity_main)

        //grabs toolbar and sets up as action bar
        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        //nav host stuff
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        //allows toolbar to use nav controller
        NavigationUI.setupActionBarWithNavController(this, navController)
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true;
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            //shares info about better mail
            R.id.shareButton -> {
                val sharingIntent = Intent(Intent.ACTION_SEND)

                sharingIntent.setType("text/plain")

                val shareBody = "Get BetterMail"

                val shareSubject = "Better mail, write mail for the future"

                sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody)

                sharingIntent.putExtra(Intent.EXTRA_SUBJECT, shareSubject)
                startActivity(Intent.createChooser(sharingIntent, "Share using"))
            }
            //displays the help fragment which explains the app
            R.id.helpFragment -> {
                NavigationUI.onNavDestinationSelected(item, navController)
            }
        }
        return super.onOptionsItemSelected(item)
    }
}