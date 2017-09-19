package org.jnanaprabodhini.happyteacher

/**
 * Created by grahamearley on 9/18/17.
 */
interface DataObserver {
    fun onRequestNewData() {}

    fun onDataLoaded() {}

    fun onDataEmpty() {}

    fun onDataNonEmpty() {}
}