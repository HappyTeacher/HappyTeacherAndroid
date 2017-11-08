package org.jnanaprabodhini.happyteacher.adapter.viewholder

import android.app.Activity
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.*
import com.google.firebase.firestore.DocumentReference
import kotlinx.android.synthetic.main.list_item_content_card.view.*
import org.jnanaprabodhini.happyteacher.R
import org.jnanaprabodhini.happyteacher.activity.CardEditorActivity
import org.jnanaprabodhini.happyteacher.extension.setVisibilityGone
import org.jnanaprabodhini.happyteacher.extension.setVisible
import org.jnanaprabodhini.happyteacher.extension.showSnackbar
import org.jnanaprabodhini.happyteacher.model.ContentCard
import org.jnanaprabodhini.happyteacher.view.DownloadBarView
import org.jnanaprabodhini.happyteacher.view.HorizontalPagerRecyclerView
import org.jnanaprabodhini.happyteacher.view.YoutubeWebView

/**
 * Created by grahamearley on 9/25/17.
 */
open class ContentCardViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
    val headerTextView: TextView = itemView.headerEditText
    val bodyTextView: TextView = itemView.bodyEditText

    val headerMediaFrame: FrameLayout = itemView.headerMediaFrame
    val headerImageView: ImageView = itemView.headerImageView
    val youtubeWebView: YoutubeWebView = itemView.youtubePlayerWebView
    val headerProgressBar: ProgressBar = itemView.headerProgressBar
    val loadButton: TextView = itemView.loadButton

    val imageGalleryRecyclerView: HorizontalPagerRecyclerView = itemView.imageGalleryRecyclerView

    val attachmentDownloadButton: DownloadBarView = itemView.attachmentDownloadBar

    private val editButton: ImageButton = itemView.editButton
    private val deleteButton: ImageButton = itemView.deleteButton

    fun setupEditButtons(activity: Activity, cardRef: DocumentReference, cardModel: ContentCard, subtopicId: String) {
        editButton.setVisible()
        deleteButton.setVisible()

        editButton.setOnClickListener {
            CardEditorActivity.launch(activity, cardRef, cardModel, subtopicId)
        }

        deleteButton.setOnClickListener {
            cardRef.delete()
        }
    }

    fun hideEditButtons() {
        editButton.setVisibilityGone()
        deleteButton.setVisibilityGone()
    }
}