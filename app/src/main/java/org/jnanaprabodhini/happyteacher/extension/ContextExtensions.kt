package org.jnanaprabodhini.happyteacher.extension

import android.content.Context
import android.support.annotation.StringRes
import android.widget.Toast
import org.jnanaprabodhini.happyteacher.util.LocaleManager

/**
 * Extension functions for Activities and Context.
 */

fun Context.showToast(text: String, length: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, text, length).show()
}

fun Context.showToast(@StringRes stringId: Int, length: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, stringId, length).show()
}

fun Context.withCurrentLocale(): Context {
    return LocaleManager.setLocale(this)
}