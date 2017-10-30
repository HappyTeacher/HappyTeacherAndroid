package org.jnanaprabodhini.happyteacher.preference

import android.content.Context
import android.support.v7.preference.EditTextPreference
import android.util.AttributeSet
import android.content.res.TypedArray
import org.jnanaprabodhini.happyteacher.R


/**
 * Created by grahamearley on 10/30/17.
 */
open class EditTextValueDisplayPreference(context: Context, attrs: AttributeSet): EditTextPreference(context, attrs) {

    private var showDialogOnClick = true

    init {
        val attributes = context.theme.obtainStyledAttributes(
                attrs,
                R.styleable.EditTextValueDisplayPreference,
                0, 0)
        try {
            showDialogOnClick = attributes.getBoolean(R.styleable.EditTextValueDisplayPreference_showDialogOnClick, true)
        } finally {
            attributes.recycle()
        }
    }

    override fun persistString(value: String?): Boolean {
        val persisted = super.persistString(value)
        this.notifyChanged()
        return persisted
    }

    override fun getSummary(): CharSequence {
        return if (text.isNullOrEmpty()) {
            super.getSummary()
        } else {
            text
        }
    }

    override fun onClick() {
        if (showDialogOnClick) super.onClick()
    }
}