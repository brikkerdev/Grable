package ru.sirius.grable.playlist

import android.provider.UserDictionary
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ru.sirius.grable.R


class LearnPlaylistAdapter : RecyclerView.Adapter<LearnPlaylistAdapter.WordsViewHolder>() {
    private var words: List<Word> = emptyList()

    fun submitList(newList: List<Word>) {
        words = newList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WordsViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_playlist, parent, false)
        return WordsViewHolder(view)
    }

    override fun onBindViewHolder(holder: WordsViewHolder, position: Int) {
        val word = words[position]
        holder.bind(word)
    }

    override fun getItemCount(): Int = words.size

    class WordsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nameTextView: TextView = itemView.findViewById(R.id.originalWord)
        private val translationTextView: TextView = itemView.findViewById(R.id.translation)

        fun bind(word: Word) {
            nameTextView.text = word.original
            translationTextView.text = word.translation
        }
    }
}