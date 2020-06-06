package com.example.parkinglot.login.ui.login

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
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.parkinglot.MainActivity
import com.example.parkinglot.R
import com.example.parkinglot.register.ui.register.RegisterActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings


class LoginActivity : AppCompatActivity() {

    private lateinit var loginViewModel: LoginViewModel
    private var PRIVATE_MODE = 0
    private val PREF_NAME = "alreadylogged"
    // [START declare_auth]
    private lateinit var auth: FirebaseAuth
    // [END declare_auth]


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_login)

        val username = findViewById<EditText>(R.id.username)
        val password = findViewById<EditText>(R.id.password)
        val login = findViewById<Button>(R.id.login)
        val register = findViewById<Button>(R.id.login2)
        val loading = findViewById<ProgressBar>(R.id.loading)

        // [START initialize_auth]
        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()
        // [END initialize_auth]

        loginViewModel = ViewModelProviders.of(this, LoginViewModelFactory())
            .get(LoginViewModel::class.java)

        loginViewModel.loginFormState.observe(this@LoginActivity, Observer {
            val loginState = it ?: return@Observer

            // disable login button unless both username / password is valid
            login.isEnabled = loginState.isDataValid

            if (loginState.usernameError != null) {
                username.error = getString(loginState.usernameError)
            }
            if (loginState.passwordError != null) {
                password.error = getString(loginState.passwordError)
            }
        })

        loginViewModel.loginResult.observe(this@LoginActivity, Observer {
            val loginResult = it ?: return@Observer

            val us = username.text.toString()
            val pas = password.text.toString()

            loading.visibility = View.VISIBLE

            auth.signInWithEmailAndPassword(us, pas)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d("logeo", "signInWithEmail:success")
                        val user = auth.currentUser

                        val db = FirebaseFirestore.getInstance()
                        // [END get_firestore_instance]
                        // [START set_firestore_settings]
                        val settings = FirebaseFirestoreSettings.Builder()
                            .setPersistenceEnabled(true)
                            .build()
                        db.firestoreSettings = settings
                        // [END set_firestore_settings]
                        // TODO: handle loggedInUser authentication
                        db.collection("users")
                            .whereEqualTo("username", us)
                            .whereEqualTo("password", pas)
                            .get()
                            .addOnSuccessListener { documents ->
                                for (document in documents) {
                                    Log.d("loggeandoxd", "${document.id} => ${document.data}")
                                }

                                if(documents.isEmpty){
                                    Log.d("loggeandoxd", "no existe el user")
                                    showLoginFailed("Usuario y/o contraseña no válida")
                                    loading.visibility = View.GONE
                                }
                                else{
                                    updateUiWithUser(us)
                                    setResult(Activity.RESULT_OK)

                                    //Complete and destroy login activity once successful
                                    finish()
                                }

                            }
                            .addOnFailureListener { exception ->
                                Log.w(ContentValues.TAG, "Error getting documents: ", exception)
                            }

                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w("logeo", "signInWithEmail:failure", task.exception)
                        showLoginFailed("Usuario y/o contraseña no válida")
                        loading.visibility = View.GONE
                    }

                    // ...
                }




        })

        username.afterTextChanged {
            loginViewModel.loginDataChanged(
                username.text.toString(),
                password.text.toString()
            )
        }

        password.apply {
            afterTextChanged {
                loginViewModel.loginDataChanged(
                    username.text.toString(),
                    password.text.toString()
                )
            }

            setOnEditorActionListener { _, actionId, _ ->
                when (actionId) {
                    EditorInfo.IME_ACTION_DONE ->
                        loginViewModel.login(
                            username.text.toString(),
                            password.text.toString()
                        )
                }
                false
            }

            login.setOnClickListener {
                loading.visibility = View.VISIBLE
                loginViewModel.login(username.text.toString(), password.text.toString())
            }

            register.setOnClickListener {

                val intent = Intent(this@LoginActivity,
                    RegisterActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }

    private fun updateUiWithUser(userna: String) {
        val welcome = getString(R.string.welcome)
        val displayName = userna

        val sharedPref: SharedPreferences = getSharedPreferences(PREF_NAME, PRIVATE_MODE)
        val editor = sharedPref.edit()
        editor.putBoolean(PREF_NAME, false)
        editor.putString("username",userna)
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
