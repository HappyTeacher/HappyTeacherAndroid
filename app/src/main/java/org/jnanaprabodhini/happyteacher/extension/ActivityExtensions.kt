package org.jnanaprabodhini.happyteacher.extension

import android.app.Activity
import android.support.v4.os.ConfigurationCompat
import java.util.*

/**
 * Extension functions for Activities.
 */

fun Activity.getPrimaryLocale(): Locale = ConfigurationCompat.getLocales(resources.configuration)[0]

fun Activity.getPrimaryLanguageCode(): String = getPrimaryLocale().language