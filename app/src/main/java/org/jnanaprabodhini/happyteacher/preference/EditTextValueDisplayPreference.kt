package org.jnanaprabodhini.happyteacher.preference

import android.content.Context
import android.support.v7.preference.EditTextPreference
import android.util.AttributeSet

/**
 * Created by grahamearley on 10/30/17.
 */
open class EditTextValueDisplayPreference(context: Context, attrs: AttributeSet): EditTextPreference(context, attrs) {

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
}