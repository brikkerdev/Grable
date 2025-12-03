package ru.sirius.grable.learn.data

import ru.sirius.grable.learn.Word

class WordsRepository {
//    private val words = mutableListOf<Word>()
    fun getWordsByPlaylistId(playlistId: Long = 1) : List<Word> {
        val words = listOf(
            Word(1,1,"delicious", "вкусный", "[dɪˈlɪʃəs]", "This soup is absolutely delicious!"),
            Word(2, 1,"spicy", "острый", "[ˈspaɪsi]", "I love spicy Thai food."),
            Word(3,1,"rare", "с кровью (стейк)", "[rɛə]", "I'd like my steak rare, please."),
            Word(4,1,"medium rare", "средней прожарки", "[ˈmiːdiəm rɛə]", "Medium rare is perfect for me."),
            Word(5,1,"well-done", "хорошо прожаренный", "[ˌwɛl ˈdʌn]", "He always orders his meat well-done."),
            Word(6,1,"craving", "сильное желание съесть", "[ˈkreɪvɪŋ]", "I'm craving pizza right now."),
            Word(7,1,"crispy", "хрустящий", "[ˈkrɪspi]", "These fries are super crispy!"),
            Word(8,1,"tender", "нежный (мясо)", "[ˈtendər]", "The chicken was very tender."),
            Word(9,1,"overcooked", "переваренный/пережаренный", "[ˌəʊvəˈkʊkt]", "The pasta was overcooked and mushy."),
            Word(10,1,"undercooked", "недоваренный", "[ˌʌndəˈkʊkt]", "The rice is still undercooked."),
            Word(11,1,"mouthwatering", "аппетитный", "[ˈmaʊθˌwɔːtərɪŋ]", "That cake looks mouthwatering."),
        )
        return words.filter{it.playlistId == playlistId}
    }
}