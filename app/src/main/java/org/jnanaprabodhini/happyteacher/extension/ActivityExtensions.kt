package org.jnanaprabodhini.happyteacher.extension

import android.app.Activity
import android.support.annotation.StringRes
import android.support.v4.os.ConfigurationCompat
import android.widget.Toast
import java.util.*

/**
 * Extension functions for Activities.
 */

fun Activity.showToast(@StringRes stringId: Int, length: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, stringId, length).show()
}