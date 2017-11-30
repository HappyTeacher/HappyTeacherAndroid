package org.jnanaprabodhini.happyteacherapp.adapter.firestore

import android.support.v7.widget.RecyclerView
import android.view.View
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import org.jnanaprabodhini.happyteacherapp.R
import org.jnanaprabodhini.happyteacherapp.activity.base.HappyTeacherActivity
import org.jnanaprabodhini.happyteacherapp.adapter.helper.FirebaseDataObserver
import org.jnanaprabodhini.happyteacherapp.model.Topic

/**
 * An abstract base adapter for adapters that display lists of Topics (with alternating colors)
 *  with nested lists of child objects related to the Topic.
 *
 * @param VH the type of the ViewHolder for the views in the RecyclerView.
 */
abstract class TopicsRecyclerAdapter<VH: RecyclerView.ViewHolder>(topicsAdapterOptions: FirestoreRecyclerOptions<Topic>,
                                     topicsDataObserver: FirebaseDataObserver,
                                     val activity: HappyTeacherActivity):
        FirestoreObservableRecyclerAdapter<Topic, VH>(topicsAdapterOptions, topicsDataObserver) {

    protected fun setBackgroundColor(view: View?, position: Int) {
        // Alternate between these four colors:
        when (position % 4) {
            0 -> view?.setBackgroundResource(R.color.seaTeal)
            1 -> view?.setBackgroundResource(R.color.grassGreen)
            2 -> view?.setBackgroundResource(R.color.bubbleGumPink)
            3 -> view?.setBackgroundResource(R.color.dreamsicleOrange)
        }
    }

    fun indexOfTopicOrNull(topicId: String): Int? {
        for (i in 0..snapshots.lastIndex) {
            val snapshot = snapshots.getSnapshot(i)
            val snapshotId = snapshot.id
            if (snapshotId == topicId) {
               return i
            }
        }
        return null
    }
}