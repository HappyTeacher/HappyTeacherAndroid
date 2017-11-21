package org.jnanaprabodhini.happyteacherapp.adapter.viewholder

import android.app.Activity
import android.support.constraint.Group
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.*
import com.google.firebase.firestore.DocumentReference
import kotlinx.android.synthetic.main.list_item_content_card.view.*
import org.jnanaprabodhini.happyteacherapp.R
import org.jnanaprabodhini.happyteacherapp.activity.CardEditorActivity
import org.jnanaprabodhini.happyteacherapp.extension.*
import org.jnanaprabodhini.happyteacherapp.model.ContentCard
import org.jnanaprabodhini.happyteacherapp.view.DownloadBarView
import org.jnanaprabodhini.happyteacherapp.view.HorizontalPagerRecyclerView
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

    val imageGalleryRecyclerView: HorizontalPagerRecyclerView = itemView.imageGalleryRecyclerView

    val attachmentDownloadButton: DownloadBarView = itemView.attachmentDownloadBar

    private val editButtonGroup: Group = itemView.editButtonGroup
    private val editButton: TextView = itemView.editButton
    private val deleteButton: TextView = itemView.deleteButton

//    private val commentsButton: ImageView = itemView.commentsButton

    fun setupEditButtons(activity: Activity, cardRef: DocumentReference, cardModel: ContentCard, parentContentId: String) {

        editButton.setDrawableLeft(R.drawable.ic_pencil_white_24dp)
        deleteButton.setDrawableRight(R.drawable.ic_delete_white_24dp)

        editButtonGroup.setVisible()

        editButton.setOnClickListener {
            CardEditorActivity.launch(activity, cardRef, cardModel, parentContentId)
        }

        deleteButton.setOnClickListener {
            // FirebaseUI query refreshes too fast to animate item removals
            //  so for now we run our own animation -- delete item after.
            val exitAnimation = AnimationUtils.loadAnimation(activity, R.anim.slide_out_right_quick)

            exitAnimation.onFinish {
                cardRef.delete()
            }

            itemView.startAnimation(exitAnimation)
        }
    }

    // TODO: revisit parameters..
    fun setupCommentButton(activity: Activity, cardRef: DocumentReference, cardModel: ContentCard, parentContentId: String) {
//        commentsButton.apply {
//            setVisible()
//
//            // todo: add count!
//
//            setOnClickListener {
//                commentsButton.jiggle()
//                activity.showToast("Comments coming soon!")
//            }
//        }
    }

    fun hideEditButtons() {
        editButtonGroup.setVisibilityGone()
    }
}