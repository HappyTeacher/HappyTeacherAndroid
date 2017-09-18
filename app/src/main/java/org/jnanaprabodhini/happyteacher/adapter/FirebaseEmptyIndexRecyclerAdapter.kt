package org.jnanaprabodhini.happyteacher.adapter

import android.support.annotation.LayoutRes
import android.support.v7.widget.RecyclerView
import com.firebase.ui.database.FirebaseIndexRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.Query

/**
 * Created by grahamearley on 9/18/17.
 */
abstract class FirebaseEmptyIndexRecyclerAdapter<T, VH: RecyclerView.ViewHolder>(modelClass: Class<T>,
                                                                        @LayoutRes modelLayout: Int,
                                                                        viewHolderClass: Class<VH>,
                                                                        keyQuery: Query,
                                                                        dataRef: DatabaseReference): FirebaseIndexRecyclerAdapter<T, VH>(modelClass, modelLayout, viewHolderClass, keyQuery, dataRef) {

    override fun onDataChanged() {
        super.onDataChanged()

        when (itemCount) {
            0 -> onEmpty()
            else -> onNonEmpty()
        }
    }

    abstract fun onEmpty()

    abstract fun onNonEmpty()

}