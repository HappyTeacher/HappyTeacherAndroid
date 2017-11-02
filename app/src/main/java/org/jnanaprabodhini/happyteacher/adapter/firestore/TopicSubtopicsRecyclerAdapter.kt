package org.jnanaprabodhini.happyteacher.adapter.firestore

import android.app.Activity
import android.provider.Settings.Global.getString
import android.view.LayoutInflater
import android.view.ViewGroup
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Query
import org.jnanaprabodhini.happyteacher.R
import org.jnanaprabodhini.happyteacher.adapter.helper.FirebaseDataObserver
import org.jnanaprabodhini.happyteacher.adapter.viewholder.TopicSubtopicChoiceRecyclerViewHolder
import org.jnanaprabodhini.happyteacher.extension.setVisibilityGone
import org.jnanaprabodhini.happyteacher.extension.setVisible
import org.jnanaprabodhini.happyteacher.model.Subtopic
import org.jnanaprabodhini.happyteacher.model.Topic
import org.jnanaprabodhini.happyteacher.util.PreferencesManager
import org.jnanaprabodhini.happyteacher.view.HorizontalPagerRecyclerView

/**
 * Created by grahamearley on 11/2/17.
 */
class TopicSubtopicsRecyclerAdapter(topicsAdapterOptions: FirestoreRecyclerOptions<Topic>,
                                    topicsDataObserver: FirebaseDataObserver,
                                    activity: Activity): TopicsRecyclerAdapter<TopicSubtopicChoiceRecyclerViewHolder>(topicsAdapterOptions, topicsDataObserver, activity) {

    private val prefs: PreferencesManager by lazy {
        PreferencesManager.getInstance(activity)
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): TopicSubtopicChoiceRecyclerViewHolder {
        val view = LayoutInflater.from(parent?.context).inflate(R.layout.list_item_subtopic_choice_header_recycler, parent, false)
        return TopicSubtopicChoiceRecyclerViewHolder(view)
    }

    override fun onBindViewHolder(holder: TopicSubtopicChoiceRecyclerViewHolder?, position: Int, model: Topic?) {
        holder?.titleTextView?.text = model?.name
        setBackgroundColor(holder?.itemView, position)

        val topicRef = snapshots.getSnapshot(position).reference
        initializeSubtopicRecycler(holder?.horizontalRecyclerView, topicRef, holder)
    }

    private fun initializeSubtopicRecycler(horizontalRecyclerView: HorizontalPagerRecyclerView?, topicRef: DocumentReference, holder: TopicSubtopicChoiceRecyclerViewHolder?) {
        val query: Query = topicRef.collection(activity.getString(R.string.subtopics))

        val adapterOptions = FirestoreRecyclerOptions.Builder<Subtopic>().setQuery(query, Subtopic::class.java).build()
        val adapter = SubtopicHeaderRecyclerAdapter(adapterOptions, getSubtopicDataObserverForViewHolder(holder))

        adapter.startListening()
        horizontalRecyclerView?.setAdapter(adapter)
    }

    override fun getSubtopicDataObserverForViewHolder(viewHolder: TopicSubtopicChoiceRecyclerViewHolder?, level: Int?) = object: FirebaseDataObserver {
        override fun onRequestNewData() {
            viewHolder?.horizontalRecyclerView?.setVisibilityGone()
            viewHolder?.progressBar?.setVisible()
            viewHolder?.statusTextView?.setVisibilityGone()
        }

        override fun onDataLoaded() {
            viewHolder?.progressBar?.setVisibilityGone()
        }

        override fun onDataEmpty() {
            viewHolder?.statusTextView?.setVisible()
            viewHolder?.statusTextView?.setText(R.string.there_are_no_lessons_for_this_topic_yet)
        }

        override fun onDataNonEmpty() {
            viewHolder?.horizontalRecyclerView?.setVisible()
            viewHolder?.statusTextView?.setVisibilityGone()
        }

        override fun onError(e: FirebaseFirestoreException?) {
            viewHolder?.horizontalRecyclerView?.setVisibilityGone()
            viewHolder?.statusTextView?.setVisible()
            viewHolder?.progressBar?.setVisibilityGone()
            viewHolder?.statusTextView?.setText(R.string.there_was_an_error_loading_lessons_for_this_topic)
        }
    }
}