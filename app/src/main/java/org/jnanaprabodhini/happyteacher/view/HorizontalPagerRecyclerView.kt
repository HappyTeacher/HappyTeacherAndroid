package org.jnanaprabodhini.happyteacher.view

import android.content.Context
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.LinearSmoothScroller
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import kotlinx.android.synthetic.main.view_recycler_horizontal_pager.view.*
import org.jnanaprabodhini.happyteacher.R
import org.jnanaprabodhini.happyteacher.extension.*


/**
 * A horizontal RecyclerView wrapper that has forward and backward pager buttons
 *  for scrolling content.
 */
class HorizontalPagerRecyclerView(context: Context, attrs: AttributeSet): FrameLayout(context, attrs) {

    private val onScrollListener = object: RecyclerView.OnScrollListener() {
        override fun onScrollStateChanged(recyclerView: RecyclerView?, newState: Int) {
            when (newState) {
                RecyclerView.SCROLL_STATE_IDLE -> showRelevantPagers()
                RecyclerView.SCROLL_STATE_DRAGGING -> hidePagers()
            }
        }
    }

    private val snapToStartScroller = object : LinearSmoothScroller(context) {
        override fun getHorizontalSnapPreference(): Int {
            return LinearSmoothScroller.SNAP_TO_START
        }
    }

    private val snapToEndScroller = object : LinearSmoothScroller(context) {
        override fun getHorizontalSnapPreference(): Int {
            return LinearSmoothScroller.SNAP_TO_END
        }
    }

    val layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

    init {
        View.inflate(context, R.layout.view_recycler_horizontal_pager, this)
    }

    override fun onFinishInflate() {
        super.onFinishInflate()

        setupRecycler()

        backwardPager.setElevation(R.dimen.pager_button_elevation)
        forwardPager.setElevation(R.dimen.pager_button_elevation)

        setPagerClickListeners()
    }

    private fun setupRecycler() {
        recyclerView.layoutManager = layoutManager
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

    private fun setPagerClickListeners() {
        backwardPager.setOnClickListener {
            val firstVisiblePosition = layoutManager.findFirstVisibleItemPosition()
            val firstCompletelyVisiblePosition = layoutManager.findFirstCompletelyVisibleItemPosition()
            val lastVisiblePosition = layoutManager.findLastVisibleItemPosition()

            when (firstVisiblePosition) {
                // Only one view is visible (and perhaps not completely visible). Scroll to next:
                lastVisiblePosition -> attemptScrollToPosition(firstVisiblePosition - 1)

                // First visible pos is completely visible. Scroll to next:
                firstCompletelyVisiblePosition -> attemptScrollToPosition(firstCompletelyVisiblePosition - 1)

                // First visible pos isn't completely visible. Scroll to it:
                else -> attemptScrollToPosition(firstVisiblePosition)
            }
        }

        forwardPager.setOnClickListener {
            val lastVisiblePosition = layoutManager.findLastVisibleItemPosition()
            val lastCompletelyVisiblePosition = layoutManager.findLastCompletelyVisibleItemPosition()
            val firstVisiblePosition = layoutManager.findFirstVisibleItemPosition()

            when (lastVisiblePosition) {
                firstVisiblePosition -> attemptScrollToPosition(lastVisiblePosition + 1)
                lastCompletelyVisiblePosition -> attemptScrollToPosition(lastCompletelyVisiblePosition + 1)
                else -> attemptScrollToPosition(lastVisiblePosition)
            }
        }
    }

    private fun attemptScrollToPosition(position: Int) {
        val itemCount = recyclerView.adapter?.itemCount ?: 0

        when {
            position in 0..itemCount -> {
                snapToStartScroller.targetPosition = position
                layoutManager.startSmoothScroll(snapToStartScroller)
            }
            position >= itemCount -> scrollToEnd()
            else -> scrollToStart()
        }
    }

    private fun scrollToEnd() {
        val itemCount = recyclerView.adapter?.itemCount ?: 0
        snapToEndScroller.targetPosition = if (itemCount == 0) itemCount else itemCount - 1
        layoutManager.startSmoothScroll(snapToStartScroller)
    }

    private fun scrollToStart() {
        snapToStartScroller.targetPosition = 0
        layoutManager.startSmoothScroll(snapToStartScroller)
    }

}