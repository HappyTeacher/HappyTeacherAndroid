package org.jnanaprabodhini.happyteacher.extension

import android.app.Activity
import android.support.v4.os.ConfigurationCompat
import java.util.*

/**
 * Created by grahamearley on 9/12/17.
 */

fun Activity.getPrimaryLocale(): Locale = ConfigurationCompat.getLocales(resources.configuration)[0]