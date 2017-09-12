package org.jnanaprabodhini.happyteacher

import android.app.Application
import com.google.firebase.database.FirebaseDatabase
import uk.co.chrisjenx.calligraphy.CalligraphyConfig



/**
 * Created by grahamearley on 9/11/17.
 */
class HappyTeacherApplication: Application() {
    override fun onCreate() {
        super.onCreate()

        // Set Firebase offline persistence to true
        val databaseInstance = FirebaseDatabase.getInstance()
        databaseInstance.setPersistenceEnabled(true)

        // Set Roboto as default font
        CalligraphyConfig.initDefault(CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/Roboto-Regular.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        )
    }
}