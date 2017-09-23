package org.jnanaprabodhini.happyteacher.adapter

import android.support.annotation.LayoutRes
import android.support.v7.widget.RecyclerView
import com.firebase.ui.database.FirebaseIndexRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.Query
import org.jnanaprabodhini.happyteacher.DataObserver

/**
 * An extension of the FirebaseIndexRecyclerAdapter that provides callbacks
 *  for certain data change events.
 */
abstract class FirebaseIndexDataObserverRecyclerAdapter<T, VH: RecyclerView.ViewHolder>(modelClass: Class<T>,
                                                                                        @LayoutRes modelLayout: Int,
                                                                                        viewHolderClass: Class<VH>,
                                                                                        keyQuery: Query,
                                                                                        dataRef: DatabaseReference,
                                                                                        val dataObserver: DataObserver): FirebaseIndexRecyclerAdapter<T, VH>(modelClass, modelLayout, viewHolderClass, keyQuery, dataRef) {

    init {
        dataObserver.onRequestNewData()
    }

    override fun onDataChanged() {
        super.onDataChanged()

        dataObserver.onDataLoaded()

        when (itemCount) {
            0 -> dataObserver.onDataEmpty()
            else -> dataObserver.onDataNonEmpty()
        }
    }

}