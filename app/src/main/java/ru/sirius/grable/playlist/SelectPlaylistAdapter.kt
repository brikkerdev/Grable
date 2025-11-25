package ru.sirius.grable.playlist

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ru.sirius.grable.R

class SelectPlaylistAdapter : RecyclerView.Adapter<SelectPlaylistAdapter.PlaylistViewHolder>() {
    private var playlists: List<Playlist> = emptyList()

    fun submitList(newList: List<Playlist>) {
        playlists = newList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_playlist, parent, false)
        return PlaylistViewHolder(view)
    }

    override fun onBindViewHolder(holder: PlaylistViewHolder, position: Int) {
        val playlist = playlists[position]
        holder.bind(playlist)
    }

    override fun getItemCount(): Int = playlists.size

    // Класс ViewHolder держит ссылки на элементы View, чтобы не искать их каждый раз
    class PlaylistViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nameTextView: TextView = itemView.findViewById(R.id.playlist_name)
        private val descriptionTextView: TextView = itemView.findViewById(R.id.playlist_description)

        fun bind(playlist: Playlist) {
            nameTextView.text = playlist.name
            descriptionTextView.text = playlist.description
        }
    }
}