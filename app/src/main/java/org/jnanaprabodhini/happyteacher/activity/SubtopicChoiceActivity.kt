package org.jnanaprabodhini.happyteacher.activity

import android.os.Bundle
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.Query
import kotlinx.android.synthetic.main.activity_subtopic_choice.*
import kotlinx.android.synthetic.main.stacked_subject_spinners.*
import org.jnanaprabodhini.happyteacher.R
import org.jnanaprabodhini.happyteacher.activity.base.HappyTeacherActivity
import org.jnanaprabodhini.happyteacher.adapter.firestore.TopicsRecyclerAdapter
import org.jnanaprabodhini.happyteacher.adapter.helper.FirebaseDataObserver
import org.jnanaprabodhini.happyteacher.model.CardListContentHeader
import org.jnanaprabodhini.happyteacher.model.Topic
import org.jnanaprabodhini.happyteacher.view.SubjectSpinnerManager

class SubtopicChoiceActivity : HappyTeacherActivity(), FirebaseDataObserver {
    // TODO: Data observer!

    private val subjectSpinnerManager = SubjectSpinnerManager(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_subtopic_choice)

        initializeSpinners()
    }

    private fun initializeSpinners() {
        subjectSpinnerManager.initializeSpinners(parentSubjectSpinner, childSubjectSpinner, progressBar,
                onSpinnerSelectionsComplete = {subjectKey -> updateListOfTopicsForSubject(subjectKey)})
    }

    /**
     *  Display list of topics for the selected subject.
     */
    private fun updateListOfTopicsForSubject(subjectKey: String) {
        val topicQuery = firestoreLocalized.collection(getString(R.string.topics))
                .whereEqualTo(getString(R.string.subject), subjectKey) // todo: ordering

        val topicAdapterOptions = FirestoreRecyclerOptions.Builder<Topic>()
                .setQuery(topicQuery, Topic::class.java).build()

        val topicAdapter = object: TopicsRecyclerAdapter(topicAdapterOptions, this, this) {
            override fun getSubtopicAdapterOptions(topicId: String): FirestoreRecyclerOptions<CardListContentHeader> {
                val query: Query = firestoreLocalized.collection(getString(R.string.lessons))
                        .whereEqualTo(getString(R.string.topic), topicId)
                        .whereEqualTo(getString(R.string.is_featured), true)

                return FirestoreRecyclerOptions.Builder<CardListContentHeader>().setQuery(query, CardListContentHeader::class.java).build()
            }
        }

        topicAdapter.startListening()
        topicsRecyclerView.adapter = topicAdapter
    }
}
