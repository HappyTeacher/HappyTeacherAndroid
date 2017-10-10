package org.jnanaprabodhini.happyteacher.adapter.firebase

import android.support.annotation.LayoutRes
import android.support.v7.widget.RecyclerView
import com.firebase.ui.database.ClassSnapshotParser
import com.firebase.ui.database.FirebaseIndexArray
import com.firebase.ui.database.SnapshotParser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.Query
import org.jnanaprabodhini.happyteacher.adapter.helper.FirebaseDataObserver

/**
 * A custom version of the FirebaseIndexRecyclerAdapter that allows for
 *  AdapterDataObservers (since it calls `notifyDataSetChanged()`),
 *  and handles our custom FirebaseDataObserver interface calls.
 *
 *  Constructors are based on the hierarchy of constructors
 *  used in the original FirebaseIndexRecyclerAdapter class.
 */

abstract class FirebaseObserverIndexRecyclerAdapter<T, VH: RecyclerView.ViewHolder>(parser: SnapshotParser<T>,
                                                                                    @LayoutRes modelLayout: Int,
                                                                                    viewHolderClass: Class<VH>,
                                                                                    keyQuery: Query,
                                                                                    dataRef: DatabaseReference,
                                                                                    dataObserver: FirebaseDataObserver): FirebaseObserverRecyclerAdapter<T, VH>(FirebaseIndexArray<T>(keyQuery, dataRef, parser),
                                                                                                                        modelLayout, viewHolderClass, dataObserver) {


    constructor(modelClass: Class<T>, @LayoutRes modelLayout: Int, viewHolderClass: Class<VH>, keyQuery: Query, dataRef: DatabaseReference, dataObserver: FirebaseDataObserver):
        this(ClassSnapshotParser(modelClass), modelLayout, viewHolderClass, keyQuery, dataRef, dataObserver)

}