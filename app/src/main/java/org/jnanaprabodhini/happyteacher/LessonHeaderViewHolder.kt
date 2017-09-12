package org.jnanaprabodhini.happyteacher

import android.support.v7.widget.RecyclerView
import android.view.View
import kotlinx.android.synthetic.main.list_item_lesson.view.*

/**
 * Created by grahamearley on 9/12/17.
 */
class LessonHeaderViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
    val lessonTitleTextView = itemView.lessonTitleTextView
    val authorNameTextView = itemView.authorNameTextView
    val institutionTextView = itemView.institutionTextView
    val locationTextView = itemView.locationTextView
    val dateEditedTextView = itemView.dateEditedTextView
}