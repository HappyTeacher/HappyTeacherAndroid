package org.jnanaprabodhini.happyteacher.viewholder

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.TextView
import kotlinx.android.synthetic.main.list_item_lesson_card.view.*

/**
 * Created by grahamearley on 9/25/17.
 */
class LessonCardViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
    val headerTextView: TextView = itemView.headerTextView
    val bodyTextView: TextView = itemView.bodyTextView
}