package com.example.musicplayer

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.Color.RED
import android.hardware.camera2.params.MeteringRectangle
import android.hardware.camera2.params.RggbChannelVector.RED
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.Menu
import android.view.MenuItem
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.musicplayer.databinding.ActivityMainBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import java.io.File
import kotlin.system.exitProcess

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var musicAdapter: MusicAdapter

    companion object{
        lateinit var MusicListMA : ArrayList<Music>
        lateinit var musicListSearch : ArrayList<Music>
        var search:Boolean = false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setTheme(R.style.coolPinkNav)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        //for nav drov
        toggle = ActionBarDrawerToggle(this,binding.root,R.string.open,R.string.close)
        binding.root.addDrawerListener(toggle)
        toggle.syncState()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        if (requestRuntimePermission())
        {
            initialzeLayout()


            FavourteActivity.favouriteSongs = ArrayList()
            val editor = getSharedPreferences("FAVOURTES", MODE_PRIVATE)
            val jsonString = editor.getString("favouriteSongs",null)
            val typeToken = object : TypeToken<ArrayList<Music>>(){}.type
            if (jsonString != null)
            {
                val data: ArrayList<Music> = GsonBuilder().create().fromJson(jsonString,typeToken)
                FavourteActivity.favouriteSongs.addAll(data)
            }


            PlaylistActivity.musicPlaylist = MusicPlaylist()

            val jsonStringPlaylist = editor.getString("MusicPlaylist",null)

            if (jsonStringPlaylist != null)
            {
                val dataPlaylist: MusicPlaylist = GsonBuilder().create().fromJson(jsonStringPlaylist,MusicPlaylist::class.java)
                PlaylistActivity.musicPlaylist = dataPlaylist
            }


        }


        binding.shuffleBtn.setOnClickListener {
            try{
                val intent = Intent(this,PlayerActivity::class.java)

                intent.putExtra("index", 0)
                intent.putExtra("class","MainActivity")
                startActivity(intent)


            }catch (e: Exception)
            {
                Toast.makeText(this,"Sorry not song",Toast.LENGTH_SHORT).show()
            }


        }

        binding.favourteBtn.setOnClickListener {
            startActivity(Intent(this,FavourteActivity::class.java))
            Toast.makeText(this,"Favourite",Toast.LENGTH_SHORT).show()
        }




        binding.playlist.setOnClickListener {
            startActivity(Intent(this,PlaylistActivity::class.java))
            Toast.makeText(this,"Shuffle",Toast.LENGTH_SHORT).show()
        }



        //------------------------------------------navigation Iteam work-------------------------------------------//

        binding.navView.setNavigationItemSelectedListener {
            when(it.itemId)
            {
                R.id.navFeedback -> startActivity(Intent(this,FeedbackActivity::class.java))
                R.id.navSettings -> startActivity(Intent(this,SettingActivity::class.java))
                R.id.navAbout-> startActivity(Intent(this,AboutActivity::class.java))
                R.id.navExit ->
                {
                    //alirt box
                    val builder = MaterialAlertDialogBuilder(this)
                    builder.setTitle("Exit")
                        .setMessage("Do you want to close app?")
                        .setPositiveButton("yes"){_,_->
                            exitProcess(0)
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
            true
        }


    }


    //------------------------------------for requesting permission---------------------------------------------

    private fun requestRuntimePermission() : Boolean {
        if(ActivityCompat.checkSelfPermission(this,android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE),13)
            return false
        }

        return true
    }

    //---------------------------------override the permission _______________________________________________//

    override fun onRequestPermissionsResult(requestCode: Int,permissions: Array<out String>,grantResults: IntArray)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == 13){
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                Toast.makeText(this,"permission grandete",Toast.LENGTH_SHORT).show()
                MusicListMA = getAllAudio()
                initialzeLayout()
            }
            else{
                ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE),13)
            }
        }
    }

    //---------------------------------overrite the function because open drawer ** open drawer perpose------------------------------------

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(toggle.onOptionsItemSelected(item))
            return true
        return super.onOptionsItemSelected(item)
    }

    //-------------------------------------------initianlazeLayout-------------------------------------------->
    private fun initialzeLayout()
    {

        search = false

        MusicListMA = getAllAudio()

        binding.musicRv.setHasFixedSize(true)
        binding.musicRv.setItemViewCacheSize(13)
        binding.musicRv.layoutManager = LinearLayoutManager(this)
        musicAdapter = MusicAdapter(this, MusicListMA)
        binding.musicRv.adapter = musicAdapter
        binding.totalsong.text = "Total Songs: " + musicAdapter.itemCount
    }

    //----------------------------------------------get all song-------------------------------------------------------->>>>

    @SuppressLint("Range")
    private fun getAllAudio():ArrayList<Music>{
        val tempList = ArrayList<Music>()
        val selection = MediaStore.Audio.Media.IS_MUSIC + "!=0"
        val projection = arrayOf(MediaStore.Audio.Media._ID,
        MediaStore.Audio.Media.TITLE,MediaStore.Audio.Media.ARTIST,
        MediaStore.Audio.Media.DURATION,MediaStore.Audio.Media.DATE_ADDED,MediaStore.Audio.Media.DATA,MediaStore.Audio.Media.ALBUM,MediaStore.Audio.Media.ALBUM_ID)

        val cursor = this.contentResolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,projection,selection,null,MediaStore.Audio.Media.DATE_ADDED + " DESC ",null)

        if (cursor != null )
        {
            if (cursor.moveToFirst())
                do{
                val titleC = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE))
                val idC = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media._ID))
                val albumC = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM))
                val artistC = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST))
                val pathC =cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA))
                val durationC = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION))
                    val albumIdC=cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID)).toString()
                    val uri = Uri.parse("content://media/external/audio/albumart")
                    val artUriC = Uri.withAppendedPath(uri, albumIdC).toString()


                val music = Music(id = idC,title=titleC, artist = artistC, path = pathC, duration = durationC, albums = albumC, artUri = artUriC)
                val file = File(music.path)
                    if (file.exists())
                        tempList.add(music)
            }while (cursor.moveToNext())
            cursor.close()

        }
        return tempList

    }




    //------------------------------------------------------------- serarch view--------------------------------------------------------->

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.search_view_manu,menu)
        val serachView = menu?.findItem(R.id.searchView)?.actionView as androidx.appcompat.widget.SearchView
        serachView.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(p0: String?): Boolean =  true

            override fun onQueryTextChange(p0: String?): Boolean {
                musicListSearch  =ArrayList()
               if (p0!=null)
               {
                   val userInput = p0.lowercase()
                   for (song in MusicListMA)
                       if (song.title.lowercase().contains(userInput))
                           musicListSearch.add(song)
                   search = true
                   musicAdapter.updateMusicList(searchList = musicListSearch)
                 }
                return true
            }
        })
        return super.onCreateOptionsMenu(menu)
    }

    //--------------------------------------share prefernece for save data when application close----------------------------------
    override fun onDestroy() {
        super.onDestroy()

        if (!PlayerActivity.isPlaying && PlayerActivity.musicService!=null)
        {
            exitApplication()
        }


    }

    override fun onResume() {
        super.onResume()
        val editor = getSharedPreferences("FAVOURTES", MODE_PRIVATE).edit()

        val jsonString = GsonBuilder().create().toJson(FavourteActivity.favouriteSongs)
        editor.putString("favouriteSongs",jsonString)


        val jsonStringPlaylist= GsonBuilder().create().toJson(PlaylistActivity.musicPlaylist)
        editor.putString("MusicPlaylist",jsonStringPlaylist)


        editor.apply()
    }
}


