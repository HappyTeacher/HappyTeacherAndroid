package org.jnanaprabodhini.happyteacherapp.adapter.helper

import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper

class RecyclerVerticalDragHelperCallback(movableViewContainer: MovableViewContainer):
        RecyclerDragHelperCallback(movableViewContainer) {

    override fun getMovementFlags(recyclerView: RecyclerView?, viewHolder: RecyclerView.ViewHolder?): Int {
        val dragFlags = ItemTouchHelper.UP or ItemTouchHelper.DOWN
        val swipeFlags = 0 // no swiping
        return makeMovementFlags(dragFlags, swipeFlags)
    }

}