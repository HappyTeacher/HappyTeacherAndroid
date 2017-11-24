package org.jnanaprabodhini.happyteacherapp.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.android.synthetic.main.activity_subtopic_choice.*
import kotlinx.android.synthetic.main.stacked_subject_spinners.*
import org.jnanaprabodhini.happyteacherapp.R
import org.jnanaprabodhini.happyteacherapp.activity.base.HappyTeacherActivity
import org.jnanaprabodhini.happyteacherapp.adapter.helper.FirebaseDataObserver
import org.jnanaprabodhini.happyteacherapp.extension.setVisibilityGone
import org.jnanaprabodhini.happyteacherapp.extension.setVisible
import org.jnanaprabodhini.happyteacherapp.util.ResourceType
import org.jnanaprabodhini.happyteacherapp.view.manager.SubjectSpinnerManager
import org.jnanaprabodhini.happyteacherapp.view.manager.SubtopicWriteChoiceTopicListManager

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

        initializeSpinners()
    }

    private fun initializeSpinners() {
        val topicListManager = SubtopicWriteChoiceTopicListManager(topicsRecyclerView, resourceType,
                this, this)
        subjectSpinnerManager.initializeWithTopicsListManager(parentSubjectSpinner,
                childSubjectSpinner, progressBar, topicListManager)
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
