package org.jnanaprabodhini.happyteacher.extension

import java.text.NumberFormat

/**
 * Created by grahamearley on 9/29/17.
 */

fun Long.toMegabyteFromByte(): Double {
    return  this / 1000000.toDouble()
}

fun Int.toLocaleString(): CharSequence? {
    return NumberFormat.getInstance().format(this)
}