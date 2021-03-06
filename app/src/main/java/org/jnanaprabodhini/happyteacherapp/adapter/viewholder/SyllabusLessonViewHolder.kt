package org.jnanaprabodhini.happyteacherapp.adapter.viewholder

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.TextView
import kotlinx.android.synthetic.main.list_item_syllabus_lesson.view.*

class SyllabusLessonViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
    val lessonNumberTextView: TextView = itemView.lessonNumberTextView
    val lessonTitleTextView: TextView = itemView.titleTextView
    val topicCountTextView: TextView = itemView.topicCountTextView
}