package com.example.musicplayer

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.musicplayer.databinding.ActivityPlaylistDetailBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.GsonBuilder

class PlaylistDetail : AppCompatActivity() {
    lateinit var binding :ActivityPlaylistDetailBinding
    lateinit var adapter:MusicAdapter

    companion object{
        var currentPlaylistpos: Int = 0
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.coolPink)
        binding = ActivityPlaylistDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        PlaylistActivity.musicPlaylist.ref[currentPlaylistpos].playlist = checkPlaylist(Playlist = PlaylistActivity.musicPlaylist.ref[currentPlaylistpos].playlist)


        currentPlaylistpos = intent.extras?.get("index") as Int


        binding.playlistDetailRV.setItemViewCacheSize(10)
        binding.playlistDetailRV.setHasFixedSize(true)
        binding.playlistDetailRV.layoutManager = LinearLayoutManager(this)

        adapter = MusicAdapter(this,PlaylistActivity.musicPlaylist.ref[currentPlaylistpos].playlist,playlistDetails = true)
        binding.playlistDetailRV.adapter = adapter

        binding.backBtnPD.setOnClickListener { finish() }

        binding.shuffleBtnPD.setOnClickListener {
            val intent = Intent(this,PlayerActivity::class.java)
            intent.putExtra("index", 0)
            intent.putExtra("class","playlistDetailShuffle")
            startActivity(intent)
            Toast.makeText(this,"Shuffle", Toast.LENGTH_SHORT).show()
        }
        binding.addBtnPD.setOnClickListener {
            startActivity(Intent(this,SelectionActivity::class.java))
        }
        binding.removebtnPD.setOnClickListener {
            val builder = MaterialAlertDialogBuilder(this)
            builder.setTitle("Remove")
                .setMessage("Do you want to remove all song for playlist?")
                .setPositiveButton("yes"){dialog,_->
                    PlaylistActivity.musicPlaylist.ref[currentPlaylistpos].playlist.clear()
                    adapter.refreshPlaylist()
                    dialog.dismiss()
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

    override fun onResume() {
        super.onResume()

        binding.playlistNamePD.text = PlaylistActivity.musicPlaylist.ref[currentPlaylistpos].name
        binding.moreInfoPD.text = "Total ${adapter.itemCount} Song.\n\n" + "Created On: ${PlaylistActivity.musicPlaylist.ref[currentPlaylistpos].createOn}\n\n"+
                " -- ${PlaylistActivity.musicPlaylist.ref[currentPlaylistpos].createdBy}"
        if (adapter.itemCount>0)
        {
            Glide.with(this).load(PlaylistActivity.musicPlaylist.ref[currentPlaylistpos].playlist[0].artUri).apply(RequestOptions().placeholder(R.drawable.music).centerCrop())
                .into(binding.playlistImgPD)
            binding.shuffleBtnPD.visibility = View.VISIBLE

        }

        adapter.notifyDataSetChanged()

        val editor = getSharedPreferences("FAVOURTES", MODE_PRIVATE).edit()




        val jsonStringPlaylist= GsonBuilder().create().toJson(PlaylistActivity.musicPlaylist)
        editor.putString("MusicPlaylist",jsonStringPlaylist)


        editor.apply()



    }
}