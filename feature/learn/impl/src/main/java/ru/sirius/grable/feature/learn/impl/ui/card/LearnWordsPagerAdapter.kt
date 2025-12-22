package ru.sirius.grable.feature.learn.impl.ui.card

import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DiffUtil
import androidx.viewpager2.adapter.FragmentStateAdapter
import ru.sirius.grable.feature.learn.api.Word

class LearnWordsPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    private val items = mutableListOf<Word>()

    override fun getItemCount(): Int = items.size

    override fun createFragment(position: Int): Fragment {
        return WordFragment.newInstance(items[position])
    }

    override fun getItemId(position: Int): Long = items[position].id

    override fun containsItem(itemId: Long): Boolean = items.any { it.id == itemId }

    fun submitWords(words: List<Word>) {
        val diffCallback = object : DiffUtil.Callback() {
            override fun getOldListSize(): Int = items.size
            override fun getNewListSize(): Int = words.size
            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return items[oldItemPosition].id == words[newItemPosition].id
            }
            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return items[oldItemPosition] == words[newItemPosition]
            }
        }
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        items.clear()
        items.addAll(words)
        diffResult.dispatchUpdatesTo(this)
    }
}

