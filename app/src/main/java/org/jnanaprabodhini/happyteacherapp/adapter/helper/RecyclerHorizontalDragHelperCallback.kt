package org.jnanaprabodhini.happyteacherapp.adapter.helper

import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper

class RecyclerHorizontalDragHelperCallback(movableViewContainer: MovableViewContainer):
        RecyclerDragHelperCallback(movableViewContainer) {

    override fun getMovementFlags(recyclerView: RecyclerView?, viewHolder: RecyclerView.ViewHolder?): Int {
        val dragFlags = ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        val swipeFlags = 0 // no swiping
        return makeMovementFlags(dragFlags, swipeFlags)
    }

}