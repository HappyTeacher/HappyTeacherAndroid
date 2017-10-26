package org.jnanaprabodhini.happyteacher.adapter.firestore

import android.app.Activity
import android.text.format.DateFormat
import android.view.ViewGroup
import org.jnanaprabodhini.happyteacher.R
import org.jnanaprabodhini.happyteacher.adapter.helper.FirebaseDataObserver
import org.jnanaprabodhini.happyteacher.adapter.viewholder.CardListHeaderViewHolder
import org.jnanaprabodhini.happyteacher.adapter.viewholder.ClassroomResourcesHeaderViewHolder
import org.jnanaprabodhini.happyteacher.adapter.viewholder.LessonHeaderViewHolder
import org.jnanaprabodhini.happyteacher.model.CardListContentHeader
import android.view.LayoutInflater
import android.view.View
import com.firebase.ui.firestore.FirestoreRecyclerOptions


/**
 * An abstract adapter for a list of cards showing header information for content lists.
 */
abstract class CardListContentHeaderRecyclerAdapter<VH: CardListHeaderViewHolder>(options: FirestoreRecyclerOptions<CardListContentHeader>, val activity: Activity, val topicName: String, firebaseDataObserver: FirebaseDataObserver):
        FirestoreObserverRecyclerAdapter<CardListContentHeader, VH>(options, firebaseDataObserver) {

    private val dateFormat by lazy {
        DateFormat.getDateFormat(activity)
    }

    override fun onBindViewHolder(holder: VH, position: Int, model: CardListContentHeader?) {
        val cardRef = snapshots.getSnapshot(position).reference.collection(activity.getString(R.string.cards))
        holder.populateView(model, cardRef, topicName, activity, dateFormat)
    }

    fun inflateView(parent: ViewGroup?): View {
        return LayoutInflater.from(parent?.context)
                .inflate(R.layout.list_item_content_header_card, parent, false)
    }

}

/**
 * A CardListContentHeaderRecyclerAdapter implementation for Lesson headers.
 */
class LessonHeaderRecyclerAdapter(topicName: String, val shouldShowSubmissionCount: Boolean, options: FirestoreRecyclerOptions<CardListContentHeader>, activity: Activity, firebaseDataObserver: FirebaseDataObserver):
        CardListContentHeaderRecyclerAdapter<LessonHeaderViewHolder>(options, activity, topicName, firebaseDataObserver) {

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): LessonHeaderViewHolder {
        return LessonHeaderViewHolder(inflateView(parent), shouldShowSubmissionCount)
    }
}

/**
 * A CardListContentHeaderRecyclerAdapter implementation for classroom resources.
 */
class ClassroomResourcesHeaderRecyclerAdapter(topicName: String, options: FirestoreRecyclerOptions<CardListContentHeader>, activity: Activity, firebaseDataObserver: FirebaseDataObserver):
        CardListContentHeaderRecyclerAdapter<ClassroomResourcesHeaderViewHolder>(options, activity, topicName, firebaseDataObserver) {

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ClassroomResourcesHeaderViewHolder {
        return ClassroomResourcesHeaderViewHolder(inflateView(parent))
    }
}