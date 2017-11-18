package org.jnanaprabodhini.happyteacherapp.util

import android.content.Context
import android.content.res.Configuration
import android.os.Build
import org.jnanaprabodhini.happyteacherapp.R
import java.util.*

/**
 * A singleton object for managing language in the app.
 * 
 *  🙏
 *  Thanks to this blog post:
 *  https://proandroiddev.com/change-language-programmatically-at-runtime-on-android-5e6bc15c758
 */
object LocaleManager {

    fun changeLocale(newLocale: String, context: Context) {
        val prefs = PreferencesManager.getInstance(context)
        prefs.setCurrentLanguageCode(newLocale)

        // Make user choose a new board (since current
        // choice may not be available in this new locale):
        prefs.resetBoardChoice()
    }

    fun setLocale(context: Context): Context {
        val prefs = PreferencesManager.getInstance(context)
        val language = prefs.getCurrentLanguageCode()

        val locale = Locale(language)
        Locale.setDefault(locale)

        val resources = context.resources
        val config = Configuration(resources.configuration)
        if (Build.VERSION.SDK_INT >= 17) {
            config.setLocale(locale)
            return context.createConfigurationContext(config)
        } else {
            config.locale = locale
            resources.updateConfiguration(config, resources.displayMetrics)
        }

        return context
    }

    data class LocaleCodeWithTitle(val code: String, val title: String) {
        override fun toString(): String = title
    }

    fun getSupportedLanguagesWithTitles(context: Context) = arrayOf(
            LocaleCodeWithTitle("en", context.getString(R.string.english_in_english)),
            LocaleCodeWithTitle("mr", context.getString(R.string.marathi_in_marathi))
    )

}