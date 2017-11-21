package org.jnanaprabodhini.happyteacherapp.view

import android.content.Context
import android.support.constraint.ConstraintLayout
import android.support.v4.content.res.ResourcesCompat
import android.util.AttributeSet
import android.view.LayoutInflater
import kotlinx.android.synthetic.main.view_feedback_preview.view.*
import org.jnanaprabodhini.happyteacherapp.R

/**
 * Created by grahamearley on 11/21/17.
 */
class FeedbackPreviewView(context: Context, attrs: AttributeSet): ConstraintLayout(context, attrs) {

    var noteText: CharSequence? = noteTextView?.text
        set(text) {
            noteTextView?.text = text
        }

    init {
        LayoutInflater.from(context).inflate(R.layout.view_feedback_preview, this)
        this.background = ResourcesCompat.getDrawable(context.resources, R.drawable.accent_ripple_pill, null)
    }

    fun setEditable() {
        // set empty text view
        // set text on click to show dialog
        // set icon on click to show feedback activity

        // allow callback for onchanged so client can update the data model
    }

    fun setReadOnlyForCard(cardId: String) {
        // set whole view on click to open feedback activity
    }

}