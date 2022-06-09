package com.example.musicplayer

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.musicplayer.databinding.MusicViewBinding

class MusicAdapter(private val context:Context, private var musicList: ArrayList<Music>,private val playlistDetails:Boolean = false,private val selectionActivity:Boolean = false): RecyclerView.Adapter<MusicAdapter.MyHolder>() {
    class MyHolder(binding: MusicViewBinding):RecyclerView.ViewHolder(binding.root) {
        val title = binding.songNameMV
        val albums = binding.songAlbumMV
        val  image = binding.imageMv
        val duration = binding.songDuration
        val root = binding.root
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder {
        return MyHolder(MusicViewBinding.inflate(LayoutInflater.from(context),parent,false))
    }

    override fun onBindViewHolder(holder: MyHolder, position: Int) {
        holder.title.text = musicList[position].title
        holder.albums.text = musicList[position].albums
        //holder.duration.text = musicList[position].duration.toString()
        holder.duration.text = formatDuration(musicList[position].duration)

        Glide.with(context).load(musicList[position].artUri).apply(RequestOptions().placeholder(R.drawable.music).centerCrop())
            .into(holder.image)

        when{
            playlistDetails ->{
                holder.root.setOnClickListener {
                    sentIntent(ref = "PlaylistDetailAdapter", pos = position)
                }
            }
            
            selectionActivity ->{
                holder.root.setOnClickListener {
                    if (addsong(musicList[position]))
                        holder.root.setBackgroundColor(ContextCompat.getColor(context, R.color.RED))
                    else
                        holder.root.setBackgroundColor(
                            ContextCompat.getColor(
                                context,
                                R.color.white
                            )
                        )
                }
            }
            else->
            {
                holder.root.setOnClickListener {
                    when{
                        MainActivity.search -> sentIntent(ref = "MusicAdapter", pos = position)
                        musicList[position].id ==PlayerActivity.nowPlayingId->
                            sentIntent(ref = "NowPlaying", pos = PlayerActivity.songPotion)
                        else->sentIntent(ref = "MusicAdapter", pos = position)
                    }


                }

            }
        }


    }

    override fun getItemCount(): Int {
        return musicList.size
    }

    fun updateMusicList(searchList:ArrayList<Music>)
    {
        musicList = ArrayList()
        musicList.addAll(searchList)
        notifyDataSetChanged()
    }

    private fun sentIntent(ref:String,pos:Int)
    {
        val intent = Intent(context,PlayerActivity::class.java)
        intent.putExtra("index",pos)
        intent.putExtra("class",ref)

        ContextCompat.startActivities(context, arrayOf(intent),null)
    }
    
    private fun addsong(song:Music):Boolean{
        PlaylistActivity.musicPlaylist.ref[PlaylistDetail.currentPlaylistpos].playlist.forEachIndexed { index, music ->
            if (song.id == music.id)
            {
                PlaylistActivity.musicPlaylist.ref[PlaylistDetail.currentPlaylistpos].playlist.removeAt(index)
                return false
            }

        }

        PlaylistActivity.musicPlaylist.ref[PlaylistDetail.currentPlaylistpos].playlist.add(song)
        return true
    }
    fun refreshPlaylist()
    {
        musicList = ArrayList()
        musicList = PlaylistActivity.musicPlaylist.ref[PlaylistDetail.currentPlaylistpos].playlist
        notifyDataSetChanged()
    }
}