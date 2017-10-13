package org.jnanaprabodhini.happyteacher

import android.content.Context
import android.content.res.Configuration
import android.os.Build
import java.util.*

/**
 * Created by grahamearley on 10/13/17.
 */
object LocaleManager {

    fun changeLocale(newLocale: String) {
        prefs.setCurrentLanguageCode(newLocale)
    }

    fun setLocale(context: Context): Context {
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

}