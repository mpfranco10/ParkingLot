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
import com.google.android.gms.tasks.Tasks
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import java.lang.Exception

import java.util.*
import java.util.concurrent.TimeUnit

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
class LoginDataSource {

    fun login(username: String, password: String, phone: Long, firstname: String, lastname: String): Result<LoggedInUser> {

        try {
            val fakeUser = LoggedInUser(java.util.UUID.randomUUID().toString(), firstname)
            return Result.Success(fakeUser)
        } catch (e: Throwable) {
            return Result.Error(IOException("Ya existe un usuario con ese correo", e))
        }

        return Result.Error(IOException("Registro no exitoso :("))
    }

    fun logout() {
        // TODO: revoke authentication
    }
}

