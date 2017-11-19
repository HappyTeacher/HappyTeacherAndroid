package org.jnanaprabodhini.happyteacherapp.extension

import android.support.v7.widget.RecyclerView

/**
 * Created by grahamearley on 10/11/17.
 */

fun RecyclerView.Adapter<*>.onDataChanged(onChanged: () -> Unit) {
    registerAdapterDataObserver(object: RecyclerView.AdapterDataObserver() {
        override fun onChanged() {
            onChanged()
        }
    })
}