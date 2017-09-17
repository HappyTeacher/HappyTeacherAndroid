package org.jnanaprabodhini.happyteacher.extension

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import org.jnanaprabodhini.happyteacher.prefs

/**
 * Created by grahamearley on 9/17/17.
 */

/**
 * Get child of database root that corresponds to the current language.
 */
fun FirebaseDatabase.getBaseReferenceForCurrentLanguage(): DatabaseReference {
    return getReference(prefs.getCurrentLanguageCode())
}