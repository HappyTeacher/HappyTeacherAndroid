package org.jnanaprabodhini.happyteacherapp.adapter.helper

import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper

/**
 * A callback for ItemTouchHelper that allows for views to be dragged. Implementations
 *  of this abstract class determine the directions in which views can be dragged.
 *
 *  This callback alerts a MovableViewContainer (e.g. an adapter) when a view is
 *  moved, and when a view is "set down" -- after the move is complete.
 *
 */
abstract class RecyclerDragHelperCallback(private val movableViewContainer: MovableViewContainer) : ItemTouchHelper.Callback() {

    private var dragItemInitialPosition: Int? = null
    private var targetItemPosition: Int? = null

    override fun onMove(recyclerView: RecyclerView?, viewHolder: RecyclerView.ViewHolder?, target: RecyclerView.ViewHolder?): Boolean {
        val oldPosition = viewHolder?.adapterPosition
        val newPosition = target?.adapterPosition

        return if (oldPosition != null && newPosition != null
                    && oldPosition != newPosition) {

            // If the item being dragged has already been
            //  stored, then don't change it. Maintain
            //  the *initial* position of the item, not
            //  the position that gets updated as it moves
            if (dragItemInitialPosition == null)
                dragItemInitialPosition = oldPosition

            targetItemPosition = newPosition
            movableViewContainer.onViewMoved(oldPosition, newPosition)

            true
        } else {
            false
        }
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder?, direction: Int) {}
    override fun isItemViewSwipeEnabled() = false

    /**
     * According to docs:
     *  "Called by the ItemTouchHelper when the user interaction
     *  with an element is over and it also completed its animation."
     *
     *  We use this method to trigger our movable view container's `onViewSetDown`
     *   so that the item rearranging only occurs after the move is complete.
     *
     */
    override fun clearView(recyclerView: RecyclerView?, viewHolder: RecyclerView.ViewHolder?) {
        super.clearView(recyclerView, viewHolder)

        dragItemInitialPosition?.let { oldPosition ->
            targetItemPosition?.let { newPosition ->
                movableViewContainer.onViewSetDown(oldPosition, newPosition)
            }
        }

        dragItemInitialPosition = null
        targetItemPosition = null
    }

}

