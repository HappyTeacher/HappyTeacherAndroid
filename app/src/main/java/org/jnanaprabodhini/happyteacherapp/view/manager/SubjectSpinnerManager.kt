package org.jnanaprabodhini.happyteacherapp.view.manager

import android.os.Bundle
import android.support.annotation.LayoutRes
import android.view.View
import android.widget.Spinner
import android.widget.TextView
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Query
import org.jnanaprabodhini.happyteacherapp.R
import org.jnanaprabodhini.happyteacherapp.activity.base.HappyTeacherActivity
import org.jnanaprabodhini.happyteacherapp.adapter.firestore.FirestoreObservableListAdapter
import org.jnanaprabodhini.happyteacherapp.adapter.helper.FirebaseDataObserver
import org.jnanaprabodhini.happyteacherapp.extension.onItemSelected
import org.jnanaprabodhini.happyteacherapp.extension.setVisibilityGone
import org.jnanaprabodhini.happyteacherapp.extension.setVisible
import org.jnanaprabodhini.happyteacherapp.model.Subject
import org.jnanaprabodhini.happyteacherapp.view.SubjectSpinnerRecyclerView

/**
 * A class that manages the view logic of a pair of spinners (containing child/parents subjects).
 *
 *  This is used for displaying a list of Topics controlled by two subject spinners.
 */
class SubjectSpinnerManager(val view: SubjectSpinnerRecyclerView, val activity: HappyTeacherActivity) {

    companion object SavedInstanceStateConstants {
        const val PARENT_SUBJECT_SPINNER_SELECTION = "PARENT_SUBJECT_SPINNER_SELECTION"
        const val CHILD_SUBJECT_SPINNER_SELECTION = "CHILD_SUBJECT_SPINNER_SELECTION"
    }

    private var parentSpinnerSelectionIndex = 0
    private var childSpinnerSelectionIndex = 0

    private var onSpinnerSelectionsComplete: (subjectKey: String) -> Unit = {}
    private var parentSubjectIdToSelect: String? = null
    private var childSubjectIdToSelect: String? = null

    fun initializeWithTopicsListManager(topicsListManager: TopicListManager) {
        this.onSpinnerSelectionsComplete = topicsListManager::updateListOfTopicsForSubject

        setupSpinners()
    }

    fun setParentSubjectIdToSelect(subjectId: String) {
        this.parentSubjectIdToSelect = subjectId
    }

    fun setChildSubjectIdToSelect(subjectId: String) {
        this.childSubjectIdToSelect = subjectId
    }

    private fun setupSpinners() {
        setupParentSpinner()
    }

    private fun setupParentSpinner() {
        val parentAdapter = getParentSubjectAdapter()
        parentAdapter.startListening()
        view.parentSpinner.adapter = parentAdapter

        view.parentSpinner.onItemSelected { position ->

            if (position != parentSpinnerSelectionIndex) {
                // If a new item is selected (different from saved instance state value),
                //  then reset child spinner stored selection.
                childSpinnerSelectionIndex = 0
            }

            parentSpinnerSelectionIndex = position

            val subject = parentAdapter.getItem(position)
            val selectedSubjectKey = parentAdapter.getItemKey(position)

            if (subject.hasChildren) {
                setupChildSpinner(selectedSubjectKey)
            } else {
                onSpinnerSelectionsComplete(selectedSubjectKey)
                view.childSpinner.setVisibilityGone()
            }
        }
    }

    private fun setupChildSpinner(parentSubjectKey: String) {
        val childAdapter = getChildSubjectAdapter(parentSubjectKey)
        childAdapter.startListening()
        view.childSpinner.adapter = childAdapter

        view.childSpinner.onItemSelected { position ->
            childSpinnerSelectionIndex = position

            val selectedSubjectKey = childAdapter.getItemKey(position)
            onSpinnerSelectionsComplete(selectedSubjectKey)
        }
    }

