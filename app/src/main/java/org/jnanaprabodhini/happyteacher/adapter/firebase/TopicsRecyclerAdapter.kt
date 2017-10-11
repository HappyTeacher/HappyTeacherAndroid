package org.jnanaprabodhini.happyteacher.adapter.firebase

import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import com.firebase.ui.database.FirebaseRecyclerOptions
import org.jnanaprabodhini.happyteacher.R
import org.jnanaprabodhini.happyteacher.adapter.LessonHeaderRecyclerAdapter
import org.jnanaprabodhini.happyteacher.adapter.helper.FirebaseDataObserver
import org.jnanaprabodhini.happyteacher.adapter.viewholder.ContentHeaderRecyclerViewHolder
import org.jnanaprabodhini.happyteacher.extension.setVisibilityGone
import org.jnanaprabodhini.happyteacher.extension.setVisible
import org.jnanaprabodhini.happyteacher.model.CardListContentHeader
import org.jnanaprabodhini.happyteacher.model.Topic
import org.jnanaprabodhini.happyteacher.view.HorizontalPagerRecyclerView

/**
 * Created by grahamearley on 10/11/17.
 */
abstract class TopicsRecyclerAdapter(topicsAdapterOptions: FirebaseRecyclerOptions<Topic>,
                                     topicsDataObserver: FirebaseDataObserver,
                                     val activity: Activity):
        FirebaseObserverRecyclerAdapter<Topic, ContentHeaderRecyclerViewHolder>(topicsAdapterOptions, topicsDataObserver) {

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ContentHeaderRecyclerViewHolder {
        val view = LayoutInflater.from(parent?.context).inflate(R.layout.list_item_content_header_recycler, parent, false)
        return ContentHeaderRecyclerViewHolder(view)
    }

    override fun onBindViewHolder(holder: ContentHeaderRecyclerViewHolder?, position: Int, model: Topic?) {
        holder?.titleTextView?.text = model?.name

        // Alternate between these four colors:
        when (position % 4) {
            0 -> holder?.itemView?.setBackgroundResource(R.color.seaTeal)
            1 -> holder?.itemView?.setBackgroundResource(R.color.grassGreen)
            2 -> holder?.itemView?.setBackgroundResource(R.color.bubbleGumPink)
            3 -> holder?.itemView?.setBackgroundResource(R.color.dreamsicleOrange)
        }

        initializeChildRecyclerView(holder?.horizontalRecyclerView, getRef(position).key, model, holder)
    }

    private fun initializeChildRecyclerView(recyclerView: HorizontalPagerRecyclerView?, topicKey: String, model: Topic?, holder: ContentHeaderRecyclerViewHolder?) {
        val adapterOptions = getSubtopicAdapterOptions(topicKey)
        val adapter = LessonHeaderRecyclerAdapter(model?.name ?: "", adapterOptions, activity, getSubtopicDataObserverForViewHolder(holder))

        adapter.startListening()
        recyclerView?.setAdapter(adapter)
    }

    abstract fun getSubtopicAdapterOptions(topicId: String): FirebaseRecyclerOptions<CardListContentHeader>

    private fun getSubtopicDataObserverForViewHolder(topicViewHolder: ContentHeaderRecyclerViewHolder?, level: Int? = null) = object: FirebaseDataObserver {
        override fun onRequestNewData() {
            topicViewHolder?.progressBar?.setVisible()
        }

        override fun onDataLoaded() {
            topicViewHolder?.progressBar?.setVisibilityGone()
        }

        override fun onDataEmpty() {
            topicViewHolder?.horizontalRecyclerView?.setVisibilityGone()
            topicViewHolder?.emptyView?.setVisible()
            topicViewHolder?.emptyTextView?.setText(R.string.there_are_no_lessons_for_this_topic_yet)

            level?.let { topicViewHolder?.emptyTextView?.text = activity.getString(R.string.no_lessons_at_level_yet, level) }
        }

        override fun onDataNonEmpty() {
            topicViewHolder?.horizontalRecyclerView?.setVisible()
            topicViewHolder?.emptyView?.setVisibilityGone()
        }
    }
}