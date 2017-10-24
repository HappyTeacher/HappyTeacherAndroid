package org.jnanaprabodhini.happyteacher.activity

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
import org.jnanaprabodhini.happyteacher.R
import org.jnanaprabodhini.happyteacher.activity.base.BottomNavigationActivity
import org.jnanaprabodhini.happyteacher.adapter.firestore.FirestoreObserverListAdapter
import org.jnanaprabodhini.happyteacher.adapter.firestore.SyllabusLessonRecyclerAdapter
import org.jnanaprabodhini.happyteacher.adapter.helper.FirebaseDataObserver
import org.jnanaprabodhini.happyteacher.dialog.BoardChoiceDialog
import org.jnanaprabodhini.happyteacher.extension.onItemSelected
import org.jnanaprabodhini.happyteacher.extension.selectIndexWhenPopulated
import org.jnanaprabodhini.happyteacher.extension.setVisibilityGone
import org.jnanaprabodhini.happyteacher.extension.setVisible
import org.jnanaprabodhini.happyteacher.model.Subject
import org.jnanaprabodhini.happyteacher.model.SyllabusLesson
import org.jnanaprabodhini.happyteacher.prefs


class BoardLessonsActivity : BottomNavigationActivity(), FirebaseDataObserver {

    @IntegerRes override val bottomNavigationMenuItemId: Int = R.id.navigation_board

    object SavedInstanceStateConstants {
        const val LEVEL_SPINNER_SELECTION = "LEVEL_SPINNER_SELECTION"
        const val SUBJECT_SPINNER_SELECTION = "SUBJECT_SPINNER_SELECTION"
    }

    private var levelSpinnerSelectionIndex = 0
    private var subjectSpinnerSelectionIndex = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_board_lessons)

        bottomNavigation.selectedItemId = bottomNavigationMenuItemId
        bottomNavigation.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)

        savedInstanceState?.let { setSpinnerSelectionIndicesFromSavedInstanceState(it) }

        setupRecyclerView()

        if (!prefs.hasChosenBoard()) {
            // Prompt the user to select which board they would like
            //  to see syllabus lesson plans from.
            showBoardChooser()
        } else {
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
    }

    private fun setupSubjectSpinner() {
        val subjectQuery = firestoreLocalized.collection(getString(R.string.subjects)).whereEqualTo("boards.${prefs.getBoardKey()}", true)

        val subjectDataObserver = object: FirebaseDataObserver {
            override fun onDataNonEmpty() {
                setSpinnersVisible()
                boardLessonsProgressBar.setVisibilityGone()
            }
        }

        val adapter = object: FirestoreObserverListAdapter<Subject>(subjectQuery, Subject::class.java, R.layout.spinner_item, subjectDataObserver, this) {
            override fun populateView(view: View, model: Subject) {
                (view as TextView).text = model.name
            }
        }
        adapter.startListening()

        // The level spinner depends on what subject is selected:
        subjectSpinner.onItemSelected { pos -> setupLevelSpinnerForSubject(adapter.getItem(pos).getLevelsArrayForCurrentBoard(), adapter.getItemKey(pos)) }

        subjectSpinner.adapter = adapter
        subjectSpinner.selectIndexWhenPopulated(subjectSpinnerSelectionIndex)
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
                (view as TextView).text = getString(R.string.standard_n, levels[position])
                return view
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
    }

    override fun onDataLoaded() {
        boardLessonsProgressBar.setVisibilityGone()
    }

    override fun onDataEmpty() {
        statusTextView.setVisible()
        statusTextView.setText(R.string.there_are_currently_no_lesson_plans_for_this_subject_and_level)
    }

    override fun onDataNonEmpty() {
        statusTextView.setVisibilityGone()

        // Animate layout changes
        syllabusLessonsRecyclerView.scheduleLayoutAnimation()
        syllabusLessonsRecyclerView.invalidate()
    }

    override fun onError(e: FirebaseFirestoreException?) {
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

