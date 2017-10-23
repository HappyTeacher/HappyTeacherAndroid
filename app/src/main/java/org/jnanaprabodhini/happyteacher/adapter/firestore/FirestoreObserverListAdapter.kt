package org.jnanaprabodhini.happyteacher.adapter.firestore

import android.content.Context
import android.database.DataSetObserver
import android.support.annotation.LayoutRes
import android.view.View
import android.view.ViewGroup
import android.widget.ListAdapter
import com.google.firebase.firestore.Query
import org.jnanaprabodhini.happyteacher.R.id.textView
import android.support.v7.widget.RecyclerView.ViewHolder
import android.support.design.widget.CoordinatorLayout.Behavior.setTag
import android.util.Log
import android.view.LayoutInflater
import android.widget.ArrayAdapter
import android.widget.TextView
import com.firebase.ui.common.ChangeEventType
import com.firebase.ui.firestore.ChangeEventListener
import com.firebase.ui.firestore.ClassSnapshotParser
import com.firebase.ui.firestore.FirestoreArray
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.QueryListenOptions
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

    override fun getCount(): Int = snapshots.size

    override fun getItem(position: Int): T = snapshots[position]

    fun getItemKey(position: Int) = snapshots.getSnapshot(position).reference.id

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        if (convertView == null) {
            val view = LayoutInflater.from(context).inflate(layoutId, parent, false)
            populateView(view, getItem(position))
            return view
        } else {
            populateView(convertView, getItem(position))
            return convertView
        }
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = super.getDropDownView(position, convertView, parent)
        populateView(view, snapshots[position])
        return view
    }

    abstract fun populateView(view: View, model: T)

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
        e?.printStackTrace()
        // todo
    }
}