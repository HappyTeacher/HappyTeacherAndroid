package org.jnanaprabodhini.happyteacher.adapter.contribute

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import org.jnanaprabodhini.happyteacher.R
import org.jnanaprabodhini.happyteacher.activity.LessonEditorActivity
import org.jnanaprabodhini.happyteacher.activity.base.HappyTeacherActivity
import org.jnanaprabodhini.happyteacher.adapter.firestore.FirestoreObserverRecyclerAdapter
import org.jnanaprabodhini.happyteacher.adapter.helper.FirebaseDataObserver
import org.jnanaprabodhini.happyteacher.adapter.viewholder.SubtopicViewHolder
import org.jnanaprabodhini.happyteacher.model.ResourceHeader
import org.jnanaprabodhini.happyteacher.model.Subtopic
import org.jnanaprabodhini.happyteacher.util.ResourceStatus


class SubtopicWriteChoiceRecyclerAdapter(options: FirestoreRecyclerOptions<Subtopic>, firebaseDataObserver: FirebaseDataObserver, val activity: HappyTeacherActivity):
        FirestoreObserverRecyclerAdapter<Subtopic, SubtopicViewHolder>(options, firebaseDataObserver) {

    private val userId by lazy {
        activity.auth.currentUser!!.uid // User must be logged in to reach this point.
    }

    override fun onBindViewHolder(holder: SubtopicViewHolder, position: Int, model: Subtopic?) {
        holder.populateView(model?.name.orEmpty())

        holder.writeButton.setOnClickListener {
            val subtopicId = snapshots.getSnapshot(position).reference.id
            val lessonHeader = getLessonHeader(model ?: Subtopic(), subtopicId)

            // Create a new draft lesson and launch editor
            val draftRef = activity.firestoreLocalized.collection(activity.getString(R.string.resources))
                    .document()

            draftRef.set(lessonHeader)
            LessonEditorActivity.launch(activity, draftRef, lessonHeader)
        }
    }

    private fun getLessonHeader(subtopic: Subtopic, subtopicId: String): ResourceHeader {
        val authorName = activity.prefs.getUserName()
        val authorInstitution = activity.prefs.getUserInstitution()
        val authorLocation = activity.prefs.getUserLocation()

        return ResourceHeader(
                name = subtopic.name,
                authorId = userId,
                subtopic = subtopicId,
                topic = subtopic.topic,
                topicName = subtopic.topicName,
                subjectName = subtopic.subjectName,
                authorInstitution = authorInstitution,
                authorLocation = authorLocation,
                authorName = authorName,
                status = ResourceStatus.DRAFT,
                resourceType = activity.getString(R.string.lesson) // add classroom resources too!
        )
    }

    private fun inflateView(parent: ViewGroup?): View {
        return LayoutInflater.from(parent?.context)
                .inflate(R.layout.list_item_subtopic_header_card, parent, false)
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): SubtopicViewHolder {
        return SubtopicViewHolder(inflateView(parent))
    }

}