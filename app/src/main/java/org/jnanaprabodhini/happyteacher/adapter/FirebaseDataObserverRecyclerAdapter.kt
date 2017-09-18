package org.jnanaprabodhini.happyteacher.adapter

import android.support.annotation.LayoutRes
import android.support.v7.widget.RecyclerView
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.google.firebase.database.Query

/**
 * An extension of the FirebaseRecyclerAdapter that provides callbacks
 *  for certain data change events.
 */
abstract class FirebaseDataObserverRecyclerAdapter<T, VH: RecyclerView.ViewHolder>(modelClass: Class<T>,
                                                                                   @LayoutRes modelLayout: Int,
                                                                                   viewHolderClass: Class<VH>,
                                                                                   query: Query): FirebaseRecyclerAdapter<T, VH>(modelClass, modelLayout, viewHolderClass,query) {
    override fun onDataChanged() {
        super.onDataChanged()

        onFinishedLoading()

        when (itemCount) {
            0 -> onEmpty()
            else -> onNonEmpty()
        }
    }

    abstract fun onFinishedLoading()

    abstract fun onEmpty()

    abstract fun onNonEmpty()

}