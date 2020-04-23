package com.example.parkinglot.register.data

import android.content.ContentValues
import android.content.ContentValues.TAG
import android.content.Intent
import android.util.Log
import androidx.core.content.ContextCompat.startActivity
import com.example.parkinglot.MainActivity
import com.example.parkinglot.register.data.model.LoggedInUser
import java.io.IOException
import com.example.parkinglot.register.ui.register.LoginViewModelFactory
import com.example.parkinglot.register.ui.register.RegisterActivity
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings

import java.util.*

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
class LoginDataSource {

    fun login(username: String, password: String, phone: Int, firstname: String, lastname: String): Result<LoggedInUser> {
        try {


                    val db = FirebaseFirestore.getInstance()
                    // [END get_firestore_instance]
                    // [START set_firestore_settings]
                    val settings = FirebaseFirestoreSettings.Builder()
                        .setPersistenceEnabled(true)
                        .build()
                    db.firestoreSettings = settings

            if(exists(db,username) ==false) {
                val docData = hashMapOf(
                    "firstname" to firstname,
                    "lastname" to lastname,
                    "phone_number" to phone,
                    "birthday" to Timestamp(Date()),
                    "gender" to 0,
                    "saldo" to 10000,
                    "username" to username,
                    "password" to password
                )

                db.collection("users").document(username)
                    .set(docData)
                    .addOnSuccessListener {
                        Log.d("Register", "DocumentSnapshot successfully written!")

                    }
                    .addOnFailureListener { e ->
                        Log.w("Register", "Error writing document", e)
                        //no se pudo escribir
                    }

                val fakeUser = LoggedInUser(java.util.UUID.randomUUID().toString(), firstname)
                return Result.Success(fakeUser)
                // TODO: handle loggedInUser authentication
            }

        } catch (e: Throwable) {
            return Result.Error(IOException("Ya existe un usuario con ese correo", e))
        }

        return Result.Error(IOException("Registro no exitoso :("))
    }

    fun exists(db:FirebaseFirestore ,username:String): Boolean{

        var r = false
        val docRef = db.collection("users").document(username)
        docRef.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    Log.d("existe", "DocumentSnapshot data: ${document.data}")
                    if(document.data == null){
                        Log.d("existe", "No existe el usuario:")
                        r = true
                    }
                   else{
                        r = false
                    }
                } else {
                    Log.d("existe", "No such document")

                }
            }
            .addOnFailureListener { exception ->
                Log.d("existe", "get failed with ", exception)
                r = true
            }
        return r

    }

    fun logout() {
        // TODO: revoke authentication
    }
}

