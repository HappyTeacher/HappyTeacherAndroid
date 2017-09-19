package org.jnanaprabodhini.happyteacher.adapter

import android.content.Context
import android.support.annotation.LayoutRes
import com.firebase.ui.database.FirebaseIndexListAdapter
import com.firebase.ui.database.FirebaseListAdapter
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.Query
import org.jnanaprabodhini.happyteacher.DataObserver

/**
 * Created by grahamearley on 9/19/17.
 */
abstract class FirebaseIndexDataObserverListAdapter<T>(context: Context,
                                              modelClass: Class<T>,
                                              @LayoutRes modelLayout: Int,
                                              keyQuery: Query,
                                              dataRef: DatabaseReference,
                                              val dataObserver: DataObserver): FirebaseIndexListAdapter<T>(context, modelClass, modelLayout, keyQuery, dataRef) {

    override fun onDataChanged() {
        super.onDataChanged()

        dataObserver.onDataLoaded()

        when (count) {
            0 -> dataObserver.onDataEmpty()
            else -> dataObserver.onDataNonEmpty()
        }
    }
}