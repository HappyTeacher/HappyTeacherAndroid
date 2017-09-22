package org.jnanaprabodhini.happyteacher.extension

import android.database.DataSetObserver
import android.widget.SpinnerAdapter

/**
 * Created by grahamearley on 9/22/17.
 */

fun SpinnerAdapter.onDataChanged(onChanged: () -> Unit) {
    registerDataSetObserver(object: DataSetObserver() {
        override fun onChanged() {
            onChanged()
        }
    })
}