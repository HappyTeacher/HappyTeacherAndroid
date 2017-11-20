package org.jnanaprabodhini.happyteacherapp.adapter.contentlist

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import org.jnanaprabodhini.happyteacherapp.R
import org.jnanaprabodhini.happyteacherapp.activity.base.HappyTeacherActivity
import org.jnanaprabodhini.happyteacherapp.adapter.helper.FirebaseDataObserver
import org.jnanaprabodhini.happyteacherapp.adapter.helper.MovableViewContainer
import org.jnanaprabodhini.happyteacherapp.adapter.viewholder.ContentCardViewHolder
import org.jnanaprabodhini.happyteacherapp.model.ContentCard
import java.io.File

class CommentableResourceRecyclerAdapter(options: FirestoreRecyclerOptions<ContentCard>, attachmentDestinationDirectory: File, subtopicId: String, activity: HappyTeacherActivity, dataObserver: FirebaseDataObserver):
        ResourceContentRecyclerAdapter(options, attachmentDestinationDirectory, subtopicId, activity, dataObserver) {

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int, model: ContentCard?) {
        if (holder is ContentCardViewHolder) {
            onBindContentCardViewHolder(holder, model)

            val cardRef = snapshots.getSnapshot(position).reference
            val parentContentId = cardRef.parent.parent.id
            holder.setupCommentButton(activity, cardRef, model ?: ContentCard(), parentContentId)
        }
    }

}