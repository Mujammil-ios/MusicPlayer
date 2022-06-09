package com.example.musicplayer

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.example.musicplayer.databinding.ActivityFavourteBinding

class FavourteActivity : AppCompatActivity() {

    private  lateinit var binding: ActivityFavourteBinding
    private lateinit var adapter: FavouriteAdapter

    companion object{
        var favouriteSongs:ArrayList<Music> = ArrayList()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.coolPink)
        binding = ActivityFavourteBinding.inflate(layoutInflater)
        setContentView(binding.root)
        favouriteSongs = checkPlaylist(favouriteSongs)

        //favourte song

        binding.backBtnFA.setOnClickListener { finish() }
        binding.favourteRV.setHasFixedSize(true)
        binding.favourteRV.setItemViewCacheSize(13)
        binding.favourteRV.layoutManager = GridLayoutManager(this,4)
        adapter = FavouriteAdapter(this, favouriteSongs)
        binding.favourteRV.adapter = adapter


        //--------shffal button show when song is 0 or 1----------

        if(favouriteSongs.size<1) binding.suffalBtnFA.visibility = View.INVISIBLE
        binding.suffalBtnFA.setOnClickListener {
            val intent = Intent(this,PlayerActivity::class.java)
            intent.putExtra("index", 0)
            intent.putExtra("class","FavouriteShuffle")
            startActivity(intent)

        }


    }
}