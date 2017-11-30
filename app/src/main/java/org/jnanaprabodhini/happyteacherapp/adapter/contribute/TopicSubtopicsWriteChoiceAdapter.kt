package org.jnanaprabodhini.happyteacherapp.adapter.contribute

import android.view.LayoutInflater
import android.view.ViewGroup
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Query
import org.jnanaprabodhini.happyteacherapp.R
import org.jnanaprabodhini.happyteacherapp.activity.base.HappyTeacherActivity
import org.jnanaprabodhini.happyteacherapp.adapter.firestore.TopicsRecyclerAdapter
import org.jnanaprabodhini.happyteacherapp.adapter.helper.FirebaseDataObserver
import org.jnanaprabodhini.happyteacherapp.adapter.viewholder.TopicSubtopicChoiceRecyclerViewHolder
import org.jnanaprabodhini.happyteacherapp.extension.setVisibilityGone
import org.jnanaprabodhini.happyteacherapp.extension.setVisible
import org.jnanaprabodhini.happyteacherapp.model.Subtopic
import org.jnanaprabodhini.happyteacherapp.model.Topic
import org.jnanaprabodhini.happyteacherapp.view.HorizontalPagerRecyclerView

/**
 * Created by grahamearley on 11/2/17.
 */
class TopicSubtopicsWriteChoiceAdapter(topicsAdapterOptions: FirestoreRecyclerOptions<Topic>,
                                       private val resourceType: String,
                                       topicsDataObserver: FirebaseDataObserver,
                                       activity: HappyTeacherActivity): TopicsRecyclerAdapter<TopicSubtopicChoiceRecyclerViewHolder>(topicsAdapterOptions, topicsDataObserver, activity) {

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): TopicSubtopicChoiceRecyclerViewHolder {
        val view = LayoutInflater.from(parent?.context).inflate(R.layout.list_item_subtopic_choice_header_recycler, parent, false)
        return TopicSubtopicChoiceRecyclerViewHolder(view)
    }

    override fun onBindViewHolder(holder: TopicSubtopicChoiceRecyclerViewHolder?, position: Int, model: Topic?) {
        holder?.titleTextView?.text = model?.name
        setBackgroundColor(holder?.itemView, position)

        val topicId = snapshots.getSnapshot(position).reference.id
        initializeSubtopicRecycler(holder?.horizontalRecyclerView, topicId, holder)
    }

    private fun initializeSubtopicRecycler(horizontalRecyclerView: HorizontalPagerRecyclerView?, topicId: String, holder: TopicSubtopicChoiceRecyclerViewHolder?) {
        val query: Query = activity.firestoreLocalized.collection(activity.getString(R.string.subtopics))
                .whereEqualTo(activity.getString(R.string.topic), topicId)

        val adapterOptions = FirestoreRecyclerOptions.Builder<Subtopic>().setQuery(query, Subtopic::class.java).build()
        val adapter = SubtopicWriteChoiceRecyclerAdapter(adapterOptions, resourceType, getSubtopicDataObserverForViewHolder(holder), activity)

        adapter.startListening()
        horizontalRecyclerView?.setAdapter(adapter)
    }

    private fun getSubtopicDataObserverForViewHolder(viewHolder: TopicSubtopicChoiceRecyclerViewHolder?) = object: FirebaseDataObserver {
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