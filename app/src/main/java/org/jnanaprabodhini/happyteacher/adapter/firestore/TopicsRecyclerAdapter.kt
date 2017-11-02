package org.jnanaprabodhini.happyteacher.adapter.firestore

import android.app.Activity
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.FirebaseFirestoreException
import org.jnanaprabodhini.happyteacher.R
import org.jnanaprabodhini.happyteacher.adapter.helper.FirebaseDataObserver
import org.jnanaprabodhini.happyteacher.adapter.viewholder.ContentHeaderRecyclerViewHolder
import org.jnanaprabodhini.happyteacher.model.CardListContentHeader
import org.jnanaprabodhini.happyteacher.model.Topic
import org.jnanaprabodhini.happyteacher.view.HorizontalPagerRecyclerView

/**
 * An abstract base adapter for adapters that display lists of Topics (with alternating colors)
 *  with nested lists of child objects related to the Topic.
 *
 * @param C the type of the data model of the Topic's child object to be displayed.
 * @param VH the type of the ViewHolder for the views in the RecyclerView.
 */
abstract class TopicsRecyclerAdapter<C, VH: RecyclerView.ViewHolder>(topicsAdapterOptions: FirestoreRecyclerOptions<Topic>,
                                     topicsDataObserver: FirebaseDataObserver,
                                     val activity: Activity):
        FirestoreObserverRecyclerAdapter<Topic, VH>(topicsAdapterOptions, topicsDataObserver) {

    protected fun setBackgroundColor(view: View?, position: Int) {
        // Alternate between these four colors:
        when (position % 4) {
            0 -> view?.setBackgroundResource(R.color.seaTeal)
            1 -> view?.setBackgroundResource(R.color.grassGreen)
            2 -> view?.setBackgroundResource(R.color.bubbleGumPink)
            3 -> view?.setBackgroundResource(R.color.dreamsicleOrange)
        }
    }

    abstract fun getSubtopicAdapterOptions(topicId: String): FirestoreRecyclerOptions<C>

    abstract fun getSubtopicDataObserverForViewHolder(viewHolder: VH?, level: Int? = null): FirebaseDataObserver
}