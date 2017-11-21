package org.jnanaprabodhini.happyteacherapp.view

import android.content.Context
import android.content.DialogInterface
import android.graphics.Typeface
import android.support.constraint.ConstraintLayout
import android.support.v4.content.res.ResourcesCompat
import android.text.InputType
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.StyleSpan
import android.util.AttributeSet
import android.view.LayoutInflater
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.view_feedback_preview.view.*
import org.jnanaprabodhini.happyteacherapp.R
import org.jnanaprabodhini.happyteacherapp.activity.FeedbackCommentsActivity
import org.jnanaprabodhini.happyteacherapp.dialog.InputTextDialogBuilder
import org.jnanaprabodhini.happyteacherapp.extension.setVisibilityGone
import org.jnanaprabodhini.happyteacherapp.extension.setVisible
import org.jnanaprabodhini.happyteacherapp.model.CardComment
import org.jnanaprabodhini.happyteacherapp.model.ContentCard
import org.jnanaprabodhini.happyteacherapp.util.FirestoreKeys
import org.jnanaprabodhini.happyteacherapp.util.PreferencesManager
import java.util.*

/**
 * Created by grahamearley on 11/21/17.
 */
class FeedbackPreviewView(context: Context, attrs: AttributeSet): ConstraintLayout(context, attrs) {

    var noteText: CharSequence
        get() = noteTextView?.text ?: ""
        set(text) {
            noteTextView?.text = text
        }

    init {
        LayoutInflater.from(context).inflate(R.layout.view_feedback_preview, this)
        this.background = ResourcesCompat.getDrawable(context.resources, R.drawable.ripple_round_corners_accent, null)
    }

    fun setEditableForCard(cardRef: DocumentReference, card: ContentCard) {
        this.setVisible()

        noteText = if (card.feedbackPreviewComment.isEmpty()) {
            val tapToAddFeedbackText = context.getString(R.string.tap_to_add_feedback)
            val italicText = SpannableStringBuilder(tapToAddFeedbackText)
            italicText.setSpan(StyleSpan(Typeface.ITALIC), 0, italicText.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

            italicText
        } else {
            card.feedbackPreviewComment
        }

        // Keep a reference to the most recent comment on the card:
        if (card.feedbackPreviewCommentPath.isEmpty()) {
            card.feedbackPreviewCommentPath = cardRef.collection(FirestoreKeys.COMMENTS).document().path
        }

        setOnClickListener {
            val feedbackDialog = InputTextDialogBuilder(context)

            feedbackDialog.apply {
                setTitle(context.getString(R.string.feedback))

                if (card.feedbackPreviewComment.isNotEmpty()) {
                    setInputText(card.feedbackPreviewComment)
                } else {
                    setInputHint(context.getString(R.string.add_your_feedback))
                }

                setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE)

                setPositiveButton(R.string.save, {dialog, feedbackText ->
                    updateComment(feedbackText, cardRef, card.feedbackPreviewCommentPath)
                    dialog.dismiss()
                })

                setNegativeButton(R.string.cancel, DialogInterface.OnClickListener {
                    dialog, _ -> dialog.dismiss()
                })

                show()
            }
        }

        arrowIcon.setOnClickListener {
            launchCardFeedbackActivity(cardRef)
        }
    }

    fun setReadOnlyForCard(cardRef: DocumentReference, card: ContentCard) {
        if (card.feedbackPreviewComment.isNotEmpty()) {
            this.setVisible()

            noteText = card.feedbackPreviewComment

            arrowIcon.setOnClickListener {
                launchCardFeedbackActivity(cardRef)
            }
        } else {
            this.setVisibilityGone()
        }
    }

    private fun updateComment(commentText: String, cardRef: DocumentReference, commentPath: String) {
        if (commentPath.isEmpty()) return
        val commentRef = FirebaseFirestore.getInstance().document(commentPath)

        val comment = CardComment(
                commenterId = FirebaseAuth.getInstance().currentUser?.uid.orEmpty(),
                commenterName = PreferencesManager.getInstance(context).getUserName(),
                commentText = commentText,
                dateUpdated = Date(),
                reviewerComment = true
        )

        // Update both the comment ref and the card's preview text for the comment
        cardRef.update(mapOf(FirestoreKeys.FEEDBACK_PREVIEW_COMMENT to commentText,
                FirestoreKeys.FEEDBACK_PREVIEW_COMMENT_PATH to commentPath))
        commentRef.set(comment)
    }

    private fun launchCardFeedbackActivity(cardRef: DocumentReference) {
        FeedbackCommentsActivity.launch(context, cardRef)
    }

}