package org.jnanaprabodhini.happyteacher.adapter.firebase

import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import com.firebase.ui.database.FirebaseRecyclerOptions
import kotlinx.android.synthetic.main.activity_board_lessons.*
import org.jnanaprabodhini.happyteacher.R
import org.jnanaprabodhini.happyteacher.activity.TopicsListActivity
import org.jnanaprabodhini.happyteacher.activity.parent.BottomNavigationActivity
import org.jnanaprabodhini.happyteacher.adapter.helper.FirebaseDataObserver
import org.jnanaprabodhini.happyteacher.adapter.viewholder.SyllabusLessonViewHolder
import org.jnanaprabodhini.happyteacher.extension.jiggle
import org.jnanaprabodhini.happyteacher.extension.showSnackbar
import org.jnanaprabodhini.happyteacher.model.SyllabusLesson

/**
 * Created by grahamearley on 10/11/17.
 */
class SyllabusLessonRecyclerAdapter(options: FirebaseRecyclerOptions<SyllabusLesson>, dataObserver: FirebaseDataObserver, val activity: BottomNavigationActivity):
        FirebaseObserverRecyclerAdapter<SyllabusLesson, SyllabusLessonViewHolder>(options, dataObserver) {

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): SyllabusLessonViewHolder {
        val view = LayoutInflater.from(parent?.context).inflate(R.layout.list_item_syllabus_lesson, parent, false)
        return SyllabusLessonViewHolder(view)
    }

    override fun onBindViewHolder(holder: SyllabusLessonViewHolder?, position: Int, model: SyllabusLesson?) {
        holder?.lessonTitleTextView?.text = model?.name
        holder?.lessonNumberTextView?.text = model?.lessonNumber.toString()
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

                val keyUrl = getRef(position).child(activity.getString(R.string.topics)).toString()
                val subject = model?.subject
                val level = model?.level
                val title = model?.name

                TopicsListActivity.launchActivity(activity, keyUrl, subject ?: "", title ?: "", level ?: 0)
            }
        }
    }

}