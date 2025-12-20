package ru.sirius.grable.main

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.sirius.grable.R

class SelectPlaylistAdapter(
    private val onItemClick: (Playlist) -> Unit
) : ListAdapter<Playlist, SelectPlaylistAdapter.PlaylistViewHolder>(DIFF_CALLBACK) {

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Playlist>() {
            override fun areItemsTheSame(oldItem: Playlist, newItem: Playlist): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Playlist, newItem: Playlist): Boolean {
                return oldItem == newItem
            }
        }
    }

    class PlaylistViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameText: TextView = itemView.findViewById(R.id.playlist_name)
        val descText: TextView = itemView.findViewById(R.id.playlist_description)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_playlist, parent, false)
        return PlaylistViewHolder(view)
    }

    override fun onBindViewHolder(holder: PlaylistViewHolder, position: Int) {
        val playlist = getItem(position)
        holder.nameText.text = playlist.name
        holder.descText.text = playlist.description ?: "${playlist.name} description"
        Log.d("TEST_CLICK", "CLICKED ${playlist.name}")
        holder.itemView.setOnClickListener { onItemClick(playlist) }
    }
}