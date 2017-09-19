package org.jnanaprabodhini.happyteacher.extension

import com.google.firebase.database.*
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

fun DatabaseReference.onSingleValueEvent(onValueEvent: (DataSnapshot?) -> Unit) {
    this.addListenerForSingleValueEvent(object: ValueEventListener {
        override fun onCancelled(error: DatabaseError?) {}

        override fun onDataChange(snapshot: DataSnapshot?) {
            onValueEvent(snapshot)
        }
    })
}