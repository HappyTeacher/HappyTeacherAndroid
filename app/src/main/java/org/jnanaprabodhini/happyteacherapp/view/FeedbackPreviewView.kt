package org.jnanaprabodhini.happyteacherapp.view

import android.content.Context
import android.graphics.Typeface
import android.support.constraint.ConstraintLayout
import android.support.v4.content.res.ResourcesCompat
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.StyleSpan
import android.util.AttributeSet
import android.view.LayoutInflater
import kotlinx.android.synthetic.main.view_feedback_preview.view.*
import org.jnanaprabodhini.happyteacherapp.R
import org.jnanaprabodhini.happyteacherapp.extension.showToast

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
        this.background = ResourcesCompat.getDrawable(context.resources, R.drawable.accent_ripple_pill, null)
    }

    fun setEditableForCard(cardId: String) {
        if (noteText.isEmpty()) {
            val tapToAddFeedbackText = context.getString(R.string.tap_to_add_feedback)
            val italicText = SpannableStringBuilder(tapToAddFeedbackText)
            italicText.setSpan(StyleSpan(Typeface.ITALIC), 0, italicText.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

            noteTextView.text = italicText
        }

        setOnClickListener {
            context.showToast("Dialog coming soon!")
        }

        actionIcon.setOnClickListener {
            launchCardFeedbackActivity(cardId)
        }

        // set text on click to show dialog
        // set icon on click to show feedback activity

        // allow callback for onchanged so client can update the data model
    }

    fun setReadOnlyForCard(cardId: String) {
        actionIcon.setOnClickListener {
            launchCardFeedbackActivity(cardId)
        }
    }

    fun launchCardFeedbackActivity(cardId: String) {
        context.showToast("Feedback activity coming soon!")
    }

}