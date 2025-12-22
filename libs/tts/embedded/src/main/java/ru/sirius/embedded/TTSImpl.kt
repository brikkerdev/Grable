package ru.sirius.embedded

import android.content.Context
import android.os.Build
import android.os.Handler
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import ru.sirius.api.ITTS
import java.util.Locale
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class TTSImpl(
    private val context: Context
) : ITTS {
    private var tts: TextToSpeech? = null
    private var isInitialized = false
    private var isLanguageSet = false
    private val pendingUtterances = mutableMapOf<String, String>()
    private var onInitListener: ((Boolean) -> Unit)? = null
    private var onUtteranceListener: OnUtteranceListener? = null
    private var defaultLocale: Locale = Locale.ENGLISH

    init {
        initTTS()
    }

    private fun initTTS() {
        // Инициализируем TTS с явным слушателем
        tts = TextToSpeech(context, TextToSpeech.OnInitListener { status ->
            Log.d("TTS", "TTS OnInit status: $status")

            isInitialized = status == TextToSpeech.SUCCESS

            if (isInitialized) {
                // Устанавливаем слушатель прогресса ДО попытки установки языка
                setupUtteranceListener()

                // Даем время на полную инициализацию
                Handler(context.mainLooper).postDelayed({
                    setLanguageInternal()
                }, 200)
            } else {
                Log.e("TTS", "TTS initialization failed with status: $status")
                onInitListener?.invoke(false)
            }
        })
    }

    private fun setupUtteranceListener() {
        tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
            override fun onStart(utteranceId: String?) {
                Log.d("TTS", "Utterance started: $utteranceId")
                onUtteranceListener?.onStart(utteranceId)
            }

            override fun onDone(utteranceId: String?) {
                Log.d("TTS", "Utterance done: $utteranceId")
                onUtteranceListener?.onDone(utteranceId)
            }

            @Deprecated("Deprecated in Java")
            override fun onError(utteranceId: String?) {
                Log.e("TTS", "Utterance error (deprecated): $utteranceId")
                onUtteranceListener?.onError(utteranceId)
            }

            override fun onError(utteranceId: String?, errorCode: Int) {
                Log.e("TTS", "Utterance error: $utteranceId, code: $errorCode")
                onUtteranceListener?.onError(utteranceId, errorCode)
            }
        })
    }

    private fun setLanguageInternal(): Boolean {
        return if (isInitialized && tts != null) {
            try {
                // Пробуем установить язык устройства
                var result = tts!!.setLanguage(defaultLocale)

                if (result == TextToSpeech.LANG_MISSING_DATA ||
                    result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Log.w("TTS", "Default locale not supported, trying US")
                    result = tts!!.setLanguage(Locale.US)
                }

                if (result == TextToSpeech.LANG_MISSING_DATA ||
                    result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Log.w("TTS", "US locale not supported, trying UK")
                    result = tts!!.setLanguage(Locale.UK)
                }

                if (result >= TextToSpeech.LANG_AVAILABLE) {
                    isLanguageSet = true
                    Log.d("TTS", "Language set successfully")

                    // Воспроизводим ожидающие высказывания
                    pendingUtterances.forEach { (utteranceId, text) ->
                        speakInternal(text, utteranceId)
                    }
                    pendingUtterances.clear()

                    onInitListener?.invoke(true)
                    true
                } else {
                    Log.e("TTS", "Failed to set any language")
                    onInitListener?.invoke(false)
                    false
                }
            } catch (e: Exception) {
                Log.e("TTS", "Error setting language", e)
                false
            }
        } else {
            Log.e("TTS", "Cannot set language: TTS not initialized")
            false
        }
    }

    fun setOnInitListener(listener: (Boolean) -> Unit) {
        this.onInitListener = listener
        if (isInitialized && isLanguageSet) {
            listener(true)
        }
    }

    fun setOnUtteranceListener(listener: OnUtteranceListener) {
        this.onUtteranceListener = listener
    }

    fun setLanguage(locale: Locale): Int {
        defaultLocale = locale
        return if (isInitialized && tts != null) {
            tts!!.setLanguage(locale)
        } else {
            TextToSpeech.LANG_NOT_SUPPORTED
        }
    }

    override fun play(text: String) {
        speak(text, null)
        Log.d("TTS", "Play: $text")
    }

    private fun speakInternal(text: String, utteranceId: String?): Int {
        return if (isInitialized && tts != null && isLanguageSet) {
            val id = utteranceId ?: "utterance_${System.currentTimeMillis()}"

            // Для Android 21+ используем новый метод
            val result = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                tts!!.speak(text, TextToSpeech.QUEUE_FLUSH, null, id)
            } else {
                @Suppress("DEPRECATION")
                tts!!.speak(text, TextToSpeech.QUEUE_FLUSH, null)
            }

            Log.d("TTS", "Speak result: $result for text: $text")
            result
        } else {
            Log.w("TTS", "Cannot speak: TTS not ready. Text: $text")
            TextToSpeech.ERROR
        }
    }

    override fun speak(text: String, utteranceId: String?) {
        val id = utteranceId ?: "utterance_${System.currentTimeMillis()}"

        if (isInitialized && isLanguageSet) {
            speakInternal(text, id)
        } else {
            Log.d("TTS", "TTS not ready, adding to pending: $text")
            pendingUtterances.clear() // Очищаем предыдущие
            pendingUtterances[id] = text

            // Если TTS инициализирован, но язык не установлен, пытаемся установить
            if (isInitialized && !isLanguageSet) {
                setLanguageInternal()
            }
        }
    }

    suspend fun speak(text: String): Boolean = suspendCoroutine { continuation ->
        val utteranceId = "utterance_${System.currentTimeMillis()}"

        if (isInitialized && isLanguageSet) {
            val result = speakInternal(text, utteranceId)
            continuation.resume(result == TextToSpeech.SUCCESS)
        } else {
            pendingUtterances[utteranceId] = text

            val tempListener: (Boolean) -> Unit = { success ->
                if (success) {
                    val result = speakInternal(text, utteranceId)
                    continuation.resume(result == TextToSpeech.SUCCESS)
                } else {
                    continuation.resume(false)
                }
            }

            val originalListener = onInitListener
            onInitListener = { success ->
                tempListener(success)
                originalListener?.invoke(success)
            }
        }
    }

    override fun stopPlayback() {
        Log.d("TTS", "Stop playback called")
        if (isInitialized && tts != null) {
            tts!!.stop()
        }
        pendingUtterances.clear()
    }

    override fun shutdownTTS() {
        Log.d("TTS", "Shutdown called")
        if (isInitialized && tts != null) {
            tts!!.shutdown()
            isInitialized = false
            isLanguageSet = false
        }
        pendingUtterances.clear()
    }

    fun setPitch(pitch: Float): Int {
        return if (isInitialized && tts != null) {
            tts!!.setPitch(pitch)
        } else {
            TextToSpeech.ERROR
        }
    }

    fun setSpeechRate(rate: Float): Int {
        return if (isInitialized && tts != null) {
            tts!!.setSpeechRate(rate)
        } else {
            TextToSpeech.ERROR
        }
    }

    fun isSpeaking(): Boolean {
        return if (isInitialized && tts != null) {
            tts!!.isSpeaking
        } else {
            false
        }
    }

    // Метод для принудительной переинициализации
    fun reinitialize() {
        shutdownTTS()
        initTTS()
    }

    interface OnUtteranceListener {
        fun onStart(utteranceId: String?)
        fun onDone(utteranceId: String?)
        fun onError(utteranceId: String?)
        fun onError(utteranceId: String?, errorCode: Int)
    }
}