package org.jnanaprabodhini.happyteacher.adapter.firebase

import android.content.Context
import android.support.annotation.LayoutRes
import com.firebase.ui.database.FirebaseIndexListAdapter
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.Query
import org.jnanaprabodhini.happyteacher.adapter.helper.FirebaseDataObserver

/**
 * A simple subclass of the FirebaseIndexListAdapter that notifies
 *  a FirebaseDataObserver of loading events in the adapter.
 *
 *  See FirebaseObserverRecyclerAdapter (this is a simpler version of that class,
 *      without the subclassing since we only need this one class).
 */
abstract class FirebaseIndexObserverListAdapter<T>(context: Context,
                                                   modelClass: Class<T>,
                                                   @LayoutRes modelLayout: Int,
                                                   keyQuery: Query,
                                                   dataRef: DatabaseReference,
                                                   val firebaseDataObserver: FirebaseDataObserver): FirebaseIndexListAdapter<T>(context, modelClass, modelLayout, keyQuery, dataRef) {

    init {
        firebaseDataObserver.onRequestNewData()
    }

    override fun onDataChanged() {
        super.onDataChanged()

        firebaseDataObserver.onDataLoaded()

        when (count) {
            0 -> firebaseDataObserver.onDataEmpty()
            else -> firebaseDataObserver.onDataNonEmpty()
        }
    }
}