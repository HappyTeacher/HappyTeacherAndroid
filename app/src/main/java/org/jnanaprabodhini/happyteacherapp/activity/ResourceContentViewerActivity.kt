package org.jnanaprabodhini.happyteacherapp.activity

import android.support.v7.app.AlertDialog
import android.view.Menu
import android.view.MenuItem
import org.jnanaprabodhini.happyteacherapp.R
import org.jnanaprabodhini.happyteacherapp.extension.showToast
import org.jnanaprabodhini.happyteacherapp.model.User
import org.jnanaprabodhini.happyteacherapp.util.FirestoreKeys
import org.jnanaprabodhini.happyteacherapp.util.ResourceStatus
import org.jnanaprabodhini.happyteacherapp.util.ResourceType
import org.jnanaprabodhini.happyteacherapp.util.UserRoles

/**
 * A parent activity for resource content viewers (as opposed to editors).
 *  Sets up a menu with Admin/Mod tools.
 */
abstract class ResourceContentViewerActivity: ResourceContentActivity() {

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_cards_viewer, menu)
        val editLessonMenuItem = menu?.findItem(R.id.menu_admin_edit_card_list_content)
        val promoteToFeaturedLessonMenuItem = menu?.findItem(R.id.menu_admin_promote_to_featured)
        editLessonMenuItem?.isVisible = false
        promoteToFeaturedLessonMenuItem?.isVisible = false

        // Only show admin/mod buttons to admins/mods!
        //  (Our Firestore security rules also only allow writes from admins)
        auth.currentUser?.uid?.let { uid ->
            firestoreUsersCollection.document(uid).get().addOnSuccessListener { snapshot ->
                val user = snapshot.toObject(User::class.java)
                if (user.role == UserRoles.ADMIN) {
                    editLessonMenuItem?.isVisible = true
                }

                if (header.resourceType == ResourceType.LESSON
                        && !header.isFeatured
                        && header.status == ResourceStatus.PUBLISHED
                        && user.role == UserRoles.ADMIN || user.role == UserRoles.MODERATOR) {
                    promoteToFeaturedLessonMenuItem?.isVisible = true
                }
            }
        }

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        super.onOptionsItemSelected(item)
        when (item.itemId) {
            R.id.menu_admin_edit_card_list_content -> openInEditor()
            R.id.menu_admin_promote_to_featured -> showPromoteToFeaturedLessonDialog()
        }
        return true
    }

    private fun openInEditor() {
        ResourceContentEditorActivity.launch(this, contentRef, header)
    }

    private fun showPromoteToFeaturedLessonDialog() {
        AlertDialog.Builder(this)
                .setTitle(R.string.do_you_want_to_set_this_as_the_featured_lesson)
                .setMessage(R.string.this_lesson_will_replace_current_featured_lesson_warning)
                .setPositiveButton(R.string.yes, { dialog, _ ->
                    contentRef.update(FirestoreKeys.IS_FEATURED, true).addOnSuccessListener {
                        showToast(R.string.lesson_set_to_featured)
                    }
                    dialog.dismiss()
                })
                .setNegativeButton(R.string.cancel, { dialog, _ -> dialog.dismiss() })
                .show()
    }

}