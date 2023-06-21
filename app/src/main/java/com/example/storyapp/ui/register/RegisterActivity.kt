package com.example.storyapp.ui.register

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.example.storyapp.data.source.local.ApiConfig
import com.example.storyapp.ui.login.LoginActivity
import com.example.storyapp.data.response.RegisterResponse
import com.example.storyapp.databinding.ActivityRegisterBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        binding.btnSignup.setOnClickListener {
            val name = binding.edRegisterName.text.toString()
            val email = binding.edRegisterEmail.text.toString()
            val password = binding.edRegisterPassword.text.toString()

            register(name, email, password)
        }

        binding.btnLogin.setOnClickListener {
            finish()
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
        binding.ibBack.setOnClickListener {
            finish()
        }
    }

    private fun register(name: String, email: String, password: String) {
        if (name.isEmpty() || email.isEmpty() || password.isEmpty() || binding.edRegisterEmail.error != null || binding.edRegisterName.error != null || binding.edRegisterPassword.error != null) {
            Toast.makeText(this@RegisterActivity, "Invalid credentials!", Toast.LENGTH_SHORT)
                .show()
        } else {
            showLoading(true)
            val client = ApiConfig.getApiService().register(name, email, password)
            client.enqueue(object : Callback<RegisterResponse> {
                override fun onResponse(
                    call: Call<RegisterResponse>,
                    response: Response<RegisterResponse>
                ) {
                    val responseBody = response.body()
                    if (response.isSuccessful && responseBody != null && !responseBody.error) {
                        Log.e(TAG, responseBody.toString())
                        val intent = Intent(this@RegisterActivity,
                            LoginActivity::class.java)
                        startActivity(intent)
                        finish()
                        Toast.makeText(this@RegisterActivity, "Account created!", Toast.LENGTH_SHORT)
                            .show()
                    } else {
                        showLoading(false)
                        Toast.makeText(
                            this@RegisterActivity,
                            response.message(),
                            Toast.LENGTH_SHORT
                        )
                            .show()
                        Log.e(TAG, "onFailure: ${response.message()}")
                    }
                }

                override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                    showLoading(false)
                    Toast.makeText(this@RegisterActivity, t.message.toString(), Toast.LENGTH_SHORT)
                        .show()
                    Log.e(TAG, t.message.toString())
                }
            })
        }
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.GONE
        }
    }


    companion object {
        private const val TAG = "RegisterActivity"
    }
}