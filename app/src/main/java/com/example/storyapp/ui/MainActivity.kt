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
import com.example.storyapp.adapter.ListStoriesAdapter
import com.example.storyapp.adapter.LoadingStateAdapter
import com.example.storyapp.models.PagingViewModel
import com.example.storyapp.ui.story.StoryActivity
import com.example.storyapp.utils.UserModel
import com.example.storyapp.utils.UserPreferences
import com.example.storyapp.utils.UserSharedPreferences
import com.example.storyapp.utils.ViewModelFactory

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var mUserPreference: UserSharedPreferences
    private lateinit var userData: UserModel
    private lateinit var pagingViewModel: PagingViewModel
    private val adapter: ListStoriesAdapter by lazy { ListStoriesAdapter() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

//        val mainViewModel = ViewModelProvider(
//            this,
//            ViewModelProvider.NewInstanceFactory()
//        )[MainViewModel::class.java]

        val mainViewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get(
            MainViewModel::class.java)

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
        }

        mUserPreference = UserSharedPreferences(this)
        userData = mUserPreference.getUser()

        pagingViewModel = ViewModelProvider(
            this,
            ViewModelFactory(
                application = application,
                context = this,
                token = userData.token )
        )[PagingViewModel::class.java]

        binding.apply {
            rvStory.layoutManager = LinearLayoutManager(this@MainActivity)
            rvStory.adapter = adapter.withLoadStateFooter(
                footer = LoadingStateAdapter {
                    adapter.retry()
                }
            )
        }

        pagingViewModel.stories.observe(this) {
            adapter.submitData(lifecycle,it)
        }

        adapter.setOnItemClickCallback(object : ListStoriesAdapter.OnItemClickCallback {
            override fun onItemClicked(data: ListStoryItem) {
                Intent(this@MainActivity, DetailActivity::class.java).also {
                    it.putExtra(DetailActivity.EXTRA_IMAGE, data.photoUrl)
                    it.putExtra(DetailActivity.EXTRA_USERNAME, data.name)
                    it.putExtra(DetailActivity.EXTRA_DESCRIPTION, data.description)
                    startActivity(it)
                }
            }
        })

        mainViewModel.isLoading.observe(this) {
            showLoading(it)
        }

        val layoutManager = LinearLayoutManager(this)
        binding.rvStory.layoutManager = layoutManager

        binding.fab.setOnClickListener {
            val storyIntent = Intent(this, StoryActivity::class.java)
            startActivity(storyIntent)
            finish()
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

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}
