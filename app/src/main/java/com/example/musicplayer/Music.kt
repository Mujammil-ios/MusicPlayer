package com.example.musicplayer

import android.media.MediaMetadataRetriever
import java.io.File
import java.util.concurrent.TimeUnit
import kotlin.system.exitProcess

data class Music(val id:String,val title:String,val artist:String,
                 val duration:Long=0, val path: String,val albums:String,
                 val artUri:String)
class Playlist{
    lateinit var name:String
    lateinit var playlist:ArrayList<Music>
    lateinit var createdBy: String
    lateinit var createOn: String
}

class MusicPlaylist{
    var ref :ArrayList<Playlist> = ArrayList()
}



fun formatDuration(duration: Long):String
{
    val minutes = TimeUnit.MINUTES.convert(duration, TimeUnit.MILLISECONDS)
    val seconds = (TimeUnit.SECONDS.convert(duration, TimeUnit.MILLISECONDS)-
            minutes*TimeUnit.SECONDS.convert(1, TimeUnit.MINUTES))

    return String.format("%02d:%02d",minutes,seconds)
}

fun getImgArt(path: String): ByteArray? {
    val retriever = MediaMetadataRetriever()
    retriever.setDataSource(path)
    return retriever.embeddedPicture
}

fun setSongPotion(increment: Boolean)
{
   if (!PlayerActivity.repeat)
   {
       if (increment)
       {
           if (PlayerActivity.musicListPA.size - 1 == PlayerActivity.songPotion)
               PlayerActivity.songPotion = 0
           else ++PlayerActivity.songPotion
       }
       else{
           if (0 == PlayerActivity.songPotion)
               PlayerActivity.songPotion = PlayerActivity.musicListPA.size-1
           else --PlayerActivity.songPotion
       }
   }
}

fun exitApplication()
{
    if (PlayerActivity.musicService !=null){
        PlayerActivity.musicService!!.audioManager.abandonAudioFocus(PlayerActivity.musicService)
        PlayerActivity.musicService!!.stopForeground(true)
        PlayerActivity.musicService!!.mediaPlayer!!.release()
        PlayerActivity.musicService =null
        exitProcess(2)

    }
}

fun favouriteChecker(id: String):Int
{
    PlayerActivity.isfavourite = false
    FavourteActivity.favouriteSongs.forEachIndexed { index, music ->
        if (id == music.id)
        {
            PlayerActivity.isfavourite = true
            return index
        }
    }
    return -1
}

fun checkPlaylist(Playlist: ArrayList<Music>):ArrayList<Music>
{
    Playlist.forEachIndexed { index, music ->
        val file = File(music.path)
        if (!file.exists()) {
            Playlist.removeAt(index)
        }


    }
    return Playlist
}
