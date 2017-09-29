package org.jnanaprabodhini.happyteacher.extension

/**
 * Created by grahamearley on 9/29/17.
 */

fun Long.toMegabyteFromByte(): Double {
    return  this / 1000000.toDouble()
}