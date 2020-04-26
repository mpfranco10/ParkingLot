package com.example.parkinglot.login.data

import android.widget.Toast
import com.example.parkinglot.MainActivity
import com.example.parkinglot.login.data.model.LoggedInUser
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter

/**
 * Class that requests authentication and user information from the remote data source and
 * maintains an in-memory cache of login status and user credentials information.
 */

class LoginRepository(val dataSource: LoginDataSource) {

    // in-memory cache of the loggedInUser object

    var database = FirebaseFirestore.getInstance()

    var user: LoggedInUser? = null
        private set

    val isLoggedIn: Boolean
        get() = user != null

    init {
        // If user credentials will be cached in local storage, it is recommended it be encrypted
        // @see https://developer.android.com/training/articles/keystore
        user = null
    }

    fun logout() {
        user = null
        dataSource.logout()
    }

    fun login(username: String, password: String): Result<LoggedInUser> {
        // handle login

        val docRef = database.collection("users").document(username)
        docRef.get().addOnCompleteListener(OnCompleteListener<DocumentSnapshot> { task ->

            if (task.isSuccessful) {

                val document = task.result?.toString()

                if (!document?.contains("doc=null")!!) {

                    var str = task.result?.data.toString()
                    var indx = str.indexOf("password=")+9
                    str = str.substring(indx)
                    indx = str.indexOf(",");
                    str = str.substring(0,indx)

                    if(str.equals(password))
                    {
                        val lfile4 = File("/data/user/0/com.example.parkinglot/files", "USERS.txt")
                        lfile4.createNewFile()
                        val lfilewriter = FileWriter(lfile4)
                        val lout = BufferedWriter(lfilewriter)
                        lout.write(username+";"+password)
                        lout.close()
                    }
                    else
                    {
                        val lfile4 = File("/data/user/0/com.example.parkinglot/files", "USERS.txt")
                        lfile4.createNewFile()
                        val lfilewriter = FileWriter(lfile4)
                        val lout = BufferedWriter(lfilewriter)
                        lout.write("EMPTY"+";"+"EMPTY")
                        lout.close()
                    }
                }
                else
                {
                    val lfile4 = File("/data/user/0/com.example.parkinglot/files", "USERS.txt")
                    lfile4.createNewFile()
                    val lfilewriter = FileWriter(lfile4)
                    val lout = BufferedWriter(lfilewriter)
                    lout.write("EMPTY"+";"+"EMPTY")
                    lout.close()
                }
            }
        })

        val result = dataSource.login(username, password)

        if (result is Result.Success) {
            setLoggedInUser(result.data)
        }

        return result
    }

    private fun setLoggedInUser(loggedInUser: LoggedInUser) {
        this.user = loggedInUser
        // If user credentials will be cached in local storage, it is recommended it be encrypted
        // @see https://developer.android.com/training/articles/keystore
    }
}
