package com.example.parkinglot.register.ui.register

import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.*
import androidx.annotation.StringRes
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.appcompat.app.AppCompatActivity
import com.example.parkinglot.MainActivity
import com.example.parkinglot.R
import com.example.parkinglot.register.ui.register.LoggedInUserView
import com.example.parkinglot.login.ui.login.LoginActivity
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import java.util.*


class RegisterActivity : AppCompatActivity() {


    private lateinit var loginViewModel: LoginViewModel
    private var PRIVATE_MODE = 0
    private val PREF_NAME = "alreadylogged"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_register)

        val etname = findViewById<View>(R.id.etname) as EditText
        val etapellido = findViewById<View>(R.id.etapellido) as EditText
        val ettelefono = findViewById<View>(R.id.etnumber) as EditText
        val etusername = findViewById<View>(R.id.etusername) as EditText
        val etpassword = findViewById<View>(R.id.etpassword) as EditText

        val btnregister = findViewById<View>(R.id.btnregister) as Button
        val tvlogin = findViewById<View>(R.id.tvlogin) as TextView

        val loading = findViewById<ProgressBar>(R.id.loadingreg)

        loginViewModel = ViewModelProviders.of(this, LoginViewModelFactory())
            .get(com.example.parkinglot.register.ui.register.LoginViewModel::class.java)


        loginViewModel.loginFormState.observe(this@RegisterActivity, Observer {
            val loginState = it ?: return@Observer

            // disable login button unless both username / password is valid
            btnregister.isEnabled = loginState.isDataValid

            if (loginState.usernameError != null) {
                etusername.error = getString(loginState.usernameError)
            }
            if (loginState.passwordError != null) {
                etpassword.error = getString(loginState.passwordError)
            }
            if (loginState.phoneError != null) {
                ettelefono.error = getString(loginState.phoneError)
            }
            if (loginState.firstError != null) {
                etname.error = getString(loginState.firstError)
            }
            if (loginState.lastError != null) {
                etapellido.error = getString(loginState.lastError)
            }
        })

        loginViewModel.loginResult.observe(this@RegisterActivity, Observer {
            val loginResult = it ?: return@Observer

            val username =  etusername.text.toString()
            val password =etpassword.text.toString()
            val phone = ettelefono.text.toString().toInt()
            val firstname =etname.text.toString()
            val lastname =etapellido.text.toString()

            loading.visibility = View.VISIBLE

            val db = FirebaseFirestore.getInstance()
            // [END get_firestore_instance]
            // [START set_firestore_settings]
            val settings = FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build()
            db.firestoreSettings = settings

            db.collection("users")
                .whereEqualTo("username", username)
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        Log.d("loggeandoxd", "${document.id} => ${document.data}")
                    }

                    if(documents.isEmpty){

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
                                updateUiWithUser(username)
                                setResult(Activity.RESULT_OK)

                                //Complete and destroy login activity once successful
                                finish()

                            }
                            .addOnFailureListener { e ->
                                Log.w("Register", "Error writing document", e)
                                //no se pudo escribir
                            }



                    }
                    else{
                        Log.d("loggeandoxd", "no existe el user")
                        showLoginFailed("Ya hay un usuario registrado con ese correo")
                        loading.visibility = View.GONE
                    }

                }
                .addOnFailureListener { exception ->
                    Log.w(ContentValues.TAG, "Error getting documents: ", exception)
                }


        })

        etusername.afterTextChanged {
            loginViewModel.loginDataChanged(
                etusername.text.toString(),
                etpassword.text.toString(),
                ettelefono.text.toString(),
                etname.text.toString(),
                etapellido.text.toString()

            )
        }

        etpassword.apply {
            afterTextChanged {
                loginViewModel.loginDataChanged(
                    etusername.text.toString(),
                    etpassword.text.toString(),
                    ettelefono.text.toString(),
                    etname.text.toString(),
                    etapellido.text.toString()
                )
            }

            ettelefono.afterTextChanged {
                loginViewModel.loginDataChanged(
                    etusername.text.toString(),
                    etpassword.text.toString(),
                    ettelefono.text.toString(),
                    etname.text.toString(),
                    etapellido.text.toString()

                )
            }

            etname.afterTextChanged {
                loginViewModel.loginDataChanged(
                    etusername.text.toString(),
                    etpassword.text.toString(),
                    ettelefono.text.toString(),
                    etname.text.toString(),
                    etapellido.text.toString()

                )
            }

            etapellido.afterTextChanged {
                loginViewModel.loginDataChanged(
                    etusername.text.toString(),
                    etpassword.text.toString(),
                    ettelefono.text.toString(),
                    etname.text.toString(),
                    etapellido.text.toString()

                )
            }

            setOnEditorActionListener { _, actionId, _ ->
                when (actionId) {
                    EditorInfo.IME_ACTION_DONE ->
                        loginViewModel.login(
                            etusername.text.toString(),
                            etpassword.text.toString(),
                            ettelefono.text.toString().toInt(),
                            etname.text.toString(),
                            etapellido.text.toString()
                        )
                }
                false
            }

            btnregister.setOnClickListener {
                loading.visibility = View.VISIBLE
                loginViewModel.login(etusername.text.toString(),
                    etpassword.text.toString(),
                    ettelefono.text.toString().toInt(),
                    etname.text.toString(),
                    etapellido.text.toString())
            }

            tvlogin.setOnClickListener {

                val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
                startActivity(intent)
            }
        }

        }


    private fun updateUiWithUser(us: String) {




        val sharedPref: SharedPreferences = getSharedPreferences(PREF_NAME, PRIVATE_MODE)
        val editor = sharedPref.edit()
        editor.putBoolean(PREF_NAME, false)
        editor.putString("username",us)
        editor.apply()

        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun showLoginFailed(errorString: String) {
        Toast.makeText(applicationContext, errorString, Toast.LENGTH_SHORT).show()
    }
}

/**
 * Extension function to simplify setting an afterTextChanged action to EditText components.
 */
fun EditText.afterTextChanged(afterTextChanged: (String) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(editable: Editable?) {
            afterTextChanged.invoke(editable.toString())
        }

        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
    })
}

