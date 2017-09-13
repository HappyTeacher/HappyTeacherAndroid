package org.jnanaprabodhini.happyteacher

import android.app.Application
import com.google.firebase.database.FirebaseDatabase
import uk.co.chrisjenx.calligraphy.CalligraphyConfig

/**
 * Created by grahamearley on 9/11/17.
 */

val prefs: PreferencesManager by lazy {
    HappyTeacherApplication.preferences!!
}

class HappyTeacherApplication: Application() {

    companion object {
        var preferences: PreferencesManager? = null
    }

    override fun onCreate() {
        super.onCreate()

        // Initialize preferences
        preferences = PreferencesManager(this)

        // Set Firebase offline persistence to true
        val databaseInstance = FirebaseDatabase.getInstance()
        databaseInstance.setPersistenceEnabled(true)

        databaseInstance.reference.keepSynced(true)

        // Set Roboto as default font
        CalligraphyConfig.initDefault(CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/Roboto-Regular.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        )
    }
}