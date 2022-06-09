package com.example.musicplayer

import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.example.musicplayer.databinding.ActivityPlaylistBinding
import com.example.musicplayer.databinding.AddPlaylistDialogBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.text.SimpleDateFormat
import java.util.*

class PlaylistActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPlaylistBinding
    private lateinit var adapter: PlaylistViewAdapter

    companion object{
        var musicPlaylist: MusicPlaylist = MusicPlaylist()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.coolPink)
        binding = ActivityPlaylistBinding.inflate(layoutInflater)
        setContentView(binding.root)




        binding.playlistRV.setHasFixedSize(true)
        binding.playlistRV.setItemViewCacheSize(13)
        binding.playlistRV.layoutManager = GridLayoutManager(this,2)
        adapter = PlaylistViewAdapter(this, playlistList = musicPlaylist.ref)
        binding.playlistRV.adapter = adapter

        binding.backBtnPla.setOnClickListener { finish() }
        binding.addPlaylistBtn.setOnClickListener { customAlertDialog() }
    }

    private fun customAlertDialog()
    {
        val customDialog = LayoutInflater.from(this@PlaylistActivity).inflate(R.layout.add_playlist_dialog,binding.root,false)
        val binder =AddPlaylistDialogBinding.bind(customDialog)
        val builder = MaterialAlertDialogBuilder(this@PlaylistActivity)
        builder.setView(customDialog)
            .setTitle("Playlist Details")
            .setPositiveButton("ADD") { dialog,_->
                val playlistName =  binder.playlistNameDL.text
                val createBy = binder.yourNameDL.text
                if (playlistName != null && createBy != null)
                    if (playlistName.isNotEmpty() && createBy.isNotEmpty())
                    {
                        addPlaylist(playlistName.toString(),createBy.toString())
                    }

                dialog.dismiss()
            }.show()

        }

        private fun addPlaylist(name: String, createBy: String)
        {
            var playlistExists = false
                for (i in musicPlaylist.ref)
                {
                     if (name.equals(i.name))
                    {
                    playlistExists = true
                        break
                 }
              }

        if (playlistExists) Toast.makeText(this,"Playlist Exist!!",Toast.LENGTH_SHORT).show()
        else{
            val tempPlaylist = Playlist()
            tempPlaylist.name = name
            tempPlaylist.playlist = ArrayList()
            tempPlaylist.createdBy = createBy
            val calender = Calendar.getInstance().time
            val sdf = SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH)
            tempPlaylist.createOn = sdf.format(calender)
            musicPlaylist.ref.add(tempPlaylist)
            adapter.refreshPlaylist()
        }
    }

    override fun onResume() {
        super.onResume()
        adapter.notifyDataSetChanged()
    }
}