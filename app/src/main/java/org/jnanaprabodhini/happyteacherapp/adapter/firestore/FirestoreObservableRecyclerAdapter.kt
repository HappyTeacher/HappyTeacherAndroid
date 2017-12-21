package org.jnanaprabodhini.happyteacherapp.adapter.firestore

import android.support.v7.widget.RecyclerView
import com.crashlytics.android.Crashlytics
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.FirebaseFirestoreException
import org.jnanaprabodhini.happyteacherapp.adapter.helper.FirebaseDataObserver

/**
 * An override of the FirestoreRecyclerAdapter that allows for
 *  AdapterDataObservers (since it calls `notifyDataSetChanged()`),
 *  and handles our custom FirestoreDataObserver interface calls.
 */

abstract class FirestoreObservableRecyclerAdapter<T, VH: RecyclerView.ViewHolder>(options: FirestoreRecyclerOptions<T>, val dataObserver: FirebaseDataObserver): FirestoreRecyclerAdapter<T, VH>(options) {

    override fun startListening() {
        super.startListening()
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

    override fun onError(e: FirebaseFirestoreException?) {
        super.onError(e)
        e?.printStackTrace()
        dataObserver.onError(e)
        Crashlytics.logException(e)
    }
}

