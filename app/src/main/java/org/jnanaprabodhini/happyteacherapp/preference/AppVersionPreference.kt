package org.jnanaprabodhini.happyteacherapp.preference

import android.content.Context
import android.support.v7.preference.EditTextPreference
import android.util.AttributeSet
import org.jnanaprabodhini.happyteacherapp.BuildConfig


/**
 * Created by grahamearley on 10/30/17.
 */
open class AppVersionPreference(context: Context, attrs: AttributeSet): EditTextPreference(context, attrs) {

    override fun getSummary(): CharSequence = BuildConfig.VERSION_NAME

    override fun onClick() {}
}