package org.jnanaprabodhini.happyteacher

import android.app.Application
import android.content.Context
import android.content.res.Configuration
import com.google.firebase.database.FirebaseDatabase
import org.jnanaprabodhini.happyteacher.extension.withCurrentLocale
import org.jnanaprabodhini.happyteacher.util.LocaleManager
import org.jnanaprabodhini.happyteacher.util.PreferencesManager
import uk.co.chrisjenx.calligraphy.CalligraphyConfig

val prefs: PreferencesManager by lazy {
    // Package-wide access to PreferencesManager.
    HappyTeacherApplication.preferences!!
}

class HappyTeacherApplication: Application() {

    companion object {
        var preferences: PreferencesManager? = null
    }

    override fun onCreate() {
        super.onCreate()

        FirebaseDatabase.getInstance().setPersistenceEnabled(true)
        FirebaseDatabase.getInstance().reference.keepSynced(true)

        // Set Roboto as default font
        CalligraphyConfig.initDefault(CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/Roboto-Regular.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        )
    }

    override fun attachBaseContext(base: Context) {
        // Initialize preferences (package-wide)
        preferences = PreferencesManager(base)

        val localeContext = base.withCurrentLocale()

        super.attachBaseContext(localeContext)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        LocaleManager.setLocale(this)
    }
}