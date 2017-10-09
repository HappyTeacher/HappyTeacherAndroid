package org.jnanaprabodhini.happyteacher.adapter.helper

/**
 * An interface for responding to Firebase data events from an adapter.
 */
interface FirebaseDataObserver {
    fun onRequestNewData() {}

    fun onDataLoaded() {}

    fun onDataEmpty() {}

    fun onDataNonEmpty() {}
}