package com.example.storyapp.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.example.storyapp.databinding.ActivityDetailBinding


class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding

    companion object {
        const val EXTRA_STORY = "extra_story"
        const val EXTRA_IMAGE = "extra_image"
        const val EXTRA_USERNAME = "extra_username"
        const val EXTRA_DESCRIPTION = "extra_description"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val name = intent.getStringExtra(EXTRA_USERNAME)
        val description = intent.getStringExtra(EXTRA_DESCRIPTION)
        val photoUrl = intent.getStringExtra(EXTRA_IMAGE)

        supportActionBar?.hide()

        binding.apply {
            Glide.with(this@DetailActivity)
                .load(photoUrl)
                .centerCrop()
                .into(ivDetail)
            tvDetailDesc.text = description
            tvDetailName.text = name
            ibBack.setOnClickListener {
                finishAfterTransition()
            }
        }


    }
}