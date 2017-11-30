package org.jnanaprabodhini.happyteacherapp.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.widget.ProgressBar
import android.widget.Spinner
import android.widget.TextView
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.android.synthetic.main.activity_subtopic_choice.*
import kotlinx.android.synthetic.main.stacked_subject_spinners.*
import org.jnanaprabodhini.happyteacherapp.R
import org.jnanaprabodhini.happyteacherapp.activity.base.HappyTeacherActivity
import org.jnanaprabodhini.happyteacherapp.adapter.contribute.TopicSubtopicsWriteChoiceAdapter
import org.jnanaprabodhini.happyteacherapp.adapter.helper.FirebaseDataObserver
import org.jnanaprabodhini.happyteacherapp.extension.setVisibilityGone
import org.jnanaprabodhini.happyteacherapp.extension.setVisible
import org.jnanaprabodhini.happyteacherapp.model.Subject
import org.jnanaprabodhini.happyteacherapp.model.Topic
import org.jnanaprabodhini.happyteacherapp.util.FirestoreKeys
import org.jnanaprabodhini.happyteacherapp.util.ResourceType
import org.jnanaprabodhini.happyteacherapp.view.SubjectSpinnerRecyclerView
import org.jnanaprabodhini.happyteacherapp.view.manager.SubjectSpinnerManager
import org.jnanaprabodhini.happyteacherapp.view.manager.SubtopicWriteChoiceTopicListManager

class SubtopicWriteChoiceActivity : HappyTeacherActivity(), FirebaseDataObserver,
        SubjectSpinnerRecyclerView {

    override val parentSpinner: Spinner by lazy { parentSubjectSpinner }
    override val childSpinner: Spinner by lazy { childSubjectSpinner }
    override val statusText: TextView by lazy { statusTextView }
    override val progressBar: ProgressBar by lazy { subtopicChoiceProgressBar }

    companion object IntentExtraHelper {
        fun launch(from: Activity, resourceType: String) {
            val intent = Intent(from, SubtopicWriteChoiceActivity::class.java)

            intent.apply {
                putExtra(RESOURCE_TYPE, resourceType)
            }
            from.startActivity(intent)
        }

        fun launch(from: Activity, resourceType: String, topicId: String) {
            val intent = Intent(from, SubtopicWriteChoiceActivity::class.java)

            intent.apply {
                putExtra(RESOURCE_TYPE, resourceType)
                putExtra(TOPIC_ID, topicId)
            }
            from.startActivity(intent)
        }

        private const val RESOURCE_TYPE: String = "RESOURCE_TYPE"
        fun Intent.getResourceType(): String = getStringExtra(RESOURCE_TYPE)

        private const val TOPIC_ID = "TOPIC_ID"
        fun Intent.getTopicId(): String = getStringExtra(TOPIC_ID)
        fun Intent.hasTopicId() = hasExtra(TOPIC_ID)
        fun Intent.clearTopicId() = removeExtra(TOPIC_ID)
    }

    private val subjectSpinnerManager = SubjectSpinnerManager(view = this, activity = this)

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

        when(resourceType) {
            ResourceType.LESSON -> {
                supportActionBar?.title = getString(R.string.create_a_lesson_plan)
                instructionTextView.setText(R.string.choose_what_your_lesson_will_be_about)
            }
            ResourceType.CLASSROOM_RESOURCE -> {
                supportActionBar?.title = getString(R.string.create_a_classroom_resource)
                instructionTextView.setText(R.string.choose_what_your_classroom_resource_will_be_about)
            }
            else -> {
                supportActionBar?.title = getString(R.string.create_a_resource)
                instructionTextView.setVisibilityGone()
            }
        }

        if (intent.hasTopicId()) {
            initializeSpinnersWithTopicSelection(intent.getTopicId())
        } else {
            subjectSpinnerManager.restoreSpinnerState(savedInstanceState)
            initializeSpinners()
        }
    }

    private fun initializeSpinners() {
        val topicListManager = SubtopicWriteChoiceTopicListManager(topicsRecyclerView, resourceType,
                this, this)
        subjectSpinnerManager.initializeWithTopicsListManager(topicListManager)
    }

    /**
     * Query for the Topic with the given ID, and initialize spinners
     *  to be selected for that subject.
     */
    private fun initializeSpinnersWithTopicSelection(topicId: String) {
        progressBar.setVisible()
        firestoreLocalized.collection(FirestoreKeys.TOPICS).document(topicId).get()
                .addOnSuccessListener { topicSnapshot ->
                    val topic = topicSnapshot.toObject(Topic::class.java)
                    val subjectId = topic.subject
                    initializeSpinnersWithSubject(subjectId)
                }
                .addOnFailureListener {
                    initializeSpinners()
                }
    }

    /**
     * Query for the given subject to get its parent subject, and
     *  set these subjects to be selected.
     */
    private fun initializeSpinnersWithSubject(subjectId: String) {
        firestoreLocalized.collection(FirestoreKeys.SUBJECTS).document(subjectId).get()
                .addOnSuccessListener { subjectSnapshot ->
                    if (!subjectSnapshot.exists()) {
                        initializeSpinners()
                    }

                    val subject = subjectSnapshot.toObject(Subject::class.java)
                    val parentSubjectId = subject.parentSubject

                    if (parentSubjectId != null && parentSubjectId.isNotEmpty()) {
                        subjectSpinnerManager.setParentSubjectIdToSelect(parentSubjectId)
                        subjectSpinnerManager.setChildSubjectIdToSelect(subjectId)
                    } else {
                        subjectSpinnerManager.setParentSubjectIdToSelect(subjectId)
                    }
                    initializeSpinners()
                }
                .addOnFailureListener {
                    initializeSpinners()
                }
    }

    override fun onSaveInstanceState(savedInstanceState: Bundle) {
        subjectSpinnerManager.saveSpinnerState(savedInstanceState)
        super.onSaveInstanceState(savedInstanceState)
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

        val adapter = topicsRecyclerView.adapter
        if (intent.hasTopicId() && adapter is TopicSubtopicsWriteChoiceAdapter) {
            // Attempt to select the topic passed in as an extra:
            val topicId = intent.getTopicId()
            val topicIndex = adapter.indexOfTopicOrNull(topicId)
            topicIndex?.let { topicsRecyclerView.scrollToPosition(topicIndex) }
            intent.clearTopicId()
        }
    }

    override fun onError(e: FirebaseFirestoreException?) {
        progressBar.setVisibilityGone()
        topicsRecyclerView.setVisibilityGone()

        statusTextView.setVisible()
        statusTextView.setText(R.string.there_was_an_error_loading_topics_for_this_subject)
    }
}
