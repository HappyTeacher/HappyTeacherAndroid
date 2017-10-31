package org.jnanaprabodhini.happyteacher

import android.app.Application
import android.content.Context
import android.content.res.Configuration
import android.preference.PreferenceManager
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import org.jnanaprabodhini.happyteacher.extension.withCurrentLocale
import org.jnanaprabodhini.happyteacher.util.LocaleManager
import org.jnanaprabodhini.happyteacher.util.PreferencesManager
import uk.co.chrisjenx.calligraphy.CalligraphyConfig

class HappyTeacherApplication: Application() {

    override fun onCreate() {
        super.onCreate()

        val settings = FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build()
        FirebaseFirestore.getInstance().firestoreSettings = settings

        // Set Roboto as default font
        CalligraphyConfig.initDefault(CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/Roboto-Regular.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        )

        // Initialize settings preferences
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false)
    }

    override fun attachBaseContext(base: Context) {
        val localeContext = base.withCurrentLocale()
        super.attachBaseContext(localeContext)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        LocaleManager.setLocale(this)
    }
}