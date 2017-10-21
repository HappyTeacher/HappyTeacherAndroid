package org.jnanaprabodhini.happyteacher.adapter.firestore

import com.firebase.ui.database.FirebaseListAdapter
import com.firebase.ui.database.FirebaseListOptions
import org.jnanaprabodhini.happyteacher.adapter.helper.FirebaseDataObserver

/**
 * A convenience near-duplicate of FirebaseObserverRecyclerAdapter.
 *  Same functionality, but for list adapters.
 */
abstract class FirebaseObserverListAdapter<T>(options: FirebaseListOptions<T>, val dataObserver: FirebaseDataObserver): FirebaseListAdapter<T>(options) {
    init {
        dataObserver.onRequestNewData()
    }

    override fun onDataChanged() {
        super.onDataChanged()
        notifyDataSetChanged()

        dataObserver.onDataLoaded()

        when (count) {
            0 -> dataObserver.onDataEmpty()
            else -> dataObserver.onDataNonEmpty()
        }
    }
}