package org.jnanaprabodhini.happyteacher.adapter

import android.content.Context
import android.support.annotation.LayoutRes
import com.firebase.ui.database.FirebaseIndexListAdapter
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.Query

/**
 * Created by grahamearley on 9/19/17.
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