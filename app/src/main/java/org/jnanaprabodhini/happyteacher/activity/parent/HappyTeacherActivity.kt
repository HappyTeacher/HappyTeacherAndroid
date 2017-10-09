package org.jnanaprabodhini.happyteacher.activity.parent

import android.content.Context
import android.support.v7.app.AppCompatActivity
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper
import org.jnanaprabodhini.happyteacher.extension.getBaseReferenceForCurrentLanguage

/**
 * An abstract activity for all activities in the app. Includes access
 *  to the Firebase root database and the database for the current language.
 */
abstract class HappyTeacherActivity: AppCompatActivity() {
    val databaseRoot: FirebaseDatabase by lazy {
        FirebaseDatabase.getInstance()
    }

    val databaseReference: DatabaseReference by lazy {
        databaseRoot.getBaseReferenceForCurrentLanguage()
    }

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase))
    }
}