package org.jnanaprabodhini.happyteacherapp.adapter.contentlist

import android.support.v7.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import org.jnanaprabodhini.happyteacherapp.activity.base.HappyTeacherActivity
import org.jnanaprabodhini.happyteacherapp.adapter.helper.FirebaseDataObserver
import org.jnanaprabodhini.happyteacherapp.adapter.viewholder.ContentCardViewHolder
import org.jnanaprabodhini.happyteacherapp.model.ContentCard
import org.jnanaprabodhini.happyteacherapp.model.ResourceHeader
import java.io.File

class CommentableResourceRecyclerAdapter(options: FirestoreRecyclerOptions<ContentCard>, attachmentDestinationDirectory: File, activity: HappyTeacherActivity, dataObserver: FirebaseDataObserver):
        ResourceRecyclerAdapter(options, attachmentDestinationDirectory, activity, dataObserver) {

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int, model: ContentCard?) {
        if (holder is ContentCardViewHolder) {
            onBindContentCardViewHolder(holder, model)

            val cardRef = snapshots.getSnapshot(position).reference
            holder.setFeedbackEditableForCard(cardRef, model ?: ContentCard())
        }
    }

}