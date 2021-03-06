package org.jnanaprabodhini.happyteacherapp.adapter.firestore

import android.view.LayoutInflater
import android.view.ViewGroup
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import kotlinx.android.synthetic.main.activity_board_lessons.*
import org.jnanaprabodhini.happyteacherapp.R
import org.jnanaprabodhini.happyteacherapp.activity.TopicsListActivity
import org.jnanaprabodhini.happyteacherapp.activity.base.BottomNavigationActivity
import org.jnanaprabodhini.happyteacherapp.adapter.helper.FirebaseDataObserver
import org.jnanaprabodhini.happyteacherapp.adapter.viewholder.SyllabusLessonViewHolder
import org.jnanaprabodhini.happyteacherapp.extension.jiggle
import org.jnanaprabodhini.happyteacherapp.extension.showSnackbar
import org.jnanaprabodhini.happyteacherapp.extension.toLocaleString
import org.jnanaprabodhini.happyteacherapp.model.SyllabusLesson

/**
 * Created by grahamearley on 10/11/17.
 */
class SyllabusLessonRecyclerAdapter(options: FirestoreRecyclerOptions<SyllabusLesson>, val subjectName: String, dataObserver: FirebaseDataObserver, val activity: BottomNavigationActivity):
        FirestoreObservableRecyclerAdapter<SyllabusLesson, SyllabusLessonViewHolder>(options, dataObserver) {

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

                TopicsListActivity.launch(activity, syllabusLessonId, subjectName, title ?: "", level ?: 0)
            }
        }
    }

}