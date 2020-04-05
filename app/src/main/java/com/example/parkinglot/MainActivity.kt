package com.example.parkinglot

import android.content.Context
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.io.BufferedWriter
import java.io.File
import java.io.FileOutputStream
import java.io.FileWriter
import java.time.Instant
import java.time.format.DateTimeFormatter

class MainActivity : AppCompatActivity()
{
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val navView: BottomNavigationView = findViewById(R.id.nav_view)

        val navController = findNavController(R.id.nav_host_fragment)

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(setOf(
                R.id.navigation_home, R.id.navigation_dashboard,  R.id.navigation_parkinglots, R.id.navigation_notifications))
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        var location = Location("me")

        var resp = location.toString()
        val lfile = File(getFilesDir(), "LOCATION.txt")
        lfile.createNewFile()

        lfile.forEachLine {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            {
                resp = it + ";"+ DateTimeFormatter.ISO_INSTANT.format(Instant.now()).toString() + "\n" + resp
            }
            else
            {
                resp = it + ";nil" + "\n" + resp
            }
        }

        val lfilewriter = FileWriter(lfile)

        val lout = BufferedWriter(lfilewriter)
        lout.write(resp)
        lout.close()
    }
}
