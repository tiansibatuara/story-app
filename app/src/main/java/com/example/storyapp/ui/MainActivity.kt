package com.example.storyapp.ui

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.storyapp.adapter.StoryAdapter
import com.example.storyapp.data.response.ListStoryItem
import com.example.storyapp.databinding.ActivityMainBinding
import com.example.storyapp.models.AuthViewModel
import com.example.storyapp.models.MainViewModel
import com.example.storyapp.LandingActivity
import com.example.storyapp.ui.story.StoryActivity
import com.example.storyapp.utils.UserPreferences
import com.example.storyapp.utils.ViewModelFactory

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        val mainViewModel = ViewModelProvider(
            this,
            ViewModelProvider.NewInstanceFactory()
        )[MainViewModel::class.java]

        val pref = UserPreferences.getInstance(dataStore)

        val authViewModel = ViewModelProvider(this,
            ViewModelFactory(pref))[AuthViewModel::class.java]

        authViewModel.getAuthSettings().observe(this) {
            if (it.token.isEmpty()) {
                val intent = Intent(this, LandingActivity::class.java)
                startActivity(intent)
                finish()
            }
        }

        authViewModel.getAuthSettings().observe(this) { authData ->
            mainViewModel.findStories(authData.token)
            mainViewModel.listStory.observe(this) {
                setStoryData(it)
            }
        }

        mainViewModel.isLoading.observe(this) {
            showLoading(it)
        }

        val layoutManager = LinearLayoutManager(this)
        binding.rvStory.layoutManager = layoutManager

        binding.fab.setOnClickListener {
            val storyIntent = Intent(this, StoryActivity::class.java)
            startActivity(storyIntent)
        }

        binding.actionLogout.setOnClickListener {
            AlertDialog.Builder(this).apply {
                setTitle("Logout")
                setMessage("Are you sure you want to logout?")
                setPositiveButton("Yes") { _, _ ->
                    authViewModel.removeAuthSetting()
                }
                setNegativeButton("No") { dialog, _ ->
                    dialog.cancel()
                }
                create()
                show()
            }
        }

        val onBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                finishAffinity()
            }
        }
        onBackPressedDispatcher.addCallback(this, onBackPressedCallback)
    }

    private fun setStoryData(listStory : List<ListStoryItem>) {
        val adapter = StoryAdapter(listStory)
        binding.rvStory.adapter = adapter
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}
