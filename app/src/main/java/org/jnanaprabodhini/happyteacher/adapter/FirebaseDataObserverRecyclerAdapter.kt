package org.jnanaprabodhini.happyteacher.adapter

import android.support.annotation.LayoutRes
import android.support.v7.widget.RecyclerView
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.google.firebase.database.Query
import org.jnanaprabodhini.happyteacher.DataObserver

/**
 * An extension of the FirebaseRecyclerAdapter that provides callbacks
 *  for certain data change events.
 */
abstract class FirebaseDataObserverRecyclerAdapter<T, VH: RecyclerView.ViewHolder>(modelClass: Class<T>,
                                                                                   @LayoutRes modelLayout: Int,
                                                                                   viewHolderClass: Class<VH>,
                                                                                   query: Query,
                                                                                   val dataObserver: DataObserver): FirebaseRecyclerAdapter<T, VH>(modelClass, modelLayout, viewHolderClass,query) {
    override fun onDataChanged() {
        super.onDataChanged()

        dataObserver.onDataLoaded()

        when (itemCount) {
            0 -> dataObserver.onDataEmpty()
            else -> dataObserver.onDataNonEmpty()
        }
    }

}