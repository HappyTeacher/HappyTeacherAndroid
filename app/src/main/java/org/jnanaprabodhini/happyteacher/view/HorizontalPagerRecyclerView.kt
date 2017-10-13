package org.jnanaprabodhini.happyteacher.view

import android.content.Context
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import kotlinx.android.synthetic.main.view_recycler_horizontal_pager.view.*
import org.jnanaprabodhini.happyteacher.R
import org.jnanaprabodhini.happyteacher.extension.*
import android.animation.LayoutTransition


/**
 * A horizontal RecyclerView wrapper that has forward and backward pager buttons
 *  for scrolling content.
 */
class HorizontalPagerRecyclerView(context: Context, attrs: AttributeSet): FrameLayout(context, attrs) {

    val onScrollListener = object: RecyclerView.OnScrollListener() {
        override fun onScrollStateChanged(recyclerView: RecyclerView?, newState: Int) {
            when (newState) {
                RecyclerView.SCROLL_STATE_IDLE -> showRelevantPagers()
                RecyclerView.SCROLL_STATE_DRAGGING -> hidePagers()
            }
        }
    }

    val layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

    init {
        View.inflate(context, R.layout.view_recycler_horizontal_pager, this)
        layoutTransition = LayoutTransition()

        recyclerView.layoutManager = layoutManager
        recyclerView.setHasFixedSize(true)

        backwardPager.setElevation(R.dimen.pager_button_elevation)
        forwardPager.setElevation(R.dimen.pager_button_elevation)

        setPagerClickListeners()
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        recyclerView.addOnScrollListener(onScrollListener)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        recyclerView.removeOnScrollListener(onScrollListener)
    }

    fun setAdapter(adapter: RecyclerView.Adapter<*>) {
        recyclerView.adapter = adapter

        // Check if initial adapter count is >1, show pager if so
        showForwardPager(adapter.itemCount > 1)

        // Monitor adapter for changes in count:
        adapter.registerAdapterDataObserver(object: RecyclerView.AdapterDataObserver() {
            override fun onChanged() {
                if (adapter.itemCount > 1) {
                    hidePagers()

                    // View is not always scrollable at this point,
                    //  so force-show forward pager instead of checking
                    //  to show relevant pagers.
                    showForwardPager(true)
                    adapter.unregisterAdapterDataObserver(this)
                }
            }
        })
    }

    fun showForwardPager(show: Boolean) {
        if (show) {
            forwardPager.setVisible()
        } else {
            forwardPager.setVisibilityGone()
        }
    }

    fun showBackwardPager(show: Boolean) {
        if (show) {
            backwardPager.setVisible()
        } else {
            backwardPager.setVisibilityGone()
        }
    }

    fun showRelevantPagers() {
        showBackwardPager(recyclerView.canScrollLeftHorizontally())
        showForwardPager(recyclerView.canScrollRightHorizontally())
    }

    fun hidePagers() {
        backwardPager.setVisibilityGone()
        forwardPager.setVisibilityGone()
    }

    fun setPagerClickListeners() {
        backwardPager.setOnClickListener {
            val firstVisiblePosition = layoutManager.findFirstVisibleItemPosition()
            val firstCompletelyVisiblePosition = layoutManager.findFirstCompletelyVisibleItemPosition()

            if (layoutManager.isFirstVisiblePositionCompletelyVisible()) {
                attemptScrollToPosition(firstCompletelyVisiblePosition - 1)
            } else {
                attemptScrollToPosition(firstVisiblePosition)
            }
        }

        forwardPager.setOnClickListener {
            val lastVisiblePosition = layoutManager.findLastVisibleItemPosition()
            val lastCompletelyVisiblePosition = layoutManager.findLastCompletelyVisibleItemPosition()

            if (layoutManager.isLastVisiblePositionCompletelyVisible()) {
                attemptScrollToPosition(lastCompletelyVisiblePosition + 1)
            } else {
                attemptScrollToPosition(lastVisiblePosition)
            }
        }
    }

    private fun attemptScrollToPosition(position: Int) {
        val itemCount = recyclerView.adapter?.itemCount ?: 0

        if (position in 0..itemCount) {
            recyclerView.smoothScrollToPosition(position)
        }
    }

}