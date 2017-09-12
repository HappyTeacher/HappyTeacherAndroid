package org.jnanaprabodhini.happyteacher

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.TextView
import kotlinx.android.synthetic.main.list_item_lesson.view.*

/**
 * Created by grahamearley on 9/12/17.
 */
class LessonHeaderViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
    val lessonTitleTextView: TextView = itemView.lessonTitleTextView
    val authorNameTextView: TextView = itemView.authorNameTextView
    val institutionTextView: TextView = itemView.institutionTextView
    val locationTextView: TextView = itemView.locationTextView
    val dateEditedTextView: TextView = itemView.dateEditedTextView
}