package org.jnanaprabodhini.happyteacher.adapter.firebase

import android.support.v7.widget.RecyclerView
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import org.jnanaprabodhini.happyteacher.adapter.helper.FirebaseDataObserver

/**
 * An override of the FirebaseRecyclerAdapter that allows for
 *  AdapterDataObservers (since it calls `notifyDataSetChanged()`),
 *  and handles our custom FirebaseDataObserver interface calls.
 *
 *  Constructors are based on the hierarchy of constructors
 *  used in the original FirebaseRecyclerAdapter class.
 */

abstract class FirebaseObserverRecyclerAdapter<T, VH: RecyclerView.ViewHolder>(options: FirebaseRecyclerOptions<T>, val dataObserver: FirebaseDataObserver): FirebaseRecyclerAdapter<T, VH>(options) {
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

