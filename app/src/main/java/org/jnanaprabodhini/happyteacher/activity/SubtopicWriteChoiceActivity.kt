package org.jnanaprabodhini.happyteacher.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.android.synthetic.main.activity_subtopic_choice.*
import kotlinx.android.synthetic.main.stacked_subject_spinners.*
import org.jnanaprabodhini.happyteacher.R
import org.jnanaprabodhini.happyteacher.activity.base.HappyTeacherActivity
import org.jnanaprabodhini.happyteacher.adapter.contribute.TopicSubtopicsWriteChoiceAdapter
import org.jnanaprabodhini.happyteacher.adapter.helper.FirebaseDataObserver
import org.jnanaprabodhini.happyteacher.extension.setVisibilityGone
import org.jnanaprabodhini.happyteacher.extension.setVisible
import org.jnanaprabodhini.happyteacher.model.Topic
import org.jnanaprabodhini.happyteacher.util.ResourceType
import org.jnanaprabodhini.happyteacher.view.SubjectSpinnerManager

class SubtopicWriteChoiceActivity : HappyTeacherActivity(), FirebaseDataObserver {

    companion object IntentExtraHelper {
        fun launch(from: Activity, resourceType: String) {
            val intent = Intent(from, SubtopicWriteChoiceActivity::class.java)

            intent.apply {
                putExtra(RESOURCE_TYPE, resourceType)
            }
            from.startActivity(intent)
        }

        private const val RESOURCE_TYPE: String = "RESOURCE_TYPE"
        fun Intent.getResourceType(): String = getStringExtra(RESOURCE_TYPE)
    }

    // TODO: persist spinner state across config changes
    private val subjectSpinnerManager = SubjectSpinnerManager(this)

    private val resourceType by lazy {
        intent.getResourceType()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_subtopic_choice)
        topicsRecyclerView.layoutManager = LinearLayoutManager(this)

        supportActionBar?.title = when(resourceType) {
            ResourceType.LESSON -> getString(R.string.create_a_lesson_plan)
            ResourceType.CLASSROOM_RESOURCE -> getString(R.string.create_a_classroom_resource)
            else -> getString(R.string.create_a_resource)
        }

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

        val topicAdapter = TopicSubtopicsWriteChoiceAdapter(topicAdapterOptions, resourceType,this, this)

        topicAdapter.startListening()
        topicsRecyclerView.adapter = topicAdapter
    }

    override fun onRequestNewData() {
        progressBar.setVisible()
        topicsRecyclerView.setVisibilityGone()
        statusTextView.setVisibilityGone()
    }

    override fun onDataLoaded() {
        progressBar.setVisibilityGone()
    }

    override fun onDataEmpty() {
        topicsRecyclerView.setVisibilityGone()
        statusTextView.setVisible()
        statusTextView.setText(R.string.there_are_no_topics_for_this_subject_yet)
    }

    override fun onDataNonEmpty() {
        statusTextView.setVisibilityGone()
        topicsRecyclerView.setVisible()
    }

    override fun onError(e: FirebaseFirestoreException?) {
        progressBar.setVisibilityGone()
        topicsRecyclerView.setVisibilityGone()

        statusTextView.setVisible()
        statusTextView.setText(R.string.there_was_an_error_loading_topics_for_this_subject)
    }
}
