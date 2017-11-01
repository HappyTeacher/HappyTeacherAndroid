package org.jnanaprabodhini.happyteacher.adapter.helper

import com.crashlytics.android.Crashlytics
import com.google.firebase.firestore.FirebaseFirestoreException

/**
 * An interface for responding to Firebase data events from an adapter.
 */
interface FirebaseDataObserver {
    fun onRequestNewData() {}

    fun onDataLoaded() {}

    fun onDataEmpty() {}

    fun onDataNonEmpty() {}

    fun onError(e: FirebaseFirestoreException?) {
        Crashlytics.logException(e)
    }
}