package org.jnanaprabodhini.happyteacher.viewholder

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import kotlinx.android.synthetic.main.list_item_lesson_card.view.*

/**
 * Created by grahamearley on 9/25/17.
 */
open class LessonCardViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
    val headerTextView: TextView = itemView.headerTextView
    val bodyTextView: TextView = itemView.bodyTextView
    val youtubeThumnbailImageView: ImageView = itemView.youtubeThumbnailImageView
    val youtubeFrame: FrameLayout = itemView.youtubeFrame
    val youtubePlayButton: View = itemView.youtubePlayButton
}