    private fun getDataObserverForSpinner(spinner: Spinner, selectionIndex: Int, subjectIdToSelect: String?): FirebaseDataObserver {
        return object: FirebaseDataObserver {
            override fun onRequestNewData() {
                view.progressBar.setVisibilityGone()
                view.statusText.setVisibilityGone()
                spinner.setVisibilityGone()
            }

            override fun onDataNonEmpty() {
                view.progressBar.setVisibilityGone()
                view.statusText.setVisibilityGone()
                spinner.setVisible()

                subjectIdToSelect?.let { id ->
                    val adapter = spinner.adapter as FirestoreObservableListAdapter<*>
                    val indexOfChildSubject = (0 until spinner.count).map { adapter.getItemKey(it) }
                            .indexOf(id)

                    if (indexOfChildSubject in 0 until spinner.count) {
                        spinner.setSelection(indexOfChildSubject)
                    }
                } ?: if (spinner.count > selectionIndex) {
                    spinner.setSelection(selectionIndex)
                }
            }

            override fun onDataEmpty() {
                view.progressBar.setVisibilityGone()
                spinner.setVisibilityGone()

                view.statusText.setVisible()
                view.statusText.setText(R.string.there_are_no_subjects_yet)
            }

            override fun onError(e: FirebaseFirestoreException?) {
                view.progressBar.setVisibilityGone()
                spinner.setVisibilityGone()

                view.statusText.setVisible()
                view.statusText.setText(R.string.there_was_an_error_loading_subjects)
            }
        }
    }

    private fun getParentSubjectAdapter(): FirestoreObservableListAdapter<Subject> {
        val subjectQuery = activity.firestoreLocalized.collection(activity.getString(R.string.subjects))
                .whereEqualTo(activity.getString(R.string.parent_subject), null)
        val dataObserver = getDataObserverForSpinner(view.parentSpinner, parentSpinnerSelectionIndex, parentSubjectIdToSelect)

        return getSpinnerAdapterForQuery(subjectQuery, dataObserver, R.layout.spinner_item)
    }

    private fun getChildSubjectAdapter(parentSubject: String): FirestoreObservableListAdapter<Subject> {
        val subjectQuery = activity.firestoreLocalized.collection(activity.getString(R.string.subjects))
                .whereEqualTo(activity.getString(R.string.parent_subject), parentSubject)
        val dataObserver = getDataObserverForSpinner(view.childSpinner, childSpinnerSelectionIndex, childSubjectIdToSelect)

        return getSpinnerAdapterForQuery(subjectQuery, dataObserver, R.layout.spinner_item_child)
    }

    private fun getSpinnerAdapterForQuery(subjectQuery: Query, dataObserver: FirebaseDataObserver, @LayoutRes spinnerLayout: Int): FirestoreObservableListAdapter<Subject> {
        return object: FirestoreObservableListAdapter<Subject>(subjectQuery, Subject::class.java, spinnerLayout, dataObserver, activity) {
            override fun populateView(view: View, model: Subject, position: Int) {
                (view as TextView).text = model.name
            }
        }
    }

    fun restoreSpinnerState(savedInstanceState: Bundle?) {
        savedInstanceState?.let {
            val parentSubjectStoredSelection = savedInstanceState.getInt(PARENT_SUBJECT_SPINNER_SELECTION, 0)
            val childSubjectStoredSelection = savedInstanceState.getInt(CHILD_SUBJECT_SPINNER_SELECTION, 0)

            parentSpinnerSelectionIndex = parentSubjectStoredSelection
            childSpinnerSelectionIndex = childSubjectStoredSelection
        }
    }

    fun saveSpinnerState(savedInstanceState: Bundle) {
        val parentSubjectSpinnerSelectionIndex = view.parentSpinner.selectedItemPosition
        val childSubjectSpinnerSelectionIndex = view.childSpinner.selectedItemPosition

        savedInstanceState.putInt(PARENT_SUBJECT_SPINNER_SELECTION, parentSubjectSpinnerSelectionIndex)
        savedInstanceState.putInt(CHILD_SUBJECT_SPINNER_SELECTION, childSubjectSpinnerSelectionIndex)
    }
}