package com.example.parkinglot

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.location.Location
import android.net.ConnectivityManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        fixGoogleMapBug()
        setContentView(R.layout.activity_main)

        var database = FirebaseFirestore.getInstance()

        val navView: BottomNavigationView = findViewById(R.id.nav_view)

        val navController = findNavController(R.id.nav_host_fragment)

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerview)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(setOf(
                R.id.navigation_home, R.id.navigation_dashboard,  R.id.navigation_parkinglots, R.id.navigation_notifications))
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        val lfile = File(getFilesDir(), "LOCATION.txt")
        val lfile2 = File(getFilesDir(), "PENDIENTEPARQUEADERO.txt")
        println (getFilesDir().toString())
        val lfile4 = File(getFilesDir(), "PARQUEADERO.txt")

        val connectivityManager=this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo=connectivityManager.activeNetworkInfo

        if(networkInfo!=null && networkInfo.isConnected)
        {
            var location = Location("me")
            var resp = location.toString()

            if(!lfile4.exists())
            {
                lfile4.createNewFile()
                val lfilewriter = FileWriter(lfile4)
                val lout = BufferedWriter(lfilewriter)
                lout.write("EMPTY"+";"+"EMPTY")
                lout.close()
            }

            if(lfile2.exists())
            {
                lfile2.forEachLine {
                    var id = it.split(";")[0]
                    var time = it.split(";")[1]

                    val docRef = database.collection("malls").document(id.toString())

                    docRef.get().addOnCompleteListener(OnCompleteListener<DocumentSnapshot> { task ->
                        if (task.isSuccessful)
                        {
                            val document = task.result?.toString()

                            if (document?.contains("doc=null")!!)
                            {
                                Toast.makeText(this,"Se recibió el código!" + id, Toast.LENGTH_SHORT).show();
                                val lfile3 = File(getFilesDir(), "PARQUEADERO.txt")
                                lfile3.createNewFile()
                                val lfilewriter = FileWriter(lfile3)
                                val lout = BufferedWriter(lfilewriter)
                                lout.write(id.toString()+";"+ time.toString())
                                lout.close()
                            }
                            else
                            {
                                Toast.makeText(this,"No existe el parqueadero: " + id, Toast.LENGTH_SHORT).show();
                            }
                        }
                        else
                        {
                            Log.d(ContentValues.TAG, "get failed with ", task.exception)
                        }
                    })
                }
            }

            lfile.createNewFile()

            resp += ";" + java.util.Calendar.getInstance().timeInMillis.toString()

            val lfilewriter = FileWriter(lfile)
            val lout = BufferedWriter(lfilewriter)
            lout.write(resp)
            lout.close()
        }
    }
    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */


    private fun fixGoogleMapBug() {
        val googleBug: SharedPreferences = getSharedPreferences("google_bug", Context.MODE_PRIVATE)
        if (!googleBug.contains("fixed")) {
            val corruptedZoomTables = File(getFilesDir(), "ZoomTables.data")
            corruptedZoomTables.delete()
            googleBug.edit().putBoolean("fixed", true).apply()
        }
    }

    fun escanear(view: View) {}
}
