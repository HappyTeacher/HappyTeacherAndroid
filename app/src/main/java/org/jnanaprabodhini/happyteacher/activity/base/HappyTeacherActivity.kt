package org.jnanaprabodhini.happyteacher.activity.base

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper
import org.jnanaprabodhini.happyteacher.extension.getBaseReferenceForCurrentLanguage
import org.jnanaprabodhini.happyteacher.extension.withCurrentLocale
import org.jnanaprabodhini.happyteacher.prefs

/**
 * An abstract activity for all activities in the app. Includes access
 *  to the Firebase root database and the database for the current language,
 *  and locale switching.
 */
abstract class HappyTeacherActivity: AppCompatActivity() {

    val firestoreRoot: FirebaseFirestore by lazy {
        FirebaseFirestore.getInstance()
    }

    val firestoreLocalized: DocumentReference by lazy {
        firestoreRoot.collection("localized").document(prefs.getCurrentLanguageCode())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        resetTitle()
    }

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase.withCurrentLocale()))
    }

    protected fun refreshActivity() {
        val intent = intent
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
        finish()
        startActivity(intent)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            // Make "Up" button go Back
            android.R.id.home -> {
                onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    fun resetTitle() {
        try {
            val titleId = packageManager.getActivityInfo(componentName, PackageManager.GET_META_DATA).labelRes
            if (titleId != 0) {
                setTitle(titleId)
            }
        } catch (e: PackageManager.NameNotFoundException) {
            // Then we will show the title as it is set in top-level resources.
        }
    }
}