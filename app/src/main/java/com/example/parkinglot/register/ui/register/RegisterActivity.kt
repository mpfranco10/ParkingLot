package com.example.parkinglot.register.ui.register

import android.app.Activity
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

            loading.visibility = View.GONE
            if (loginResult.error != null) {
                showLoginFailed(loginResult.error)
            }
            if (loginResult.success != null) {
                updateUiWithUser(loginResult.success)
            }
            setResult(Activity.RESULT_OK)

            //Complete and destroy login activity once successful

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


    private fun updateUiWithUser(model: LoggedInUserView) {


        val sharedPref: SharedPreferences = getSharedPreferences(PREF_NAME, PRIVATE_MODE)
        val editor = sharedPref.edit()
        editor.putBoolean(PREF_NAME, false)
        editor.apply()

        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun showLoginFailed(@StringRes errorString: Int) {
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

