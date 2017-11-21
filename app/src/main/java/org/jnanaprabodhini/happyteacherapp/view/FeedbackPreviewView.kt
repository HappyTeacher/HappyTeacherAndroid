package org.jnanaprabodhini.happyteacherapp.view

import android.app.AlertDialog
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
import android.widget.EditText
import com.google.firebase.firestore.DocumentReference
import kotlinx.android.synthetic.main.view_feedback_preview.view.*
import org.jnanaprabodhini.happyteacherapp.R
import org.jnanaprabodhini.happyteacherapp.dialog.InputTextDialogBuilder
import org.jnanaprabodhini.happyteacherapp.extension.setVisibilityGone
import org.jnanaprabodhini.happyteacherapp.extension.setVisible
import org.jnanaprabodhini.happyteacherapp.extension.showToast
import org.jnanaprabodhini.happyteacherapp.model.CardComment
import org.jnanaprabodhini.happyteacherapp.model.ContentCard
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
        val cardId = cardRef.id

        noteText = if (card.feedbackPreviewComment.isEmpty()) {
            val tapToAddFeedbackText = context.getString(R.string.tap_to_add_feedback)
            val italicText = SpannableStringBuilder(tapToAddFeedbackText)
            italicText.setSpan(StyleSpan(Typeface.ITALIC), 0, italicText.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

            italicText
        } else {
            card.feedbackPreviewComment
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

                setPositiveButton(R.string.save, {dialog, feedbackText ->
                    updateComment(feedbackText, cardRef)
                    dialog.dismiss()
                })

                setNegativeButton(R.string.cancel, DialogInterface.OnClickListener {
                    dialog, _ -> dialog.dismiss()
                })

                show()
            }
        }

        arrowIcon.setOnClickListener {
            launchCardFeedbackActivity(cardId)
        }

        // set text on click to show dialog
        // set icon on click to show feedback activity

        // allow callback for onchanged so client can update the data model
    }

    fun setReadOnlyForCard(cardRef: DocumentReference, card: ContentCard) {
        val cardId = cardRef.id
        if (card.feedbackPreviewComment.isNotEmpty()) {
            this.setVisible()

            noteText = card.feedbackPreviewComment

            arrowIcon.setOnClickListener {
                launchCardFeedbackActivity(cardId)
            }
        } else {
            this.setVisibilityGone()
        }
    }

    private fun updateComment(commentText: String, cardRef: DocumentReference) {
        // TODO: update comment ref from card comment collection. Also update preview comment for immediate feedback (but GCF handles it)
        cardRef.update("feedbackPreviewComment", commentText)
    }

    private fun launchCardFeedbackActivity(cardId: String) {
        context.showToast("Feedback activity coming soon!")
    }

}