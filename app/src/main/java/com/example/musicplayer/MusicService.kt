package com.example.musicplayer

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Binder
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.support.v4.media.session.MediaSessionCompat
import androidx.core.app.NotificationCompat

class MusicService: Service(), AudioManager.OnAudioFocusChangeListener{

    private var myBinder =MyBinder()
    var mediaPlayer:MediaPlayer? = null
    private lateinit var mediaSession : MediaSessionCompat
    private lateinit var runnable: Runnable
    lateinit var audioManager: AudioManager

    override fun onBind(p0: Intent?): IBinder? {
        mediaSession = MediaSessionCompat(baseContext,"My Music")
        return myBinder
    }
    inner class MyBinder:Binder()
    {
        fun currentService():MusicService
        {
            return this@MusicService
        }
    }

    @SuppressLint("WrongConstant")
    fun showNotification(playPauseBtn:Int){

        //--------------------this is code for notification to PlayerActivity-------------------------->
        val fullScreenIntent = Intent(baseContext, MainActivity::class.java)
        fullScreenIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or PendingIntent.FLAG_IMMUTABLE
        val fullScreenPendingIntent = PendingIntent.getActivity(this, 0,
            fullScreenIntent, PendingIntent.FLAG_IMMUTABLE)





        val prevIntent = Intent(baseContext,NotificationReceiver::class.java).setAction(ApplicationClass.PREVOUS)
        val prePendingIntent = PendingIntent.getBroadcast(baseContext,0,prevIntent,PendingIntent.FLAG_MUTABLE)

        val playIntent = Intent(baseContext,NotificationReceiver::class.java).setAction(ApplicationClass.PLAY)
        val playPendingIntent = PendingIntent.getBroadcast(baseContext,0,playIntent,PendingIntent.FLAG_MUTABLE)

        val nextIntent = Intent(baseContext,NotificationReceiver::class.java).setAction(ApplicationClass.NEXT)
        val nextPendingIntent = PendingIntent.getBroadcast(baseContext,0,nextIntent,PendingIntent.FLAG_MUTABLE)

        val exitIntent = Intent(baseContext,NotificationReceiver::class.java).setAction(ApplicationClass.EXIT)
        val exitPendingIntent = PendingIntent.getBroadcast(baseContext,0,exitIntent,PendingIntent.FLAG_MUTABLE)

        val imgArt = getImgArt(PlayerActivity.musicListPA[PlayerActivity.songPotion].path)
        val image = if (imgArt != null)
        {BitmapFactory.decodeByteArray(imgArt,0,imgArt.size)}
        else
        {
            BitmapFactory.decodeResource(resources,R.drawable.splash_screen)
        }



        val notification = NotificationCompat.Builder(baseContext,ApplicationClass.CHANNEL_IC)
            .setContentIntent(fullScreenPendingIntent)
            .setContentTitle(PlayerActivity.musicListPA[PlayerActivity.songPotion].title)
            .setContentText(PlayerActivity.musicListPA[PlayerActivity.songPotion].artist)
            .setSmallIcon(R.drawable.playlist)
            .setLargeIcon(image)
            .setStyle(androidx.media.app.NotificationCompat.MediaStyle().setMediaSession(mediaSession.sessionToken))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setOnlyAlertOnce(true)
            .addAction(R.drawable.back_icon,"Pervious",prePendingIntent)
            .addAction(playPauseBtn,"Play",playPendingIntent)
            .addAction(R.drawable.previous_icon,"Play",nextPendingIntent)
            .addAction(R.drawable.exit_icon,"Play",exitPendingIntent)
            .build()

        startForeground(13,notification)


    }

    fun createMediaplayer(){

        try {
            if (PlayerActivity.musicService!!.mediaPlayer == null) PlayerActivity.musicService!!.mediaPlayer = MediaPlayer()
            PlayerActivity.musicService!!.mediaPlayer!!.reset()
            PlayerActivity.musicService!!.mediaPlayer!!.setDataSource(PlayerActivity.musicListPA[PlayerActivity.songPotion].path)
            PlayerActivity.musicService!!.mediaPlayer!!.prepare()

            PlayerActivity.binding.playpauseBtn.setIconResource(R.drawable.pause_icon)
            PlayerActivity.musicService!!.showNotification(R.drawable.pause_icon)
            PlayerActivity.binding.tvSeekbarStart.text = formatDuration(mediaPlayer!!.currentPosition.toLong())
            PlayerActivity.binding.tvSeekbarEnd.text = formatDuration(mediaPlayer!!.duration.toLong())
            PlayerActivity.binding.seekBarPA.progress = 0
            PlayerActivity.binding.seekBarPA.max = mediaPlayer!!.duration
            PlayerActivity.nowPlayingId = PlayerActivity.musicListPA[PlayerActivity.songPotion].id

        }catch (e: Exception)
        {
            return
        }
    }
    fun seeBarSetup(){
        runnable = Runnable {
            PlayerActivity.binding.tvSeekbarStart.text = formatDuration(mediaPlayer!!.currentPosition.toLong())

            PlayerActivity.binding.seekBarPA.progress = mediaPlayer!!.currentPosition
            Handler(Looper.getMainLooper()).postDelayed(runnable,200)
        }
        Handler(Looper.getMainLooper()).postDelayed(runnable,0)

    }

    override fun onAudioFocusChange(focusChange: Int) {
      if(focusChange <= 0)
      {
          PlayerActivity.binding.playpauseBtn.setIconResource(R.drawable.play_icon)
          NowPlaying.binding.playpauBtnNP.setIconResource(R.drawable.play_icon)
            showNotification(R.drawable.play_icon)
          PlayerActivity.isPlaying = false
            mediaPlayer!!.pause()

      }else{
          PlayerActivity.binding.playpauseBtn.setIconResource(R.drawable.pause_icon)
          NowPlaying.binding.playpauBtnNP.setIconResource(R.drawable.pause_icon)
          showNotification(R.drawable.pause_icon)
          PlayerActivity.isPlaying = true
            mediaPlayer!!.start()
      }
    }
}