package com.example.musicplayer

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.graphics.Color
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.audiofx.AudioEffect
import android.net.Uri
import android.os.Bundle
import android.os.IBinder
import android.widget.LinearLayout
import android.widget.SeekBar
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.musicplayer.databinding.ActivityPlayerBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class PlayerActivity : AppCompatActivity(),ServiceConnection,MediaPlayer.OnCompletionListener {

    companion object{
        lateinit var musicListPA : ArrayList<Music>
        var songPotion: Int = 0
       // var mediaPlayer: MediaPlayer? = null
        var isPlaying:Boolean = false
        var musicService: MusicService? = null
        lateinit var binding: ActivityPlayerBinding
        var repeat:Boolean =false
        var min15:Boolean = false
        var min30:Boolean = false
        var min60:Boolean = false
        var nowPlayingId : String = ""
        var isfavourite: Boolean = true
        var fIndex:Int = -1

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.coolPink)
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)



        initializaLayout()
        binding.backBtnPA.setOnClickListener { finish() }

        binding.playpauseBtn.setOnClickListener {
            if(isPlaying)
            {
                pauseMusic()
            }
            else
            {
                playMusic()
            }
        }

        binding.priousBtnPA.setOnClickListener {prevNextSong(increment = false)  }
        binding.nextBtnPA.setOnClickListener { prevNextSong(increment = true) }
        binding.seekBarPA.setOnSeekBarChangeListener(object :SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(p0: SeekBar?, p1: Int, cont: Boolean) {
                if (cont) musicService!!.mediaPlayer!!.seekTo(p1)
            }

            override fun onStartTrackingTouch(p0: SeekBar?) = Unit

            override fun onStopTrackingTouch(p0: SeekBar?) = Unit
        })
        binding.repectbtnPA.setOnClickListener {
            if(!repeat)
            {
                repeat =true
                binding.repectbtnPA.setColorFilter(ContextCompat.getColor(this,R.color.purple_500))
            }else{
                repeat = false
                binding.repectbtnPA.setColorFilter(ContextCompat.getColor(this,R.color.cool_pink))

            }
        }

        binding.eqlizerBtnPA.setOnClickListener {
           try {
               val eqIntent = Intent(AudioEffect.ACTION_DISPLAY_AUDIO_EFFECT_CONTROL_PANEL)
               eqIntent.putExtra(AudioEffect.EXTRA_AUDIO_SESSION, musicService!!.mediaPlayer!!.audioSessionId)
               eqIntent.putExtra(AudioEffect.EXTRA_PACKAGE_NAME,baseContext.packageName)
               eqIntent.putExtra(AudioEffect.EXTRA_CONTENT_TYPE,AudioEffect.CONTENT_TYPE_MUSIC)
               startActivityForResult(eqIntent,13)
           }catch (e: Exception)
           {
               Toast.makeText(this,"eqlizer not support your device",Toast.LENGTH_SHORT).show()
           }
        }
        binding.timerBtnPA.setOnClickListener {
                val timer = min15 || min30 || min60
                if (!timer)
                {
                    showBottomSheetDialog()
                }
                else{

                    val builder = MaterialAlertDialogBuilder(this)
                    builder.setTitle("Stop timer")
                        .setMessage("Do you want to stop timer?")
                        .setPositiveButton("yes"){_,_->
                            min15 = false
                            min30 = false
                            min60 = false
                            binding.timerBtnPA.setColorFilter(ContextCompat.getColor(this,
                                R.color.RED
                            ))
                        }
                        .setNegativeButton("No")
                        {
                                dialog,_ ->
                            dialog.dismiss()
                        }
                    val customDialog = builder.create()
                    customDialog.show()
                    customDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.RED)
                    customDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.RED)

                }
        }

        binding.shareBtnPA.setOnClickListener {
            val shareIntent = Intent()
            shareIntent.action = Intent.ACTION_SEND
            shareIntent.type = "audio/*"
            shareIntent.putExtra(Intent.EXTRA_STREAM,Uri.parse(musicListPA[songPotion].path))
            startActivity(Intent.createChooser(shareIntent,"Sharing Music File!!"))
        }

        binding.favourteBtnPA.setOnClickListener {
            if (isfavourite) {
                isfavourite = false
                binding.favourteBtnPA.setImageResource(R.drawable.favourte_empty_icon)
                FavourteActivity.favouriteSongs.removeAt(fIndex)
            }
            else{
                isfavourite = true
                binding.favourteBtnPA.setImageResource(R.drawable.favorite)
                FavourteActivity.favouriteSongs.add(musicListPA[songPotion])

            }


        }



    }

    private fun setLayout(){
        fIndex = favouriteChecker(musicListPA[songPotion].id)
        Glide.with(this).load(musicListPA[songPotion].artUri).apply(RequestOptions().placeholder(R.drawable.music).centerCrop())
            .into(binding.songImaPA)

        binding.sondnamePA.text = musicListPA[songPotion].title

        if (repeat) binding.repectbtnPA.setColorFilter(ContextCompat.getColor(this,R.color.purple_500))

        if (min15 || min30 || min60)
            binding.timerBtnPA.setColorFilter(ContextCompat.getColor(this,R.color.purple_500))

        if (isfavourite)
        {
            binding.favourteBtnPA.setImageResource(R.drawable.favorite)
        } else
        {
            binding.favourteBtnPA.setImageResource(R.drawable.favourte_empty_icon)
        }

    }

    private fun createMediaplayer(){

        try {
            if (musicService!!.mediaPlayer == null) musicService!!.mediaPlayer = MediaPlayer()
            musicService!!.mediaPlayer!!.reset()
            musicService!!.mediaPlayer!!.setDataSource(musicListPA[songPotion].path)
            musicService!!.mediaPlayer!!.prepare()
            musicService!!.mediaPlayer!!.start()
            isPlaying = true
            binding.playpauseBtn.setIconResource(R.drawable.pause_icon)
            musicService!!.showNotification(R.drawable.pause_icon)
            binding.tvSeekbarStart.text = formatDuration(musicService!!.mediaPlayer!!.currentPosition.toLong())
            binding.tvSeekbarEnd.text = formatDuration(musicService!!.mediaPlayer!!.duration.toLong())
            binding.seekBarPA.progress = 0
            binding.seekBarPA.max = musicService!!.mediaPlayer!!.duration
            musicService!!.mediaPlayer!!.setOnCompletionListener(this)
            nowPlayingId = musicListPA[songPotion].id

        }catch (e: Exception)
        {
            return
        }
    }

    private fun initializaLayout(){

        songPotion = intent.getIntExtra("index",0)
        when(intent.getStringExtra("class"))
        {
            "FavouriteAdapter"->
            {
                val intent = Intent(this,MusicService::class.java)
                bindService(intent,this, BIND_AUTO_CREATE)
                startService(intent)
                musicListPA = ArrayList()
                musicListPA.addAll(FavourteActivity.favouriteSongs)
                setLayout()

            }
            "NowPlaying"->
            {

                binding.tvSeekbarStart.text = formatDuration(musicService!!.mediaPlayer!!.currentPosition.toLong())
                binding.tvSeekbarEnd.text = formatDuration(musicService!!.mediaPlayer!!.duration.toLong())
                binding.seekBarPA.progress = musicService!!.mediaPlayer!!.currentPosition
                binding.seekBarPA.max = musicService!!.mediaPlayer!!.duration
                if (isPlaying) binding.playpauseBtn.setIconResource(R.drawable.pause_icon)
                else binding.playpauseBtn.setIconResource(R.drawable.play_icon)
                setLayout()
            }
            "MusicAdapterSearch"->{
                val intent = Intent(this,MusicService::class.java)
                bindService(intent,this, BIND_AUTO_CREATE)
                startService(intent)
                musicListPA = ArrayList()
                musicListPA.addAll(MainActivity.musicListSearch)
                setLayout()
            }
            "MusicAdapter" -> {
                val intent = Intent(this,MusicService::class.java)
                bindService(intent,this, BIND_AUTO_CREATE)
                startService(intent)
                musicListPA = ArrayList()
                musicListPA.addAll(MainActivity.MusicListMA)

                setLayout()

            }
            "MainActivity" ->{
                val intent = Intent(this,MusicService::class.java)
                bindService(intent,this, BIND_AUTO_CREATE)
                startService(intent)
                musicListPA = ArrayList()
                musicListPA.addAll(MainActivity.MusicListMA)
                musicListPA.shuffle()
                setLayout()

            }
            "FavouriteShuffle"->
            {
                val intent = Intent(this,MusicService::class.java)
                bindService(intent,this, BIND_AUTO_CREATE)
                startService(intent)
                musicListPA = ArrayList()
                musicListPA.addAll(FavourteActivity.favouriteSongs)
                musicListPA.shuffle()
                setLayout()

            }
            "PlaylistDetailAdapter" ->
            {
                val intent = Intent(this,MusicService::class.java)
                bindService(intent,this, BIND_AUTO_CREATE)
                startService(intent)
                musicListPA = ArrayList()
                musicListPA.addAll(FavourteActivity.favouriteSongs)
                musicListPA.addAll(PlaylistActivity.musicPlaylist.ref[PlaylistDetail.currentPlaylistpos].playlist)
                setLayout()
            }

            "playlistDetailShuffle" ->
            {
                val intent = Intent(this,MusicService::class.java)
                bindService(intent,this, BIND_AUTO_CREATE)
                startService(intent)
                musicListPA = ArrayList()
                musicListPA.addAll(FavourteActivity.favouriteSongs)
                musicListPA.addAll(PlaylistActivity.musicPlaylist.ref[PlaylistDetail.currentPlaylistpos].playlist)
                musicListPA.shuffle()
                setLayout()

            }
        }

    }

    private fun playMusic(){
        binding.playpauseBtn.setIconResource(R.drawable.pause_icon)
        musicService!!.showNotification(R.drawable.pause_icon)
        isPlaying = true
        musicService!!.mediaPlayer!!.start()
    }
    private fun pauseMusic(){
        binding.playpauseBtn.setIconResource(R.drawable.play_icon)
        musicService!!.showNotification(R.drawable.play_icon)
        isPlaying = false
        musicService!!.mediaPlayer!!.pause()
    }

    private fun prevNextSong(increment:Boolean){
        if (increment)
        {
            setSongPotion(increment = true)
            setLayout()
            createMediaplayer()
        }
        else{
            setSongPotion(increment = false)
            setLayout()
            createMediaplayer()
        }
    }



    @SuppressLint("ServiceCast")
    override fun onServiceConnected(p0: ComponentName?, p1: IBinder?) {
        val binder = p1 as MusicService.MyBinder
        musicService = binder.currentService()
        createMediaplayer()
        musicService!!.seeBarSetup()
        musicService!!.audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        musicService!!.audioManager.requestAudioFocus(musicService,AudioManager.STREAM_MUSIC,AudioManager.AUDIOFOCUS_GAIN)


    }

    override fun onServiceDisconnected(p0: ComponentName?) {
        musicService = null
    }

    override fun onCompletion(p0: MediaPlayer?) {
        setSongPotion(increment = true)
        createMediaplayer()
        try {
            setLayout()
        }catch (e:Exception){return}

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 13 || resultCode == RESULT_OK)
            return
    }


    private fun showBottomSheetDialog()
    {
        val dialog = BottomSheetDialog(this)
        dialog.setContentView(R.layout.bottom_sheet_dialog)
        dialog.show()
        dialog.findViewById<LinearLayout>(R.id.min_15)?.setOnClickListener {
            Toast.makeText(this,"Music will stop after 15 min",Toast.LENGTH_SHORT).show()
            binding.timerBtnPA.setColorFilter(ContextCompat.getColor(this,R.color.purple_200))
            min15 = true
            Thread{Thread.sleep((15*6000).toLong())
            if (min15) exitApplication()}.start()
            dialog.dismiss()
        }
        dialog.findViewById<LinearLayout>(R.id.min_30)?.setOnClickListener {
            Toast.makeText(this,"Music will stop after 30 min",Toast.LENGTH_SHORT).show()
            binding.timerBtnPA.setColorFilter(ContextCompat.getColor(this,R.color.purple_200))
            min30 = true
            Thread{Thread.sleep((30*6000).toLong())
                if (min30) exitApplication()}.start()
            dialog.dismiss()
        }
        dialog.findViewById<LinearLayout>(R.id.min_60)?.setOnClickListener {
            Toast.makeText(this,"Music will stop after 60 min",Toast.LENGTH_SHORT).show()
            binding.timerBtnPA.setColorFilter(ContextCompat.getColor(this,R.color.purple_200))
            min60 = true
            Thread{Thread.sleep((60*6000).toLong())
                if (min60) exitApplication()}.start()
            dialog.dismiss()
        }
    }
}