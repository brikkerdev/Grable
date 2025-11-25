package ru.sirius.grable.playlist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ru.sirius.grable.main.Playlist

data class Word(
    val original: String,
    val translation: String,
    val transcription: String,
    val example: String
)

class LearnPlaylistViewModel: ViewModel() {
    private val _words = MutableLiveData<List<Word>>()
    val words: LiveData<List<Word>> = _words

    init {
        loadWords()
    }

    private fun loadWords(playlist: Playlist? = null) {
        _words.value = listOf(
            Word("delicious", "вкусный", "[dɪˈlɪʃəs]", "This soup is absolutely delicious!"),
            Word("spicy", "острый", "[ˈspaɪsi]", "I love spicy Thai food."),
            Word("rare", "с кровью (стейк)", "[rɛə]", "I'd like my steak rare, please."),
            Word("medium rare", "средней прожарки", "[ˈmiːdiəm rɛə]", "Medium rare is perfect for me."),
            Word("well-done", "хорошо прожаренный", "[ˌwɛl ˈdʌn]", "He always orders his meat well-done."),
            Word("craving", "сильное желание съесть", "[ˈkreɪvɪŋ]", "I'm craving pizza right now."),
            Word("crispy", "хрустящий", "[ˈkrɪspi]", "These fries are super crispy!"),
            Word("tender", "нежный (мясо)", "[ˈtendər]", "The chicken was very tender."),
            Word("overcooked", "переваренный/пережаренный", "[ˌəʊvəˈkʊkt]", "The pasta was overcooked and mushy."),
            Word("undercooked", "недоваренный", "[ˌʌndəˈkʊkt]", "The rice is still undercooked."),
            Word("mouthwatering", "аппетитный", "[ˈmaʊθˌwɔːtərɪŋ]", "That cake looks mouthwatering."),
        )
    }
}