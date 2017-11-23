package org.jnanaprabodhini.happyteacherapp.activity

import android.os.Bundle
import android.support.annotation.IntegerRes
import android.support.v4.content.res.ResourcesCompat
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.android.synthetic.main.activity_board_lessons.*
import org.jnanaprabodhini.happyteacherapp.R
import org.jnanaprabodhini.happyteacherapp.activity.base.BottomNavigationActivity
import org.jnanaprabodhini.happyteacherapp.adapter.firestore.FirestoreObserverListAdapter
import org.jnanaprabodhini.happyteacherapp.adapter.firestore.SyllabusLessonRecyclerAdapter
import org.jnanaprabodhini.happyteacherapp.adapter.helper.FirebaseDataObserver
import org.jnanaprabodhini.happyteacherapp.dialog.BoardChoiceDialog
import org.jnanaprabodhini.happyteacherapp.extension.onItemSelected
import org.jnanaprabodhini.happyteacherapp.extension.setVisibilityGone
import org.jnanaprabodhini.happyteacherapp.extension.setVisible
import org.jnanaprabodhini.happyteacherapp.model.Subject
import org.jnanaprabodhini.happyteacherapp.model.SyllabusLesson

class BoardLessonsActivity : BottomNavigationActivity(), FirebaseDataObserver {

    @IntegerRes override val bottomNavigationMenuItemId: Int = R.id.navigation_board

    object SavedInstanceStateConstants {
        const val LEVEL_SPINNER_SELECTION = "LEVEL_SPINNER_SELECTION"
        const val SUBJECT_SPINNER_SELECTION = "SUBJECT_SPINNER_SELECTION"
    }

    private var boardQueried: String? = null

