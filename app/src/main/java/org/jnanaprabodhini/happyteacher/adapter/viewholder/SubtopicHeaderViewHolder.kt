package org.jnanaprabodhini.happyteacher.adapter.viewholder

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.TextView
import kotlinx.android.synthetic.main.list_item_lesson_header.view.*

/**
 * Created by grahamearley on 9/12/17.
 */
class SubtopicHeaderViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
    val lessonTitleTextView: TextView = itemView.lessonTitleTextView
    val authorNameTextView: TextView = itemView.authorNameTextView
    val institutionTextView: TextView = itemView.institutionTextView
    val locationTextView: TextView = itemView.locationTextView
    val dateEditedTextView: TextView = itemView.dateEditedTextView
    val submissionCountTextView: TextView = itemView.submissionCountTextView
}