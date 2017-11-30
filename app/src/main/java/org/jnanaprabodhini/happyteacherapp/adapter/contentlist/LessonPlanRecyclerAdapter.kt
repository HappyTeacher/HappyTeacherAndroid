package org.jnanaprabodhini.happyteacherapp.adapter.contentlist

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.FirebaseFirestoreException
import org.jnanaprabodhini.happyteacherapp.R
import org.jnanaprabodhini.happyteacherapp.activity.base.HappyTeacherActivity
import org.jnanaprabodhini.happyteacherapp.adapter.firestore.ResourceHeaderRecyclerAdapter
import org.jnanaprabodhini.happyteacherapp.adapter.helper.FirebaseDataObserver
import org.jnanaprabodhini.happyteacherapp.adapter.viewholder.ContentCardViewHolder
import org.jnanaprabodhini.happyteacherapp.adapter.viewholder.ResourceHeaderRecyclerViewHolder
import org.jnanaprabodhini.happyteacherapp.extension.setInvisible
import org.jnanaprabodhini.happyteacherapp.extension.setVisibilityGone
import org.jnanaprabodhini.happyteacherapp.extension.setVisible
import org.jnanaprabodhini.happyteacherapp.model.ResourceHeader
import org.jnanaprabodhini.happyteacherapp.model.ContentCard
import org.jnanaprabodhini.happyteacherapp.util.ResourceStatus
import org.jnanaprabodhini.happyteacherapp.util.ResourceType
import java.io.File

class LessonPlanRecyclerAdapter(options: FirestoreRecyclerOptions<ContentCard>,
                                attachmentDestinationDirectory: File,
                                private val resourceHeader: ResourceHeader,
                                activity: HappyTeacherActivity,
                                dataObserver: FirebaseDataObserver):
        ResourceRecyclerAdapter(options, attachmentDestinationDirectory, activity, dataObserver) {

    companion object { val LESSON_CARD_VIEW_TYPE = 0; val CLASSROOM_RESOURCES_FOOTER_VIEW_TYPE = 1 }

    override fun getItemCount(): Int {
        val cardCount = super.getItemCount()
        return if (cardCount > 0) {
            // Show a footer view if there are cards
            cardCount + 1
        } else {
            cardCount
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder? {
        return if (viewType == CLASSROOM_RESOURCES_FOOTER_VIEW_TYPE) {
            val classroomResourcesView = LayoutInflater.from(parent?.context).inflate(R.layout.list_item_content_header_recycler, parent, false)

            // Add margin to top of view:
            val margins = classroomResourcesView.layoutParams as ViewGroup.MarginLayoutParams
            margins.topMargin = activity.resources.getDimensionPixelSize(R.dimen.classroom_resources_list_top_padding)
            classroomResourcesView.layoutParams = margins

            ResourceHeaderRecyclerViewHolder(classroomResourcesView)
        } else {
            val cardView = LayoutInflater.from(parent?.context).inflate(R.layout.list_item_content_card, parent, false)
            ContentCardViewHolder(cardView)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == itemCount - 1) {
            CLASSROOM_RESOURCES_FOOTER_VIEW_TYPE
        } else {
            LESSON_CARD_VIEW_TYPE
        }
    }

    /**
     * We override this method (in addition to the abstract onBindViewHolder(holder, position, model) method)
     *  because this base method calls `getItem(position`, and this throws an error for the last item in the
     *  list because it is not an item in the list but rather the footer view.
     */
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {
        when (getItemViewType(position)) {
            LESSON_CARD_VIEW_TYPE -> onBindViewHolder(holder, position, getItem(position))
            CLASSROOM_RESOURCES_FOOTER_VIEW_TYPE -> onBindClassroomResourcesViewHolder(holder as ResourceHeaderRecyclerViewHolder)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int, model: ContentCard?) {
        if (holder is ContentCardViewHolder) {
            onBindContentCardViewHolder(holder, model)
        }
    }

    private fun onBindClassroomResourcesViewHolder(holder: ResourceHeaderRecyclerViewHolder) {
        holder.itemView.setBackgroundResource(R.color.colorPrimaryDark)
        holder.titleTextView.setText(R.string.classroom_resources)

        val classroomResourceQuery = activity.firestoreLocalized.collection(activity.getString(R.string.resources))
                .whereEqualTo(activity.getString(R.string.resource_type), activity.getString(R.string.classroom_resource_key))
                .whereEqualTo(activity.getString(R.string.status), ResourceStatus.PUBLISHED)
                .whereEqualTo(activity.getString(R.string.subtopic), resourceHeader.subtopic)
                .orderBy(activity.getString(R.string.name_key))

        val adapterOptions = FirestoreRecyclerOptions.Builder<ResourceHeader>()
                .setQuery(classroomResourceQuery, ResourceHeader::class.java)
                .build()

        val adapter = ResourceHeaderRecyclerAdapter(adapterOptions, showSubmissionCount = false,
                activity = activity, firebaseDataObserver = getClassroomResourcesDataObserver(holder))
        adapter.startListening()

        holder.horizontalRecyclerView.setAdapter(adapter)
    }

    private fun getClassroomResourcesDataObserver(holder: ResourceHeaderRecyclerViewHolder): FirebaseDataObserver = object: FirebaseDataObserver {
        override fun onRequestNewData() {
            holder.progressBar.setVisible()
            holder.hideEmptyViews()
            holder.horizontalRecyclerView.setVisibilityGone()
        }

        override fun onDataLoaded() {
            holder.progressBar.setVisibilityGone()
        }

        override fun onDataNonEmpty() {
            holder.hideEmptyViews()
            holder.horizontalRecyclerView.setVisible()
        }

        override fun onDataEmpty() {
            holder.horizontalRecyclerView.setVisibilityGone()

            holder.showEmptyViewWithContributeButton(ResourceType.CLASSROOM_RESOURCE, resourceHeader.topic, activity)
            holder.statusTextView.setText(R.string.there_are_no_classroom_resources_for_this_lesson_yet)
        }

        override fun onError(e: FirebaseFirestoreException?) {
            holder.horizontalRecyclerView.setVisibilityGone()
            holder.contributeButton.setInvisible()
            holder.progressBar.setVisibilityGone()

            holder.statusTextView.setVisible()
            holder.statusTextView.setText(R.string.there_was_an_error_loading_classroom_resources_for_this_lesson)
        }
    }

}

