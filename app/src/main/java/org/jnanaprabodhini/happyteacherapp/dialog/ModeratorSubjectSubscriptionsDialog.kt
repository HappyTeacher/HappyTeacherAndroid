package org.jnanaprabodhini.happyteacherapp.dialog

import android.content.Context
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ListView
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.dialog_settings_choice.*
import org.jnanaprabodhini.happyteacherapp.R
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

    private val subjectNames = mutableListOf<String>()
    private var subjectsWatched = setOf<String>()

    override fun configureOptionsListView(optionsListView: ListView) {
        val childlessSubjectsQuery = firestoreLocalized.collection(FirestoreKeys.SUBJECTS)
                .whereEqualTo(FirestoreKeys.HAS_CHILDREN, false)

        val getUserTask = userRef?.get()
        val getSubjectsTask = childlessSubjectsQuery.get()

        getUserTask?.addOnSuccessListener { snapshot ->
            val user = snapshot.toObject(User::class.java)
            subjectsWatched = user.watchingSubjects.filter { it.value }.keys
            setupSubjectsNotificationList()
        }

        getSubjectsTask.addOnSuccessListener { querySnapshot ->
            val subjects = querySnapshot.map { it.toObject(Subject::class.java).name }
            subjectNames.addAll(subjects)
        }

        Tasks.whenAll(getSubjectsTask, getUserTask).addOnSuccessListener {
            setupSubjectsNotificationList()
        }
    }

    private fun setupSubjectsNotificationList() {
        optionsListView.choiceMode = ListView.CHOICE_MODE_MULTIPLE
        val adapter = ArrayAdapter(context, R.layout.dialog_option_multichoice, subjectNames)
        setAdapter(adapter)

        val checkedItems = mutableListOf<Int>()

        for (i in 0..subjectNames.lastIndex) {
            val subjectName = adapter.getItem(i)
            val isSubscribed = subjectName in subjectsWatched

            if (isSubscribed) checkedItems.add(i)
        }

        optionsListView.addOnLayoutChangeListener(object: View.OnLayoutChangeListener {
            override fun onLayoutChange(p0: View?, p1: Int, p2: Int, p3: Int, p4: Int, p5: Int, p6: Int, p7: Int, p8: Int) {
                checkedItems.forEach { optionsListView.setItemChecked(it, true) }
                optionsListView.removeOnLayoutChangeListener(this)
            }
        })
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
        for (i in 0..subjectNames.lastIndex) {
            val subjectName = optionsListView.adapter.getItem(i) as String
            val isSubscribed = optionsListView.isItemChecked(i)
            userRef?.update("${FirestoreKeys.WATCHING_SUBJECTS}.$subjectName", isSubscribed)
        }
    }

}