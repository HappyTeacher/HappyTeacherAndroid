package org.jnanaprabodhini.happyteacher.adapter

import android.app.Activity
import android.text.format.DateFormat
import com.google.firebase.database.Query
import org.jnanaprabodhini.happyteacher.R
import org.jnanaprabodhini.happyteacher.adapter.helper.FirebaseDataObserver
import org.jnanaprabodhini.happyteacher.adapter.viewholder.CardListHeaderViewHolder
import org.jnanaprabodhini.happyteacher.adapter.viewholder.ClassroomResourcesHeaderViewHolder
import org.jnanaprabodhini.happyteacher.adapter.viewholder.LessonHeaderViewHolder
import org.jnanaprabodhini.happyteacher.model.CardListContentHeader

/**
 * An abstract adapter for a list of cards showing header information for content lists.
 */
abstract class CardListContentHeaderRecyclerAdapter<VH: CardListHeaderViewHolder>(val topicName: String, contentQuery: Query, val activity: Activity, viewholderClass: Class<VH>, firebaseDataObserver: FirebaseDataObserver):
        FirebaseObserverRecyclerAdapter<CardListContentHeader, VH>(CardListContentHeader::class.java, R.layout.list_item_lesson_header, viewholderClass, contentQuery, firebaseDataObserver) {

    private val dateFormat by lazy {
        DateFormat.getDateFormat(activity)
    }

    override fun populateViewHolder(cardListHeaderViewHolder: VH?, cardListContentHeaderModel: CardListContentHeader?, lessonHeaderPosition: Int) {
        cardListHeaderViewHolder?.populateView(cardListContentHeaderModel, topicName, activity, dateFormat)
    }
}

/**
 * A CardListContentHeaderRecyclerAdapter implementation for Lesson headers.
 */
class LessonHeaderRecyclerAdapter(topicName: String, lessonHeaderQuery: Query, activity: Activity, firebaseDataObserver: FirebaseDataObserver):
        CardListContentHeaderRecyclerAdapter<LessonHeaderViewHolder>(topicName, lessonHeaderQuery, activity, LessonHeaderViewHolder::class.java, firebaseDataObserver)

/**
 * A CardListContentHeaderRecyclerAdapter implementation for classroom resources.
 */
class ClassroomResourcesHeaderRecyclerAdapter(topicName: String, lessonHeaderQuery: Query, activity: Activity, firebaseDataObserver: FirebaseDataObserver):
        CardListContentHeaderRecyclerAdapter<ClassroomResourcesHeaderViewHolder>(topicName, lessonHeaderQuery, activity, ClassroomResourcesHeaderViewHolder::class.java, firebaseDataObserver)