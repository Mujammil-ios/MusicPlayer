package com.example.musicplayer

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions

class NotificationReceiver:BroadcastReceiver() {
    override fun onReceive(p0: Context?, p1: Intent?) {
        when(p1?.action)
        {
            ApplicationClass.PREVOUS -> preNextSong(increment = false, context = p0!!)
            ApplicationClass.PLAY -> if (PlayerActivity.isPlaying) pauseMusic() else playMusic()
            ApplicationClass.NEXT -> preNextSong(increment = true, context = p0!!)
            ApplicationClass.EXIT ->{
               exitApplication()
            }
        }
    }

    private fun playMusic()
    {
        PlayerActivity.isPlaying = true
        PlayerActivity.musicService!!.mediaPlayer!!.start()
        PlayerActivity.musicService!!.showNotification(R.drawable.pause_icon)
        PlayerActivity.binding.playpauseBtn.setIconResource(R.drawable.pause_icon)
        NowPlaying.binding.playpauBtnNP.setIconResource(R.drawable.pause_icon)
    }

    private fun pauseMusic()
    {
        PlayerActivity.isPlaying = false
        PlayerActivity.musicService!!.mediaPlayer!!.pause()
        PlayerActivity.musicService!!.showNotification(R.drawable.play_icon)
        PlayerActivity.binding.playpauseBtn.setIconResource(R.drawable.play_icon)
        NowPlaying.binding.playpauBtnNP.setIconResource(R.drawable.play_icon)
    }

    private fun preNextSong(increment: Boolean,context: Context){
        setSongPotion(increment = increment)
        PlayerActivity.musicService!!.createMediaplayer()

        Glide.with(context).load(PlayerActivity.musicListPA[PlayerActivity.songPotion].artUri).apply(
            RequestOptions().placeholder(R.drawable.music).centerCrop())
            .into(PlayerActivity.binding.songImaPA)

        PlayerActivity.binding.sondnamePA.text = PlayerActivity.musicListPA[PlayerActivity.songPotion].title
        Glide.with(context).load(PlayerActivity.musicListPA[PlayerActivity.songPotion].artUri).apply(
            RequestOptions().placeholder(R.drawable.music).centerCrop())
            .into(NowPlaying.binding.songImgNP)
        NowPlaying.binding.songNameNP.text = PlayerActivity.musicListPA[PlayerActivity.songPotion].title
        playMusic()


        PlayerActivity.fIndex = favouriteChecker(PlayerActivity.musicListPA[PlayerActivity.songPotion].id)

        if (PlayerActivity.isfavourite) {
            PlayerActivity.binding.favourteBtnPA.setImageResource(R.drawable.favorite)
        } else {
            PlayerActivity.binding.favourteBtnPA.setImageResource(R.drawable.favourte_empty_icon)
        }
    }
}