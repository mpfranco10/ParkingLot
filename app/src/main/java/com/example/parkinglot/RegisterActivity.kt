package com.example.parkinglot

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.parkinglot.login.ui.login.LoginActivity
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings

import java.io.IOException
import java.util.*


class RegisterActivity : AppCompatActivity() {

    private var etname: EditText? = null
    private var etapellido:EditText? = null
    private var ettelefono:EditText? = null
    private var etusername:EditText? = null
    private var etpassword: EditText? = null
    private var btnregister: Button? = null
    private var tvlogin: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_register)

        etname = findViewById<View>(R.id.etname) as EditText
        etapellido = findViewById<View>(R.id.etapellido) as EditText
        ettelefono = findViewById<View>(R.id.etnumber) as EditText
        etusername = findViewById<View>(R.id.etusername) as EditText
        etpassword = findViewById<View>(R.id.etpassword) as EditText

        btnregister = findViewById<View>(R.id.btnregister) as Button
        tvlogin = findViewById<View>(R.id.tvlogin) as TextView

        btnregister!!.isEnabled = etname!=null && etapellido!=null && ettelefono!=null && etusername!=null && etpassword!=null

        tvlogin!!.setOnClickListener {
            val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
            startActivity(intent)
        }

        btnregister!!.setOnClickListener {
            try {
                register()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        }

    private fun register() {
        try {
            btnregister!!.isEnabled = false
            btnregister!!.setBackgroundColor(resources.getColor(R.color.colorAccent))

            val db = FirebaseFirestore.getInstance()
            // [END get_firestore_instance]

            var perfil = ""

            // [START set_firestore_settings]
            val settings = FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build()
            db.firestoreSettings = settings

            val name = etname!!.text.toString()
            val ape = etapellido!!.text.toString()
            val tel = ettelefono!!.text.toString().toInt()
            val us =  etusername!!.text.toString()
            val pw = etpassword!!.text.toString()

            val docData = hashMapOf(
                "firstname" to name,
                "lastname" to ape,
                "phone_number" to tel,
                "birthday" to Timestamp(Date()),
                "gender" to 0,
                "saldo" to 10000,
                "username" to us,
                "password" to pw
            )

            db.collection("users").document(us)
                .set(docData)
                .addOnSuccessListener { Log.d("Register", "DocumentSnapshot successfully written!")

                    val intent = Intent(this@RegisterActivity, MainActivity::class.java)
                    startActivity(intent)
                }
                .addOnFailureListener { e -> Log.w("Register", "Error writing document", e)
                    btnregister!!.isEnabled = true
                }
        } catch (e: Exception) {

        } finally {

        }

    }



    }

