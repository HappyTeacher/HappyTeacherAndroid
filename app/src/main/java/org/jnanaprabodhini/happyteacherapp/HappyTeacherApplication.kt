package org.jnanaprabodhini.happyteacherapp

import android.app.Application
import android.content.Context
import android.content.res.Configuration
import android.support.v7.preference.PreferenceManager
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import org.jnanaprabodhini.happyteacherapp.extension.withCurrentLocale
import org.jnanaprabodhini.happyteacherapp.util.LocaleManager
import uk.co.chrisjenx.calligraphy.CalligraphyConfig

class HappyTeacherApplication: android.support.multidex.MultiDexApplication() {

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