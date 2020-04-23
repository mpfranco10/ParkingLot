package com.example.parkinglot.register.ui.register

import android.text.TextUtils
import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.parkinglot.register.data.LoginRepository
import com.example.parkinglot.register.data.Result

import com.example.parkinglot.R
import java.util.regex.Pattern

class LoginViewModel(private val loginRepository: LoginRepository) : ViewModel() {

    private val _loginForm = MutableLiveData<LoginFormState>()
    val loginFormState: LiveData<LoginFormState> = _loginForm

    private val _loginResult = MutableLiveData<LoginResult>()
    val loginResult: LiveData<LoginResult> = _loginResult

    fun login(username: String, password: String, phone: Int, firstn: String, lastn: String) {
        // can be launched in a separate asynchronous job
        val result = loginRepository.login(username, password, phone, firstn, lastn)

        if (result is Result.Success) {
            _loginResult.value =
                LoginResult(success = LoggedInUserView(displayName = result.data.displayName))
        } else {
            _loginResult.value = LoginResult(error = R.string.login_failed)
        }
    }

    fun loginDataChanged(username: String, password: String, phone: String, firstn: String, lastn: String) {
        if (!isUserNameValid(username)) {
            _loginForm.value = LoginFormState(usernameError = R.string.invalid_username)
        } else if (!isPasswordValid(password)) {
            _loginForm.value = LoginFormState(passwordError = R.string.invalid_password)
        } else if (!isPhoneValid(phone)) {
            _loginForm.value = LoginFormState(phoneError = R.string.invalid_phone)
        }else if (!isNameValid(firstn)) {
            _loginForm.value = LoginFormState(firstError = R.string.invalid_fn)
        }else if (!isLastNameValid(lastn)) {
            _loginForm.value = LoginFormState(lastError = R.string.invalid_ln)
        }else {
            _loginForm.value = LoginFormState(isDataValid = true)
        }
    }

    // A placeholder username validation check
    private fun isUserNameValid(username: String): Boolean {
        return !TextUtils.isEmpty(username) && Patterns.EMAIL_ADDRESS.matcher(username).matches()

    }

    // A placeholder password validation check
    private fun isPasswordValid(password: String): Boolean {
        return password.length > 5
    }

    private fun isPhoneValid(phone: String): Boolean {
        return phone.isNotBlank()
    }

    private fun isNameValid(name: String): Boolean {
        return name.isNotBlank()
    }

    private fun isLastNameValid(name: String): Boolean {
        return name.isNotBlank()
    }
}
