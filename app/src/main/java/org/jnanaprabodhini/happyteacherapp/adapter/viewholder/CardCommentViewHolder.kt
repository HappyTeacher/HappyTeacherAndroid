package org.jnanaprabodhini.happyteacherapp.adapter.viewholder

import android.support.v7.widget.RecyclerView
import android.text.format.DateFormat
import android.view.View
import android.widget.TextView
import kotlinx.android.synthetic.main.list_item_comment.view.*
import org.jnanaprabodhini.happyteacherapp.R
import org.jnanaprabodhini.happyteacherapp.extension.setTextColorRes
import org.jnanaprabodhini.happyteacherapp.extension.setVisibilityGone
import org.jnanaprabodhini.happyteacherapp.extension.setVisible
import org.jnanaprabodhini.happyteacherapp.model.CardComment

/**
 * Created by grahamearley on 11/21/17.
 */

class CardCommentViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
    val commenterNameTextView: TextView = itemView.commenterNameTextView
    val commenterRoleTextView: TextView = itemView.commenterRoleTextView
    val commentTimeTextView: TextView = itemView.commentTimeTextView
    val commentTextView: TextView = itemView.commentTextView

    fun setTextColorForReviewer() {
        commenterNameTextView.setTextColorRes(R.color.deepBlue)
        commenterRoleTextView.setTextColorRes(R.color.deepBlue)
        commentTimeTextView.setTextColorRes(R.color.deepBlue)
    }

    fun setTextColorForAuthor() {
        commenterNameTextView.setTextColorRes(R.color.colorPrimary)
        commenterRoleTextView.setTextColorRes(R.color.colorPrimary)
        commentTimeTextView.setTextColorRes(R.color.colorPrimary)
    }
}