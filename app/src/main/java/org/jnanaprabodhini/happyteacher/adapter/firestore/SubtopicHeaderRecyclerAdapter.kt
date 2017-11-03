package org.jnanaprabodhini.happyteacher.adapter.firestore

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import org.jnanaprabodhini.happyteacher.R
import org.jnanaprabodhini.happyteacher.activity.LessonEditorActivity
import org.jnanaprabodhini.happyteacher.adapter.helper.FirebaseDataObserver
import org.jnanaprabodhini.happyteacher.adapter.viewholder.SubtopicViewHolder
import org.jnanaprabodhini.happyteacher.model.CardListContentHeader
import org.jnanaprabodhini.happyteacher.model.Subtopic
import org.jnanaprabodhini.happyteacher.util.PreferencesManager

class SubtopicHeaderRecyclerAdapter(options: FirestoreRecyclerOptions<Subtopic>, firebaseDataObserver: FirebaseDataObserver, val activity: Activity):
        FirestoreObserverRecyclerAdapter<Subtopic, SubtopicViewHolder>(options, firebaseDataObserver) {

    val prefs by lazy {
        PreferencesManager.getInstance(activity)
    }

    val auth: FirebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }

    override fun onBindViewHolder(holder: SubtopicViewHolder, position: Int, model: Subtopic?) {
        holder.populateView(model?.name.orEmpty())

        holder.writeButton.setOnClickListener {
            val cardRef = FirebaseFirestore.getInstance().collection("/localized/en/lessons/5YaxDA5OqiIG0LsTL7rP/cards")

            val subtopicId = snapshots.getSnapshot(position).reference.id
            val lessonHeader = getLessonHeader(model ?: Subtopic(), subtopicId)

            LessonEditorActivity.launch(activity, cardRef, lessonHeader, model?.topicName.orEmpty())
        }
    }

    private fun getLessonHeader(subtopic: Subtopic, subtopicId: String): CardListContentHeader {
        val authorName = prefs.getUserName()
        val authorInstitution = prefs.getUserInstitution()
        val authorLocation = prefs.getUserLocation()
        val authorId = auth.currentUser!!.uid // User must be logged in to reach this point.

        return CardListContentHeader(
                name = subtopic.name,
                authorId = authorId,
                subtopic = subtopicId,
                topic = subtopic.topic,
                subjectName = subtopic.subjectName,
                authorInstitution = authorInstitution,
                authorLocation = authorLocation,
                authorName = authorName
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