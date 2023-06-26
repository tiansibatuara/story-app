package com.example.storyapp.ui.login

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.example.storyapp.data.source.local.ApiConfig
import com.example.storyapp.ui.MainActivity
import com.example.storyapp.data.response.LoginResponse
import com.example.storyapp.databinding.ActivityLoginBinding
import com.example.storyapp.models.AuthViewModel
import com.example.storyapp.ui.register.RegisterActivity
import com.example.storyapp.utils.UserModel
import com.example.storyapp.utils.UserPreferences
import com.example.storyapp.utils.UserSharedPreferences
import com.example.storyapp.utils.ViewModelFactory
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class LoginActivity : AppCompatActivity() {

    private  lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        binding.btnLogin.setOnClickListener {
            val email = binding.edLoginEmail.text.toString()
            val password = binding.edLoginPassword.text.toString()

            login(email, password)
        }

        binding.btnSignup.setOnClickListener {
            finish()
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
        binding.ibBack.setOnClickListener {
            finish()
        }
    }

    private fun login(email : String, password: String) {
        showLoading(true)
        val client = ApiConfig.getApiService().login(email,password)
        client.enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                val responseBody = response.body()
                if (response.isSuccessful && responseBody != null) {
                    val pref = UserPreferences.getInstance(dataStore)
                    val authViewModel = ViewModelProvider(this@LoginActivity, ViewModelFactory(pref)).get(
                        AuthViewModel::class.java
                    )

                    authViewModel.saveAuthSetting(responseBody.loginResult.name!!, responseBody.loginResult.userId!!, responseBody.loginResult.token!!)

                    val userSharedPreference = UserSharedPreferences(application)
                    val user = UserModel(responseBody.loginResult.name!!, responseBody.loginResult.token!!,true)
                    userSharedPreference.setUser(user)

                    showLoading(false)
                    Toast.makeText(this@LoginActivity, "Login Success!", Toast.LENGTH_SHORT).show()

                    val intent = Intent(this@LoginActivity, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(intent)
                    finish()
                } else {

                    showLoading(false)
                    Toast.makeText(this@LoginActivity, "Invalid credentials!", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                showLoading(false)
                Toast.makeText(this@LoginActivity, t.message.toString(), Toast.LENGTH_SHORT).show()
            }

        })
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.GONE
        }
    }
}