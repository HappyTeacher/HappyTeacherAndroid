package org.jnanaprabodhini.happyteacherapp.adapter.viewholder

import android.app.Activity
import android.support.constraint.Group
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.*
import com.google.firebase.firestore.DocumentReference
import kotlinx.android.synthetic.main.list_item_content_card.view.*
import org.jnanaprabodhini.happyteacherapp.R
import org.jnanaprabodhini.happyteacherapp.activity.CardEditorActivity
import org.jnanaprabodhini.happyteacherapp.extension.*
import org.jnanaprabodhini.happyteacherapp.model.ContentCard
import org.jnanaprabodhini.happyteacherapp.view.DownloadBarView
import org.jnanaprabodhini.happyteacherapp.view.FeedbackPreviewView
import org.jnanaprabodhini.happyteacherapp.view.HorizontalPagerView
import org.jnanaprabodhini.happyteacherapp.view.YoutubeWebView

/**
 * Created by grahamearley on 9/25/17.
 */
open class ContentCardViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
    val headerTextView: TextView = itemView.headerTextView
    val bodyTextView: TextView = itemView.bodyTextView

    val headerMediaFrame: FrameLayout = itemView.headerMediaFrame
    val headerImageView: ImageView = itemView.headerImageView
    val youtubeWebView: YoutubeWebView = itemView.youtubePlayerWebView
    val headerProgressBar: ProgressBar = itemView.headerProgressBar
    val loadButton: TextView = itemView.loadButton

    val imageGalleryPagerView: HorizontalPagerView = itemView.imageGalleryRecyclerView

    val attachmentDownloadButton: DownloadBarView = itemView.attachmentDownloadBar

    private val editButtonGroup: Group = itemView.editButtonGroup
    private val editButton: TextView = itemView.editButton
    private val deleteButton: TextView = itemView.deleteButton

    private val feedbackPreview: FeedbackPreviewView = itemView.feedbackPreviewView

    fun setupEditButtons(activity: Activity, cardRef: DocumentReference, cardModel: ContentCard, parentContentId: String) {

        editButton.setDrawableLeft(R.drawable.ic_pencil_white_24dp)
        deleteButton.setDrawableLeft(R.drawable.ic_delete_white_24dp)

        editButtonGroup.setVisible()

        editButton.setOnClickListener {
            CardEditorActivity.launch(activity, cardRef, cardModel, parentContentId)
        }

        deleteButton.setOnClickListener {
            cardRef.slideOutViewAndDelete(activity, itemView)
        }
    }

    fun setFeedbackEditableForCard(cardRef: DocumentReference, card: ContentCard) {
        feedbackPreview.setEditableForCard(cardRef, card)
    }

    fun setFeedbackDisplayForCard(cardRef: DocumentReference, card: ContentCard) {
        feedbackPreview.setReadOnlyForCard(cardRef, card)
    }
}