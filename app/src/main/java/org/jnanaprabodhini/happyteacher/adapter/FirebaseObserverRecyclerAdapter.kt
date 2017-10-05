package org.jnanaprabodhini.happyteacher.adapter

import android.arch.lifecycle.LifecycleOwner
import android.support.annotation.LayoutRes
import android.support.v7.widget.RecyclerView
import com.firebase.ui.database.*
import com.google.firebase.database.Query

/**
 * An override of the FirebaseRecyclerAdapter that allows for
 *  AdapterDataObservers (since it calls `notifyDataSetChanged()`),
 *  and handles our custom FirebaseDataObserver interface calls.
 *
 *  Constructors are based on the hierarchy of constructors
 *  used in the original FirebaseRecyclerAdapter class.
 */

abstract class FirebaseObserverRecyclerAdapter<T, VH: RecyclerView.ViewHolder>(snapshots: ObservableSnapshotArray<T>,
                                                                               @LayoutRes modelLayout: Int,
                                                                               viewHolderClass: Class<VH>,
                                                                               owner: LifecycleOwner?,
                                                                               val dataObserver: FirebaseDataObserver): FirebaseRecyclerAdapter<T, VH>(snapshots, modelLayout, viewHolderClass, owner) {
    init {
        dataObserver.onRequestNewData()
    }

    override fun onDataChanged() {
        super.onDataChanged()
        notifyDataSetChanged()

        dataObserver.onDataLoaded()

        when (itemCount) {
            0 -> dataObserver.onDataEmpty()
            else -> dataObserver.onDataNonEmpty()
        }
    }

    constructor(snapshots: ObservableSnapshotArray<T>, @LayoutRes modelLayout: Int, viewHolderClass: Class<VH>, dataObserver: FirebaseDataObserver):
        this(snapshots, modelLayout, viewHolderClass, null, dataObserver) {
            startListening()
    }

    constructor(parser: SnapshotParser<T>, @LayoutRes modelLayout: Int, viewHolderClass: Class<VH>, query: Query, dataObserver: FirebaseDataObserver):
        this(FirebaseArray(query, parser), modelLayout, viewHolderClass, dataObserver)

    constructor(modelClass: Class<T>, @LayoutRes modelLayout: Int, viewHolderClass: Class<VH>, query: Query, dataObserver: FirebaseDataObserver):
            this(ClassSnapshotParser<T>(modelClass), modelLayout, viewHolderClass, query, dataObserver)
}