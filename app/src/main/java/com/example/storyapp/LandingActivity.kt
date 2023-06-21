package com.example.storyapp

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.example.storyapp.ui.MainActivity
import com.example.storyapp.databinding.ActivityLandingBinding
import com.example.storyapp.models.AuthViewModel
import com.example.storyapp.ui.login.LoginActivity
import com.example.storyapp.ui.register.RegisterActivity
import com.example.storyapp.utils.UserPreferences
import com.example.storyapp.utils.ViewModelFactory


private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class LandingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLandingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLandingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        val pref = UserPreferences.getInstance(dataStore)
        val authViewModel = ViewModelProvider(this@LandingActivity,
            ViewModelFactory(pref))[AuthViewModel::class.java]

        authViewModel.getAuthSettings()
            .observe(this) {
            if (it.token.isNotEmpty()) {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        }

        binding.btnLogin
            .setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        binding.btnSignup
            .setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }
}