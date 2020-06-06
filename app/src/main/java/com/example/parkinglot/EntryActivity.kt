package com.example.parkinglot

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.parkinglot.login.ui.login.LoginActivity


class EntryActivity : AppCompatActivity() {

    private var PRIVATE_MODE = 0
    private val PREF_NAME = "alreadylogged"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sharedPref: SharedPreferences = getSharedPreferences(PREF_NAME, PRIVATE_MODE)
        //Log.d("myTag", sharedPref.getBoolean(PREF_NAME, false).toString())
        if (sharedPref.getBoolean(PREF_NAME, true)) { //si no se ha logeado lo lleva a la pantalla de log
            val homeIntent = Intent(this, LoginActivity::class.java)
            startActivity(homeIntent)
            finish()
        } else{ //si si, lo lleva al principal
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }// NOTE - Place logic here to determine which screen to show next
    // Default is used in this demo code

}