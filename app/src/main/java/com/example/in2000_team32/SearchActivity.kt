package com.example.in2000_team32

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.in2000_team32.databinding.ActivitySearchBinding

class SearchActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySearchBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }
}