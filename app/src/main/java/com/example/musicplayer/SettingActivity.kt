package com.example.musicplayer

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.musicplayer.databinding.ActivitySettingBinding

class SettingActivity : AppCompatActivity() {
    lateinit var binding: ActivitySettingBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.coolPinkNav)
        binding = ActivitySettingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = "Settings"
    }
}