package ru.sirius.api

interface ITTS {
    fun play(text: String)
    fun stopPlayback()
    fun shutdownTTS()
    fun speak(text: String, utteranceId: String? = null)
    fun setVoice(voiceType: String) // "male" or "female"
}