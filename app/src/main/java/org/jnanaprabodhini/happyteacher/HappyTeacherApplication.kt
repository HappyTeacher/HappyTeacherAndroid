package org.jnanaprabodhini.happyteacher

import android.app.Application
import com.google.firebase.database.FirebaseDatabase
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

        // Initialize preferences (package-wide)
        preferences = PreferencesManager(this)

        // Set Firebase offline persistence to true
        val databaseInstance = FirebaseDatabase.getInstance()
        databaseInstance.setPersistenceEnabled(true)

        // TODO: Don't keep entire db synced. Only sync essential items.
        databaseInstance.reference.keepSynced(true)

        // Set Roboto as default font
        CalligraphyConfig.initDefault(CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/Roboto-Regular.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        )
    }
}