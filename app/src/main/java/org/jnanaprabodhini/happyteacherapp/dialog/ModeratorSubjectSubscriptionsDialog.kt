package org.jnanaprabodhini.happyteacherapp.dialog

import android.content.Context
import android.view.View
import android.widget.ListView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.dialog_option_switch.view.*
import org.jnanaprabodhini.happyteacherapp.R
import org.jnanaprabodhini.happyteacherapp.adapter.firestore.FirestoreObservableListAdapter
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

    private var subjectsWatched = mutableSetOf<String>()

    override fun configureOptionsListView(optionsListView: ListView) {
        userRef?.get()?.addOnSuccessListener { snapshot ->
            val user = snapshot.toObject(User::class.java)

            // Filter out subjects whose value is `false`
            subjectsWatched = user.watchingSubjects.filter { it.value }.keys.toMutableSet()

            setupSubjectsNotificationList()
        }
    }

    private fun setupSubjectsNotificationList() {
        val childlessSubjectsQuery = firestoreLocalized.collection(FirestoreKeys.SUBJECTS)
                .whereEqualTo(FirestoreKeys.HAS_CHILDREN, false)

        val subjectSubscriptionAdapter = object: FirestoreObservableListAdapter<Subject>(childlessSubjectsQuery,
                Subject::class.java, R.layout.dialog_option_switch, this, context) {
            override fun populateView(view: View, model: Subject, position: Int) {
                val textView = view.textView
                val switch = view.switchView

                switch.isChecked = model.name in subjectsWatched
                textView.text = model.name

                switch.setOnCheckedChangeListener { _, isChecked ->
                    if (isChecked) {
                        subscribeToSubject(model.name)
                    } else {
                        unsubscribeFromSubject(model.name)
                    }
                }
            }
        }

        subjectSubscriptionAdapter.startListening()
        setAdapter(subjectSubscriptionAdapter)
    }

    private fun unsubscribeFromSubject(subjectName: String) {
        userRef?.update("${FirestoreKeys.WATCHING_SUBJECTS}.$subjectName", false)
        subjectsWatched.remove(subjectName)
    }

    private fun subscribeToSubject(subjectName: String) {
        userRef?.update("${FirestoreKeys.WATCHING_SUBJECTS}.$subjectName", true)
        subjectsWatched.add(subjectName)
    }

}