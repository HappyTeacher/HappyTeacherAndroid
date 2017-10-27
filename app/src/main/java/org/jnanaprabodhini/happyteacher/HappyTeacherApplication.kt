package org.jnanaprabodhini.happyteacher

import android.app.Application
import android.content.Context
import android.content.res.Configuration
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
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

        // Sign in anonymously if not signed in
        val auth = FirebaseAuth.getInstance()
        if (auth.currentUser == null) {
            auth.signInAnonymously()
        }
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