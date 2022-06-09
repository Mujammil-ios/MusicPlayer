package com.example.musicplayer

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.musicplayer.databinding.FragmentNowPlayingBinding

class NowPlaying : Fragment() {


    companion object
    {
        lateinit var binding: FragmentNowPlayingBinding
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,avedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_now_playing, container, false)
        binding = FragmentNowPlayingBinding.bind(view)
        binding.root.visibility = View.INVISIBLE
        binding.playpauBtnNP.setOnClickListener {
            if (PlayerActivity.isPlaying) pauseMusic() else playMusic()
        }

        binding.nextBtnNP.setOnClickListener {

            setSongPotion(increment = true)
            PlayerActivity.musicService!!.createMediaplayer()

            Glide.with(this).load(PlayerActivity.musicListPA[PlayerActivity.songPotion].artUri).apply(
                RequestOptions().placeholder(R.drawable.music).centerCrop())
                .into(NowPlaying.binding.songImgNP)
           binding.songNameNP.text = PlayerActivity.musicListPA[PlayerActivity.songPotion].title
            PlayerActivity.musicService!!.showNotification(R.drawable.pause_icon)
            playMusic()
        }
        binding.root.setOnClickListener {
            val intent = Intent(requireContext(),PlayerActivity::class.java)
            intent.putExtra("index",PlayerActivity.songPotion)
            intent.putExtra("class","NowPlaying")

            ContextCompat.startActivities(requireContext(), arrayOf(intent),null)
        }
        return view
    }

    override fun onResume() {
        super.onResume()
        if (PlayerActivity.musicService!=null)
        {
            binding.root.visibility = View.VISIBLE
            binding.songNameNP.isSelected = true
            Glide.with(this).load(PlayerActivity.musicListPA[PlayerActivity.songPotion].artUri).apply(
                RequestOptions().placeholder(R.drawable.music).centerCrop())
                .into(binding.songImgNP)
            binding.songNameNP.text = PlayerActivity.musicListPA[PlayerActivity.songPotion].title
            if (PlayerActivity.isPlaying) binding.playpauBtnNP.setIconResource(R.drawable.pause_icon)
            else binding.playpauBtnNP.setIconResource(R.drawable.play_icon)
        }
    }

    private fun playMusic()
    {
        PlayerActivity.musicService!!.mediaPlayer!!.start()
        binding.playpauBtnNP.setIconResource(R.drawable.pause_icon)
        PlayerActivity.musicService!!.showNotification(R.drawable.pause_icon)
        PlayerActivity.binding.nextBtnPA.setIconResource(R.drawable.pause_icon)
        PlayerActivity.isPlaying = true
    }
    private fun pauseMusic()
    {
        PlayerActivity.musicService!!.mediaPlayer!!.pause()
        binding.playpauBtnNP.setIconResource(R.drawable.play_icon)
        PlayerActivity.musicService!!.showNotification(R.drawable.play_icon)
        PlayerActivity.binding.nextBtnPA.setIconResource(R.drawable.play_icon)
        PlayerActivity.isPlaying = false
    }



}