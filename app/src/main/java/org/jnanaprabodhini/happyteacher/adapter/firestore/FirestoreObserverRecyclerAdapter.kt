package org.jnanaprabodhini.happyteacher.adapter.firestore

import android.support.v7.widget.RecyclerView
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import org.jnanaprabodhini.happyteacher.adapter.helper.FirebaseDataObserver

/**
 * An override of the FirestoreRecyclerAdapter that allows for
 *  AdapterDataObservers (since it calls `notifyDataSetChanged()`),
 *  and handles our custom FirestoreDataObserver interface calls.
 *
 *  Constructors are based on the hierarchy of constructors
 *  used in the original FirestoreRecyclerAdapter class.
 */

abstract class FirestoreObserverRecyclerAdapter<T, VH: RecyclerView.ViewHolder>(options: FirestoreRecyclerOptions<T>, val dataObserver: FirebaseDataObserver): FirestoreRecyclerAdapter<T, VH>(options) {
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
}

