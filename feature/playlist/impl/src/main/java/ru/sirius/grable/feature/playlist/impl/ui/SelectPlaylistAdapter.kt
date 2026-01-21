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
import ru.sirius.grable.feature.playlist.api.PlaylistListItem
import ru.sirius.grable.feature.playlist.impl.databinding.ItemPlaylistBinding

class SelectPlaylistAdapter(
    private val onItemClick: (Playlist) -> Unit,
    private val onCheckboxClick: ((Long) -> Unit)? = null
) : ListAdapter<PlaylistListItem, RecyclerView.ViewHolder>(DIFF_CALLBACK) {

    var isEditMode: Boolean = false
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    var selectedPlaylistIds: Set<Long> = emptySet()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    companion object {
        private const val VIEW_TYPE_PLAYLIST = 0
        private const val VIEW_TYPE_SEPARATOR = 1

        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<PlaylistListItem>() {
            override fun areItemsTheSame(oldItem: PlaylistListItem, newItem: PlaylistListItem): Boolean {
                return when {
                    oldItem is PlaylistListItem.PlaylistItem && newItem is PlaylistListItem.PlaylistItem -> 
                        oldItem.playlist.id == newItem.playlist.id
                    oldItem is PlaylistListItem.Separator && newItem is PlaylistListItem.Separator -> 
                        true
                    else -> false
                }
            }

            override fun areContentsTheSame(oldItem: PlaylistListItem, newItem: PlaylistListItem): Boolean {
                return oldItem == newItem
            }
        }
    }

    class PlaylistViewHolder(binding: ItemPlaylistBinding) : RecyclerView.ViewHolder(binding.root) {
        val nameText: TextView = binding.playlistName
        val descText: TextView = binding.playlistDescription
        val checkbox: android.widget.CheckBox = binding.playlistCheckbox
    }

    class SeparatorViewHolder(view: View) : RecyclerView.ViewHolder(view)

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is PlaylistListItem.PlaylistItem -> VIEW_TYPE_PLAYLIST
            is PlaylistListItem.Separator -> VIEW_TYPE_SEPARATOR
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_PLAYLIST -> {
                val binding = ItemPlaylistBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                PlaylistViewHolder(binding)
            }
            VIEW_TYPE_SEPARATOR -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_playlist_separator, parent, false)
                SeparatorViewHolder(view)
            }
            else -> throw IllegalArgumentException("Unknown view type: $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = getItem(position)) {
            is PlaylistListItem.PlaylistItem -> {
                val playlistHolder = holder as PlaylistViewHolder
                playlistHolder.nameText.text = item.playlist.name
                playlistHolder.descText.text = item.playlist.description ?: "${item.playlist.name} description"
                
                playlistHolder.checkbox.visibility = if (isEditMode) View.VISIBLE else View.GONE
                playlistHolder.checkbox.isChecked = selectedPlaylistIds.contains(item.playlist.id)
                
                if (isEditMode) {
                    playlistHolder.itemView.setOnClickListener {
                        playlistHolder.checkbox.isChecked = !playlistHolder.checkbox.isChecked
                        onCheckboxClick?.invoke(item.playlist.id)
                    }
                    playlistHolder.checkbox.setOnClickListener {
                        onCheckboxClick?.invoke(item.playlist.id)
                    }
                } else {
                    playlistHolder.itemView.setOnClickListener { onItemClick(item.playlist) }
                    playlistHolder.checkbox.setOnClickListener(null)
                }
            }
            is PlaylistListItem.Separator -> {
            }
        }
    }
}