package org.jnanaprabodhini.happyteacherapp.dialog

import android.content.Context
import android.support.constraint.ConstraintLayout
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.dialog_option_switch.view.*
import kotlinx.android.synthetic.main.dialog_settings_choice.*
import org.jnanaprabodhini.happyteacherapp.R
import org.jnanaprabodhini.happyteacherapp.adapter.firestore.FirestoreObservableListAdapter
import org.jnanaprabodhini.happyteacherapp.extension.setVisibilityGone
import org.jnanaprabodhini.happyteacherapp.extension.setVisible
import org.jnanaprabodhini.happyteacherapp.model.Board
import org.jnanaprabodhini.happyteacherapp.model.Subject
import org.jnanaprabodhini.happyteacherapp.model.User
import org.jnanaprabodhini.happyteacherapp.util.FirestoreKeys

/**
 * A dialog to allow moderators to choose what subjects they want
 *  "new submission" notifications for.
 */
class ModeratorSubjectSubscriptionsDialog(context: Context):
        SettingsChoiceDialog(context, R.string.reviewer_notification_preferences,
                R.string.moderator_select_which_subjects_you_want_to_be_notified_about) {

    override val windowHeight = ViewGroup.LayoutParams.MATCH_PARENT

    private val firestoreRoot: FirebaseFirestore by lazy {
        FirebaseFirestore.getInstance()
    }

    private val firestoreLocalized: DocumentReference by lazy {
        firestoreRoot.collection(context.getString(R.string.localized)).document(prefs.getCurrentLanguageCode())
    }

    private val firestoreUsersCollection: CollectionReference by lazy {
        firestoreRoot.collection(context.getString(R.string.users))
    }

    private val auth: FirebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }

    private val userRef: DocumentReference? by lazy {
        val userId = auth.currentUser?.uid
        if (userId == null) {
            null
        } else {
            firestoreUsersCollection.document(userId)
        }
    }

    private var previousSubjectsWatched = setOf<String>()
    private val subjectsWatched = mutableSetOf<String>()

    override fun configureOptionsListView(optionsListView: ListView) {
        // Set list height to match constraint:
        val params = optionsListView.layoutParams as ConstraintLayout.LayoutParams
        params.height = ConstraintLayout.LayoutParams.MATCH_CONSTRAINT
        optionsListView.layoutParams = params

        userRef?.get()?.addOnSuccessListener { snapshot ->
            val user = snapshot.toObject(User::class.java)
            previousSubjectsWatched = user.watchingSubjects.filter { it.value }.keys
            subjectsWatched.addAll(previousSubjectsWatched)
            setupSubjectsNotificationList()
        }
    }

    private fun setupSubjectsNotificationList() {
        val childlessSubjectsQuery = firestoreLocalized.collection(FirestoreKeys.SUBJECTS)
                .whereEqualTo(FirestoreKeys.HAS_CHILDREN, false)
        val subjectAdapter = object: FirestoreObservableListAdapter<Subject>(childlessSubjectsQuery,
                Subject::class.java, R.layout.dialog_option_switch, this, context) {
            override fun populateView(view: View, model: Subject, position: Int) {
                val subjectName = model.name
                view.textView.text = subjectName

                view.switchWidget.isChecked = subjectName in subjectsWatched

                view.switchWidget.setOnCheckedChangeListener { _, isSwitched ->
                    if (isSwitched) {
                        subjectsWatched.add(subjectName)
                    } else {
                        subjectsWatched.remove(subjectName)
                    }
                }
            }
        }

        subjectAdapter.startListening()
        setAdapter(subjectAdapter)
    }

    override fun dismiss() {
        updateSubscriptions()
        super.dismiss()
    }

    override fun cancel() {
        updateSubscriptions()
        super.cancel()
    }

    private fun updateSubscriptions() {
        val added = subjectsWatched.minus(previousSubjectsWatched)
        val removed = previousSubjectsWatched.minus(subjectsWatched)

        added.forEach(this::subscribeToSubject)
        removed.forEach(this::unsubscribeFromSubject)
    }

    private fun subscribeToSubject(subjectName: String) {
        userRef?.update("${FirestoreKeys.WATCHING_SUBJECTS}.$subjectName", true)
    }

    private fun unsubscribeFromSubject(subjectName: String) {
        userRef?.update("${FirestoreKeys.WATCHING_SUBJECTS}.$subjectName", false)
    }

}