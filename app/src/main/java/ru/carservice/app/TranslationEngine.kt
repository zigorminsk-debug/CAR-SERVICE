package ru.carservice.app

import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.TranslatorOptions

object TranslationEngine {
    fun toRussian(
        sourceLanguage: String,
        text: String,
        fallback: String,
        onResult: (text: String, usedMachineTranslation: Boolean) -> Unit
    ) {
        if (sourceLanguage.equals("ru", ignoreCase = true)) {
            onResult(text, false)
            return
        }
        val source = when (sourceLanguage.lowercase()) {
            "en", "en-us", "en-gb" -> TranslateLanguage.ENGLISH
            "de" -> TranslateLanguage.GERMAN
            "fr" -> TranslateLanguage.FRENCH
            "es" -> TranslateLanguage.SPANISH
            else -> null
        }
        if (source == null) {
            onResult(fallback, false)
            return
        }

        val translator = Translation.getClient(
            TranslatorOptions.Builder()
                .setSourceLanguage(source)
                .setTargetLanguage(TranslateLanguage.RUSSIAN)
                .build()
        )
        translator.downloadModelIfNeeded(DownloadConditions.Builder().build())
            .addOnSuccessListener {
                translator.translate(text)
                    .addOnSuccessListener { translated ->
                        translator.close()
                        onResult(translated.ifBlank { fallback }, true)
                    }
                    .addOnFailureListener {
                        translator.close()
                        onResult(fallback, false)
                    }
            }
            .addOnFailureListener {
                translator.close()
                onResult(fallback, false)
            }
    }
}
