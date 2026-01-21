package ru.sirius.grable.feature.learn.impl.ui.playlist

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.sirius.grable.feature.learn.impl.domain.Word
import ru.sirius.grable.feature.learn.impl.databinding.ItemWordBinding

class LearnPlaylistAdapter(
    private val onWordClick: (Word) -> Unit = {}
) : ListAdapter<Word, LearnPlaylistAdapter.WordsViewHolder>(WordDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WordsViewHolder {
        val binding = ItemWordBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return WordsViewHolder(binding, onWordClick)
    }

    override fun onBindViewHolder(holder: WordsViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class WordsViewHolder(
        private val binding: ItemWordBinding,
        private val onWordClick: (Word) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(word: Word) {
            binding.originalWord.text = word.original
            binding.translation.text = word.translation
            
            val isLearned = word.fsrsData.reviewCount > 0 && word.fsrsData.phase == ru.sirius.fsrs.CardPhase.Review.value
            
            val (statusColor, statusText) = when {
                word.fsrsData.reviewCount == 0 -> Pair(
                    ru.sirius.grable.core.design.R.color.word_status_new,
                    ru.sirius.grable.feature.learn.impl.R.string.learn_status_new
                )
                isLearned -> Pair(
                    ru.sirius.grable.core.design.R.color.word_status_learned,
                    ru.sirius.grable.feature.learn.impl.R.string.learn_status_learned
                )
                else -> Pair(
                    ru.sirius.grable.core.design.R.color.word_status_learning,
                    ru.sirius.grable.feature.learn.impl.R.string.learn_status_learning
                )
            }
            
            binding.statusIndicator.setBackgroundColor(
                binding.root.context.getColor(statusColor)
            )
            binding.statusLabel.setText(statusText)
            
            binding.root.setOnClickListener {
                onWordClick(word)
            }
        }
    }

    class WordDiffCallback : DiffUtil.ItemCallback<Word>() {
        override fun areItemsTheSame(oldItem: Word, newItem: Word): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Word, newItem: Word): Boolean {
            return oldItem == newItem
        }
    }
}

