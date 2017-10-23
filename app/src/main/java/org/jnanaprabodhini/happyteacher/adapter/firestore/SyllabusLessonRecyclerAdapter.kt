package org.jnanaprabodhini.happyteacher.adapter.firestore

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import kotlinx.android.synthetic.main.activity_board_lessons.*
import org.jnanaprabodhini.happyteacher.R
import org.jnanaprabodhini.happyteacher.activity.TopicsListActivity
import org.jnanaprabodhini.happyteacher.activity.base.BottomNavigationActivity
import org.jnanaprabodhini.happyteacher.adapter.helper.FirebaseDataObserver
import org.jnanaprabodhini.happyteacher.adapter.viewholder.SyllabusLessonViewHolder
import org.jnanaprabodhini.happyteacher.extension.jiggle
import org.jnanaprabodhini.happyteacher.extension.showSnackbar
import org.jnanaprabodhini.happyteacher.extension.toLocaleString
import org.jnanaprabodhini.happyteacher.model.SyllabusLesson

/**
 * Created by grahamearley on 10/11/17.
 */
class SyllabusLessonRecyclerAdapter(options: FirestoreRecyclerOptions<SyllabusLesson>, val subjectName: String, dataObserver: FirebaseDataObserver, val activity: BottomNavigationActivity):
        FirestoreObserverRecyclerAdapter<SyllabusLesson, SyllabusLessonViewHolder>(options, dataObserver) {

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): SyllabusLessonViewHolder {
        val view = LayoutInflater.from(parent?.context).inflate(R.layout.list_item_syllabus_lesson, parent, false)
        return SyllabusLessonViewHolder(view)
    }

    override fun onBindViewHolder(holder: SyllabusLessonViewHolder?, position: Int, model: SyllabusLesson?) {
        holder?.lessonTitleTextView?.text = model?.name
        holder?.lessonNumberTextView?.text = model?.lessonNumber?.toLocaleString()
        holder?.topicCountTextView?.text = activity.resources.getQuantityString(R.plurals.topics_count, model?.topicCount ?: 0, model?.topicCount ?: 0)

        holder?.itemView?.setOnClickListener {

            val topicCount = model?.topicCount ?: 0
            if (topicCount == 0) {
                // If there are no topics to display, jiggle the count and tell the user.
                holder.topicCountTextView.jiggle()
                activity.bottomNavigation.showSnackbar(R.string.this_lesson_has_no_relevant_topics)
            } else {
                // Pass syllabus lesson data to the TopicsListActivity
                //  so that it can display the relevant topics (instead
                //  of all topics for that subject).

                val syllabusLessonId = snapshots.getSnapshot(position).reference.id
                val level = model?.level
                val title = model?.name

                TopicsListActivity.launchActivity(activity, syllabusLessonId, subjectName, title ?: "", level ?: 0)
            }
        }
    }

}