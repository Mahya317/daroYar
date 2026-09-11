package com.example.util

import android.content.Context
import android.media.AudioManager
import android.media.ToneGenerator
import android.speech.tts.TextToSpeech
import android.util.Log
import java.util.Locale

class TtsManager(private val context: Context) : TextToSpeech.OnInitListener {
    private var tts: TextToSpeech? = TextToSpeech(context, this)
    private var isInitialized = false

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            // Try Persian locale
            val persianLocale = Locale("fa", "IR")
            val result = tts?.setLanguage(persianLocale)
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                // Fallback to default locale or Arabic/English
                tts?.setLanguage(Locale.getDefault())
            }
            isInitialized = true
        } else {
            Log.e("TtsManager", "TTS Initialization failed")
        }
    }

    fun speakReminder(medicationName: String, dosage: String) {
        playReminderBeep()
        val text = "زمان مصرف $medicationName است. دوز مصرفی: $dosage. آیا داروی خود را مصرف کردید؟"
        if (isInitialized) {
            tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "MED_REMINDER_ID")
        }
    }

    fun playReminderBeep() {
        try {
            val toneG = ToneGenerator(AudioManager.STREAM_ALARM, 100)
            toneG.startTone(ToneGenerator.TONE_CDMA_HIGH_PBX_SLS, 600)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun shutdown() {
        tts?.stop()
        tts?.shutdown()
    }
}
