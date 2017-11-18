package org.jnanaprabodhini.happyteacherapp.preference

import android.content.Context
import android.support.v7.preference.EditTextPreference
import android.util.AttributeSet
import android.graphics.Color
import android.graphics.Typeface
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import org.jnanaprabodhini.happyteacherapp.R


/**
 * Created by grahamearley on 10/30/17.
 */
open class MandatoryContributorPreference(context: Context, attrs: AttributeSet): EditTextPreference(context, attrs) {

    private var showDialogOnClick = true

    init {
        val attributes = context.theme.obtainStyledAttributes(
                attrs,
                R.styleable.MandatoryContributorPreference,
                0, 0)
        try {
            showDialogOnClick = attributes.getBoolean(R.styleable.MandatoryContributorPreference_showDialogOnClick, true)
        } finally {
            attributes.recycle()
        }

        setOnPreferenceChangeListener { _, _ ->
            this.notifyChanged()
            true
        }
    }

    override fun getSummary(): CharSequence {
        return if (text.isNullOrEmpty()) {
            // Set warning text in red -- this field is mandatory
            val spannableBuilder = SpannableStringBuilder()
            val summary = super.getSummary()
            val mandatoryFieldWarning = context.getString(R.string.required_for_contributors_tap_to_add)

            spannableBuilder.append(summary)
            spannableBuilder.append("\n")
            spannableBuilder.append(mandatoryFieldWarning)

            val warningSpanStart = summary.length
            val warningSpanEnd = spannableBuilder.length

            spannableBuilder.setSpan(ForegroundColorSpan(Color.RED), warningSpanStart, warningSpanEnd, SpannableStringBuilder.SPAN_EXCLUSIVE_EXCLUSIVE)
            spannableBuilder.setSpan(StyleSpan(Typeface.ITALIC), warningSpanStart, warningSpanEnd, SpannableStringBuilder.SPAN_EXCLUSIVE_EXCLUSIVE)

            spannableBuilder
        } else {
            text
        }
    }

    override fun onClick() {
        if (showDialogOnClick) super.onClick()
    }
}