package org.jnanaprabodhini.happyteacher.adapter.firestore

import android.content.Context
import android.support.annotation.LayoutRes
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.LayoutInflater
import android.widget.ArrayAdapter
import android.widget.ListView
import com.crashlytics.android.Crashlytics
import com.firebase.ui.common.ChangeEventType
import com.firebase.ui.firestore.ChangeEventListener
import com.firebase.ui.firestore.ClassSnapshotParser
import com.firebase.ui.firestore.FirestoreArray
import com.google.firebase.firestore.*
import org.jnanaprabodhini.happyteacher.adapter.helper.FirebaseDataObserver


/**
 * Created by grahamearley on 10/23/17.
 */
abstract class FirestoreObserverListAdapter<T>(query: Query, modelClass: Class<T>, @LayoutRes val layoutId: Int, val dataObserver: FirebaseDataObserver, context: Context):
        ArrayAdapter<T>(context, layoutId),
        ChangeEventListener {

    val snapshots = FirestoreArray<T>(query, QueryListenOptions(), ClassSnapshotParser<T>(modelClass))

    fun startListening() {
        if (!snapshots.isListening(this)) {
            dataObserver.onRequestNewData()
            snapshots.addChangeEventListener(this)
        }
    }

    fun stopListening() {
        snapshots.removeChangeEventListener(this)
        notifyDataSetChanged()
    }

    override fun getCount(): Int = snapshots.size

    override fun getItem(position: Int): T = snapshots[position]

    fun getItemRef(position: Int): DocumentReference = snapshots.getSnapshot(position).reference

    fun getItemKey(position: Int) = getItemRef(position).id

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        if (convertView == null) {
            val view = LayoutInflater.from(context).inflate(layoutId, parent, false)
            populateView(view, getItem(position), position)
            return view
        } else {
            populateView(convertView, getItem(position), position)
            return convertView
        }
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = super.getDropDownView(position, convertView, parent)
        populateView(view, snapshots[position], position)
        return view
    }

    abstract fun populateView(view: View, model: T, position: Int)

    override fun onDataChanged() {
        dataObserver.onDataLoaded()

        when (count) {
            0 -> dataObserver.onDataEmpty()
            else -> dataObserver.onDataNonEmpty()
        }

    }

    override fun onChildChanged(type: ChangeEventType?, snapshot: DocumentSnapshot?, newIndex: Int, oldIndex: Int) {
        notifyDataSetChanged()
    }

    override fun onError(e: FirebaseFirestoreException?) {
        dataObserver.onError(e)
        Crashlytics.logException(e)
    }
}