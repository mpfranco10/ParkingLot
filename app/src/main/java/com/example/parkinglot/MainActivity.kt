package com.example.parkinglot

import android.content.Context
import android.location.Location
import android.net.ConnectivityManager
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

        val data = File(getFilesDir(), "MAIN.txt")

        if(!data.exists())
        {
            data.createNewFile()

            var datos = "parqueadero1"
            datos = datos + "\n" + "parqueadero2"
            datos = datos + "\n" + "parqueadero2"
            datos = datos + "\n" + "parqueadero3"
            datos = datos + "\n" + "parqueadero4"
            datos = datos + "\n" + "parqueadero5"
            datos = datos + "\n" + "parqueadero6"
            datos = datos + "\n" + "parqueadero7"
            datos = datos + "\n" + "parqueadero8"
            datos = datos + "\n" + "parqueadero9"
            datos = datos + "\n" + "parqueadero10"
            datos = datos + "\n" + "parqueadero12"

            val myDataWriter = FileWriter(data)

            val dataOut = BufferedWriter(myDataWriter)
            dataOut.write(datos)
            dataOut.close()
        }
    }

    fun verifyAvailableNetwork(activity:AppCompatActivity):Boolean
    {
        val connectivityManager=activity.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo=connectivityManager.activeNetworkInfo
        return  networkInfo!=null && networkInfo.isConnected
    }
}