    private var levelSpinnerSelectionIndex = 0
    private var subjectSpinnerSelectionIndex = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_board_lessons)

        bottomNavigation.selectedItemId = bottomNavigationMenuItemId
        bottomNavigation.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)

        savedInstanceState?.let { setSpinnerSelectionIndicesFromSavedInstanceState(it) }

        setupRecyclerView()

        if (!prefs.hasSeenBoardChooser()) {
            // Prompt the user to select which board they would like
            //  to see syllabus lesson plans from.
            showBoardChooser()
        } else {
            initializeUi()
        }
    }

    override fun onResume() {
        super.onResume()
        if (boardQueried != prefs.getBoardKey()) {
            // If the board has changed, re-load data:
            clearAdapters()
            initializeUi()
        }
    }

    private fun showBoardChooser() {
        val dialog = BoardChoiceDialog(this)
        dialog.setOnDismissListener {
            clearAdapters()
            initializeUi()
        }
        dialog.show()
    }

    private fun setSpinnerSelectionIndicesFromSavedInstanceState(savedInstanceState: Bundle) {
        val levelSpinnerStoredSelection = savedInstanceState.getInt(SavedInstanceStateConstants.LEVEL_SPINNER_SELECTION, 0)
        val subjectSpinnerStoredSelection = savedInstanceState.getInt(SavedInstanceStateConstants.SUBJECT_SPINNER_SELECTION, 0)

        this.levelSpinnerSelectionIndex = levelSpinnerStoredSelection
        this.subjectSpinnerSelectionIndex = subjectSpinnerStoredSelection
    }

    private fun setupRecyclerView() {
        val layoutManager = LinearLayoutManager(this)
        syllabusLessonsRecyclerView.layoutManager = layoutManager

        // Leave space between each item in the list:
        val dividerItemDecoration = DividerItemDecoration(this, layoutManager.orientation)
        dividerItemDecoration.setDrawable(ResourcesCompat.getDrawable(resources, R.drawable.divider_vertical, null)!!)
        syllabusLessonsRecyclerView.addItemDecoration(dividerItemDecoration)
    }

    override fun onBottomNavigationItemReselected() {
        syllabusLessonsRecyclerView.smoothScrollToPosition(0)
    }

    private fun initializeUi() {
        boardLessonsProgressBar.setVisible()
        setupSubjectSpinner()
        boardQueried = prefs.getBoardKey()
    }

    private fun setupSubjectSpinner() {
        val subjectQuery = firestoreLocalized.collection(getString(R.string.subjects)).whereEqualTo("boards.${prefs.getBoardKey()}", true)

        val subjectDataObserver = object: FirebaseDataObserver {
            override fun onDataNonEmpty() {
                setSpinnersVisible()
                boardLessonsProgressBar.setVisibilityGone()

                if (subjectSpinner.count > subjectSpinnerSelectionIndex) {
                    subjectSpinner.setSelection(subjectSpinnerSelectionIndex)
                }
            }

            override fun onDataEmpty() {
                syllabusLessonsRecyclerView.setVisibilityGone()
                boardLessonsProgressBar.setVisibilityGone()
                statusTextView.setVisible()
                statusTextView.setText(R.string.there_are_no_lessons_available_for_this_board)
            }

            override fun onError(e: FirebaseFirestoreException?) {
                syllabusLessonsRecyclerView.setVisibilityGone()
                boardLessonsProgressBar.setVisibilityGone()
                statusTextView.setVisible()
                statusTextView.setText(R.string.there_was_an_error_loading_lessons_for_this_board)
            }
        }

        val adapter = object: FirestoreObserverListAdapter<Subject>(subjectQuery, Subject::class.java, R.layout.spinner_item, subjectDataObserver, this) {
            override fun populateView(view: View, model: Subject, position: Int) {
                (view as TextView).text = model.name
            }
        }
        adapter.startListening()

        // The level spinner depends on what subject is selected:
        subjectSpinner.onItemSelected { pos ->
            val subject = adapter.getItem(pos)
            val currentBoard = prefs.getBoardKey()
            val levelsForBoard = subject.boardStandards[currentBoard] ?: ArrayList()
            setupLevelSpinnerForSubject(levelsForBoard, adapter.getItemKey(pos))
        }

        subjectSpinner.adapter = adapter
    }

    private fun setupLevelSpinnerForSubject(levels: List<Int>, subjectId: String) {
        val previousSelection = levelSpinner.selectedItem

        // If the item that was previously selected (i.e. before the subject changed)
        //  is still available in this list, then set it to be selected
        if (previousSelection != null && previousSelection is Int) {
            val indexOfPreviousSelection = levels.indexOf(previousSelection)
            if (indexOfPreviousSelection != -1) {
                levelSpinnerSelectionIndex = indexOfPreviousSelection
            }
        }

        val adapter = object: ArrayAdapter<Int>(this, R.layout.spinner_item, levels) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
                val view = super.getView(position, convertView, parent)
                setStandardText(view as TextView, position)
                return view
            }

            override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup?): View {
                val view = super.getDropDownView(position, convertView, parent)
                setStandardText(view as TextView, position)
                return view
            }

            fun setStandardText(textView: TextView, position: Int) {
                textView.text = getString(R.string.standard_n, levels[position])
            }
        }

        // Once a level is selected, we can update the list of lessons
        levelSpinner.onItemSelected { pos -> updateSyllabusLessonList(subjectId, levels[pos]) }

        levelSpinner.adapter = adapter
        levelSpinner.setSelection(levelSpinnerSelectionIndex)
        levelSpinnerSelectionIndex = 0
    }

    private fun updateSyllabusLessonList(selectedSubjectKey: String, selectedLevel: Int) {
        val syllabusLessonQuery = firestoreLocalized.collection(getString(R.string.syllabus_lessons))
                .whereEqualTo(getString(R.string.board), prefs.getBoardKey())
                .whereEqualTo(getString(R.string.subject), selectedSubjectKey)
                .whereEqualTo(getString(R.string.level), selectedLevel)
                .orderBy(getString(R.string.lesson_number))

        val adapterOptions = FirestoreRecyclerOptions.Builder<SyllabusLesson>()
                .setQuery(syllabusLessonQuery, SyllabusLesson::class.java).build()

        val subjectName = (subjectSpinner.selectedItem as Subject).name

        val adapter = SyllabusLessonRecyclerAdapter(adapterOptions, subjectName, this, this)
        adapter.startListening()

        syllabusLessonsRecyclerView.adapter = adapter
    }

    override fun onRequestNewData() {
        statusTextView.setVisibilityGone()
        boardLessonsProgressBar.setVisible()
        syllabusLessonsRecyclerView.setVisibilityGone()
    }

    override fun onDataLoaded() {
        boardLessonsProgressBar.setVisibilityGone()
    }

    override fun onDataEmpty() {
        syllabusLessonsRecyclerView.setVisibilityGone()
        statusTextView.setVisible()
        statusTextView.setText(R.string.there_are_currently_no_lesson_plans_for_this_subject_and_level)
    }

    override fun onDataNonEmpty() {
        syllabusLessonsRecyclerView.setVisible()
        statusTextView.setVisibilityGone()

        // Animate layout changes
        syllabusLessonsRecyclerView.scheduleLayoutAnimation()
        syllabusLessonsRecyclerView.invalidate()
    }

    override fun onError(e: FirebaseFirestoreException?) {
        syllabusLessonsRecyclerView.setVisibilityGone()
        boardLessonsProgressBar.setVisibilityGone()
        statusTextView.setVisible()
        statusTextView.setText(R.string.there_was_an_error_loading_these_lesson_plans)
    }

    private fun setSpinnersVisible() {
        subjectSpinner.setVisible()
        levelSpinner.setVisible()
    }

    private fun clearAdapters() {
        subjectSpinner.adapter = null
        levelSpinner.adapter = null
        syllabusLessonsRecyclerView.adapter = null
    }

    override fun onSaveInstanceState(savedInstanceState: Bundle) {
        val levelSpinnerSelectionIndex = levelSpinner.selectedItemPosition
        val subjectSpinnerSelectionIndex = subjectSpinner.selectedItemPosition

        savedInstanceState.putInt(SavedInstanceStateConstants.LEVEL_SPINNER_SELECTION, levelSpinnerSelectionIndex)
        savedInstanceState.putInt(SavedInstanceStateConstants.SUBJECT_SPINNER_SELECTION, subjectSpinnerSelectionIndex)

        super.onSaveInstanceState(savedInstanceState)
    }

}

