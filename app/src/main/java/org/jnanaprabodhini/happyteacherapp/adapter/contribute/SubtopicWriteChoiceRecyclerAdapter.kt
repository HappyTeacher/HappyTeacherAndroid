package org.jnanaprabodhini.happyteacherapp.adapter.contribute

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import org.jnanaprabodhini.happyteacherapp.R
import org.jnanaprabodhini.happyteacherapp.activity.ResourceEditorActivity
import org.jnanaprabodhini.happyteacherapp.activity.base.HappyTeacherActivity
import org.jnanaprabodhini.happyteacherapp.adapter.firestore.FirestoreObservableRecyclerAdapter
import org.jnanaprabodhini.happyteacherapp.adapter.helper.FirebaseDataObserver
import org.jnanaprabodhini.happyteacherapp.adapter.viewholder.SubtopicViewHolder
import org.jnanaprabodhini.happyteacherapp.model.ResourceHeader
import org.jnanaprabodhini.happyteacherapp.model.Subtopic
import org.jnanaprabodhini.happyteacherapp.util.ResourceStatus
import org.jnanaprabodhini.happyteacherapp.util.ResourceType


class SubtopicWriteChoiceRecyclerAdapter(options: FirestoreRecyclerOptions<Subtopic>, val resourceType: String, firebaseDataObserver: FirebaseDataObserver, val activity: HappyTeacherActivity):
        FirestoreObservableRecyclerAdapter<Subtopic, SubtopicViewHolder>(options, firebaseDataObserver) {

    private val userId by lazy {
        activity.auth.currentUser!!.uid // User must be logged in to reach this point.
    }

    override fun onBindViewHolder(holder: SubtopicViewHolder, position: Int, model: Subtopic?) {
        holder.populateView(model?.name.orEmpty(), resourceType)

        holder.writeButton.text = when(resourceType) {
            ResourceType.LESSON -> activity.getString(R.string.write_lesson)
            ResourceType.CLASSROOM_RESOURCE -> activity.getString(R.string.write_classroom_resource)
            else -> activity.getString(R.string.write)
        }

        holder.writeButton.setOnClickListener {
            val subtopicId = snapshots.getSnapshot(position).reference.id
            val lessonHeader = getLessonHeader(model ?: Subtopic(), subtopicId)

            // Create a new draft lesson and launch editor
            val draftRef = activity.firestoreLocalized.collection(activity.getString(R.string.resources))
                    .document()

            draftRef.set(lessonHeader)
            ResourceEditorActivity.launch(activity, draftRef, lessonHeader, isNewResource = true)
        }
    }

    private fun getLessonHeader(subtopic: Subtopic, subtopicId: String): ResourceHeader {
        val authorName = activity.prefs.getUserName()
        val authorInstitution = activity.prefs.getUserInstitution()
        val authorLocation = activity.prefs.getUserLocation()

        return ResourceHeader(
                name = if (resourceType == ResourceType.CLASSROOM_RESOURCE) "" else subtopic.name,
                authorId = userId,
                subtopic = subtopicId,
                topic = subtopic.topic,
                topicName = subtopic.topicName,
                subjectName = subtopic.subjectName,
                authorInstitution = authorInstitution,
                authorLocation = authorLocation,
                authorName = authorName,
                status = ResourceStatus.DRAFT,
                resourceType = resourceType
        )
    }

    private fun inflateView(parent: ViewGroup?): View {
        return LayoutInflater.from(parent?.context)
                .inflate(R.layout.list_item_subtopic_header_card, parent, false)
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int) = SubtopicViewHolder(inflateView(parent))
}