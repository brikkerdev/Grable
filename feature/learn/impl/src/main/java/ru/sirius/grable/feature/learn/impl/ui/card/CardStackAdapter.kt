package ru.sirius.grable.feature.learn.impl.ui.card

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import org.koin.java.KoinJavaComponent.inject
import ru.sirius.embedded.TTSImpl
import ru.sirius.grable.feature.learn.impl.R
import ru.sirius.grable.feature.learn.impl.domain.Word

class CardStackAdapter : RecyclerView.Adapter<CardStackAdapter.CardViewHolder>() {

    private val tts: TTSImpl by inject(TTSImpl::class.java)
    private val cards = mutableListOf<Word>()
    
    var onCardFlip: ((Int) -> Unit)? = null
    var onMarkAsKnown: ((Word) -> Unit)? = null

    fun submitList(newCards: List<Word>) {
        cards.clear()
        cards.addAll(newCards)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_card_learn, parent, false)
        return CardViewHolder(view)
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        holder.bind(cards[position])
    }

    override fun getItemCount(): Int = cards.size

    inner class CardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val frontGroup: LinearLayout = itemView.findViewById(R.id.frontGroup)
        private val backGroup: LinearLayout = itemView.findViewById(R.id.backGroup)
        private val frontWord: TextView = itemView.findViewById(R.id.frontWord)
        private val frontTranscription: TextView = itemView.findViewById(R.id.frontTranscription)
        private val backTranslation: TextView = itemView.findViewById(R.id.backTranslation)
        private val backOriginal: TextView = itemView.findViewById(R.id.backOriginal)
        private val backExample: TextView = itemView.findViewById(R.id.backExample)
        private val btnAudio: ImageButton = itemView.findViewById(R.id.btnAudio)
        private val btnMarkAsKnown: MaterialButton = itemView.findViewById(R.id.btnMarkAsKnown)
        
        val overlayLeft: FrameLayout = itemView.findViewById(R.id.overlayLeft)
        val overlayRight: FrameLayout = itemView.findViewById(R.id.overlayRight)
        val overlayUp: FrameLayout = itemView.findViewById(R.id.overlayUp)

        private var showingFront = true
        private var currentWord: Word? = null

        init {
            itemView.setOnClickListener { flipCard() }
            btnAudio.setOnClickListener { playAudio() }
            btnMarkAsKnown.setOnClickListener { 
                currentWord?.let { word ->
                    onMarkAsKnown?.invoke(word)
                }
            }
        }

        fun bind(word: Word) {
            currentWord = word
            showingFront = true
            showFrontSide()
            
            overlayLeft.alpha = 0f
            overlayRight.alpha = 0f
            overlayUp.alpha = 0f
            
            frontWord.text = word.original
            frontTranscription.text = word.transcription ?: ""
            backTranslation.text = word.translation
            backOriginal.text = word.original
            
            val exampleText = word.examples.firstOrNull()?.text ?: ""
            backExample.text = exampleText
            backExample.visibility = if (exampleText.isNotEmpty()) View.VISIBLE else View.GONE
        }

        private fun showFrontSide() {
            frontGroup.animate().cancel()
            backGroup.animate().cancel()
            showingFront = true
            frontGroup.visibility = View.VISIBLE
            backGroup.visibility = View.GONE
            frontGroup.rotationY = 0f
            backGroup.rotationY = 0f
        }

        private fun flipCard() {
            val front = frontGroup
            val back = backGroup
            val visible = if (showingFront) front else back
            val hidden = if (showingFront) back else front

            visible.animate()
                .rotationY(90f)
                .setDuration(150)
                .withEndAction {
                    visible.visibility = View.GONE
                    visible.rotationY = 0f
                    hidden.visibility = View.VISIBLE
                    hidden.rotationY = -90f
                    hidden.animate()
                        .rotationY(0f)
                        .setDuration(150)
                        .start()
                }
                .start()
            showingFront = !showingFront
            onCardFlip?.invoke(bindingAdapterPosition)
        }

        private fun playAudio() {
            currentWord?.let {
                tts.play(it.original)
            }
        }
    }
}

