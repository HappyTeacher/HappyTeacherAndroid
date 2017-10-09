package org.jnanaprabodhini.happyteacher.adapter.viewholder

import android.app.Activity
import android.text.format.DateFormat
import com.google.firebase.database.Query
import org.jnanaprabodhini.happyteacher.R
import org.jnanaprabodhini.happyteacher.adapter.FirebaseObserverRecyclerAdapter
import org.jnanaprabodhini.happyteacher.adapter.helper.FirebaseDataObserver
import org.jnanaprabodhini.happyteacher.model.SubtopicLessonHeader

/**
 * Created by grahamearley on 10/9/17.
 */
class SubtopicLessonHeaderRecyclerAdapter(val topicName: String, lessonHeaderQuery: Query, val activity: Activity, firebaseDataObserver: FirebaseDataObserver):
        FirebaseObserverRecyclerAdapter<SubtopicLessonHeader, SubtopicHeaderViewHolder>(SubtopicLessonHeader::class.java, R.layout.list_item_lesson_header, SubtopicHeaderViewHolder::class.java, lessonHeaderQuery, firebaseDataObserver) {

    private val dateFormat by lazy {
        DateFormat.getDateFormat(activity)
    }

    override fun populateViewHolder(subtopicHeaderViewHolder: SubtopicHeaderViewHolder?, subtopicHeaderModel: SubtopicLessonHeader?, lessonHeaderPosition: Int) {
        subtopicHeaderViewHolder?.populateView(subtopicHeaderModel, topicName, activity, dateFormat)
    }
}