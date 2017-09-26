package org.jnanaprabodhini.happyteacher.viewholder

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.FrameLayout
import android.widget.ProgressBar
import android.widget.TextView
import com.google.android.youtube.player.YouTubeThumbnailView
import kotlinx.android.synthetic.main.list_item_lesson_card.view.*

/**
 * Created by grahamearley on 9/25/17.
 */
class LessonCardViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
    val headerTextView: TextView = itemView.headerTextView
    val bodyTextView: TextView = itemView.bodyTextView
    val youtubeThumnbailView: YouTubeThumbnailView = itemView.youtubeThumbnailView
    val youtubeFrame: FrameLayout = itemView.youtubeFrame // TODO: make better Play button
    val youtubeProgressBar: ProgressBar = itemView.progressBar
}