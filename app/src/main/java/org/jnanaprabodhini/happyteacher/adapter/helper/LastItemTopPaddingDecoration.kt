package org.jnanaprabodhini.happyteacher.adapter.helper

import android.graphics.Rect
import android.support.v7.widget.RecyclerView
import android.view.View
import org.jnanaprabodhini.happyteacher.R

/**
 * A RecyclerView decoration that puts padding at the top of the last item in the list.
 */
class LastItemTopPaddingDecoration : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(outRect: Rect?, view: View?, parent: RecyclerView?, state: RecyclerView.State?) {
        super.getItemOffsets(outRect, view, parent, state)

        parent?.let {
            val position = parent.getChildAdapterPosition(view)
            val itemCount = parent.adapter.itemCount

            if (position == itemCount - 1) {
                outRect?.top = parent.resources.getDimensionPixelOffset(R.dimen.classroom_resources_list_top_padding)
            }
        }

    }

}