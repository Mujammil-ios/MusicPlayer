package com.example.musicplayer

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.musicplayer.databinding.ActivityAboutBinding

class AboutActivity : AppCompatActivity() {
    lateinit var binding: ActivityAboutBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.coolPinkNav)
        binding = ActivityAboutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        this.supportActionBar?.title = "About"



        binding.aboutText.text = aboutText()
    }
    private fun aboutText(): String{
        return "Developed BY: Pinjara Mujammil" + "\n\n If you want to provide FeedBack,I will love to hear that"
    }
}