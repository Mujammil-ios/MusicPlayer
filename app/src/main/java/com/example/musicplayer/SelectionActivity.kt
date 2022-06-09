package com.example.musicplayer

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.musicplayer.databinding.ActivitySelectionBinding

class SelectionActivity : AppCompatActivity() {

    private  lateinit var binding: ActivitySelectionBinding
    private lateinit var adapter: MusicAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.coolPink)
        binding = ActivitySelectionBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.selectionSARV.setHasFixedSize(true)
        binding.selectionSARV.setItemViewCacheSize(13)
        binding.selectionSARV.layoutManager = LinearLayoutManager(this)

        adapter = MusicAdapter(this,MainActivity.MusicListMA, selectionActivity = true)
        binding.selectionSARV.adapter = adapter

        binding.backBtnSA.setOnClickListener { finish() }



        //for searchview
        binding.searchViewSA.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(p0: String?): Boolean =  true

            override fun onQueryTextChange(p0: String?): Boolean {
                MainActivity.musicListSearch =ArrayList()
                if (p0!=null)
                {
                    val userInput = p0.lowercase()
                    for (song in MainActivity.MusicListMA)
                        if (song.title.lowercase().contains(userInput))
                            MainActivity.musicListSearch.add(song)
                    MainActivity.search = true
                    adapter.updateMusicList(searchList = MainActivity.musicListSearch)
                }
                return true
            }
        })
    }
}