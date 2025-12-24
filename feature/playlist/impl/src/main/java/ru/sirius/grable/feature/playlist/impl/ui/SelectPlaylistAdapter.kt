package ru.sirius.grable.feature.playlist.impl.ui

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.sirius.grable.feature.playlist.impl.R
import ru.sirius.grable.feature.playlist.api.Playlist
import ru.sirius.grable.feature.playlist.impl.databinding.ItemPlaylistBinding

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

    class PlaylistViewHolder(binding: ItemPlaylistBinding) : RecyclerView.ViewHolder(binding.root) {
        val nameText: TextView = binding.playlistName
        val descText: TextView = binding.playlistDescription
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistViewHolder {
        val view = ItemPlaylistBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
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