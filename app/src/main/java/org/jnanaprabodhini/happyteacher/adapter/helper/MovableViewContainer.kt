package org.jnanaprabodhini.happyteacher.adapter.helper

interface MovableViewContainer {
    fun onViewMoved(oldPosition: Int, newPosition: Int)
    fun onViewSetDown(oldPosition: Int, newPosition: Int)
}