package org.jnanaprabodhini.happyteacher.view

import android.content.Context
import android.support.annotation.DimenRes
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import kotlinx.android.synthetic.main.view_recycler_horizontal_pager.view.*
import org.jnanaprabodhini.happyteacher.R
import org.jnanaprabodhini.happyteacher.extension.setElevation
import org.jnanaprabodhini.happyteacher.extension.setVisibilityGone
import org.jnanaprabodhini.happyteacher.extension.setVisible

/**
 * Created by grahamearley on 10/4/17.
 */
class HorizontalPagerRecyclerView(context: Context, attrs: AttributeSet): FrameLayout(context, attrs) {

    val onScrollListener = object: RecyclerView.OnScrollListener() {
        override fun onScrollStateChanged(recyclerView: RecyclerView?, newState: Int) {
            when (newState) {
                RecyclerView.SCROLL_STATE_IDLE -> showRelevantPagers()
                RecyclerView.SCROLL_STATE_DRAGGING -> hidePagers()
                RecyclerView.SCROLL_STATE_SETTLING -> hidePagers()
            }
        }
    }

    val layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

    init {
        LayoutInflater.from(context).inflate(R.layout.view_recycler_horizontal_pager, this)
        recyclerView.layoutManager = layoutManager

        backwardPager.setElevation(R.dimen.pager_button_elevation)
        forwardPager.setElevation(R.dimen.pager_button_elevation)

        showRelevantPagers()
        recyclerView.addOnScrollListener(onScrollListener)

        backwardPager.setOnClickListener {
            val firstVisiblePosition = layoutManager.findFirstVisibleItemPosition()
            val firstCompletelyVisiblePosition = layoutManager.findFirstCompletelyVisibleItemPosition()

            if (firstVisiblePosition != firstCompletelyVisiblePosition) {
                // Then the first visible view is not completely visible. Go there
                recyclerView.smoothScrollToPosition(firstVisiblePosition)
            } else {
                recyclerView.smoothScrollToPosition(firstCompletelyVisiblePosition + 1)
            }
        }

        forwardPager.setOnClickListener {
            val lastVisiblePosition = layoutManager.findLastVisibleItemPosition()
            val lastCompletelyVisiblePosition = layoutManager.findLastCompletelyVisibleItemPosition()

            if (lastVisiblePosition != lastCompletelyVisiblePosition) {
                // Then the last visible view is not completely visible. Go there
                recyclerView.smoothScrollToPosition(lastVisiblePosition)
            } else {
                recyclerView.smoothScrollToPosition(lastCompletelyVisiblePosition - 1)
            }
        }

    }

    fun setAdapter(adapter: RecyclerView.Adapter<*>) {
        recyclerView.adapter = adapter
    }

    fun showRelevantPagers() {
        if (recyclerView.canScrollHorizontally(-1)) {
            backwardPager.setVisible()
        } else {
            backwardPager.setVisibilityGone()
        }

        if (recyclerView.canScrollHorizontally(1)) {
            forwardPager.setVisible()
        } else {
            forwardPager.setVisibilityGone()
        }
    }

    fun hidePagers() {
        backwardPager.setVisibilityGone()
        forwardPager.setVisibilityGone()
    }

}