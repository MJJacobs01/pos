package com.refresh.pos.ui

import android.content.Context
import android.content.res.Configuration
import java.util.Locale

object LocaleHelper {

    private const val PREFS_NAME = "pos_prefs"
    private const val KEY_LOCALE = "locale"

    fun setLocale(context: Context, localeCode: String) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putString(KEY_LOCALE, localeCode)
            .apply()
    }

    fun getSavedLocale(context: Context): String {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getString(KEY_LOCALE, "en") ?: "en"
    }

    fun wrapContext(context: Context, localeCode: String): Context {
        val locale = when (localeCode) {
            "th" -> Locale.forLanguageTag("th")
            "jp" -> Locale.forLanguageTag("ja")
            else -> Locale.forLanguageTag("en")
        }
        Locale.setDefault(locale)
        val config = Configuration(context.resources.configuration)
        config.setLocale(locale)
        return context.createConfigurationContext(config)
    }
}
