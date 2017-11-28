package org.jnanaprabodhini.happyteacherapp.activity

import android.app.Activity
import android.net.Uri
import android.support.v7.app.AlertDialog
import android.view.Menu
import android.view.MenuItem
import org.jnanaprabodhini.happyteacherapp.R
import org.jnanaprabodhini.happyteacherapp.extension.showToast
import org.jnanaprabodhini.happyteacherapp.util.FirestoreKeys
import org.jnanaprabodhini.happyteacherapp.util.ResourceStatus
import org.jnanaprabodhini.happyteacherapp.util.ResourceType
import com.google.firebase.dynamiclinks.DynamicLink
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import com.google.firebase.firestore.DocumentReference
import org.jnanaprabodhini.happyteacherapp.dialog.InputTextDialogBuilder
import org.jnanaprabodhini.happyteacherapp.extension.decode
import org.jnanaprabodhini.happyteacherapp.model.ResourceHeader
import android.content.Intent
import android.util.Log


/**
 * A parent activity for resource content viewers (as opposed to editors).
 *  Sets up a menu with Admin/Mod tools.
 */
abstract class ResourceViewerActivity : ResourceActivity() {

    companion object {
        fun launchViewerForResource(from: Activity, resourceRef: DocumentReference) {
            resourceRef.get().addOnSuccessListener { documentSnapshot ->
                val header = documentSnapshot.toObject(ResourceHeader::class.java)
                when (header.resourceType) {
                    ResourceType.LESSON -> LessonViewerActivity.launch(from, resourceRef, header, true)
                    ResourceType.CLASSROOM_RESOURCE -> ClassroomResourceViewerActivity.launch(from, resourceRef, header)
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_cards_viewer, menu)
        val editLessonMenuItem = menu?.findItem(R.id.menu_admin_edit_card_list_content)
        val promoteToFeaturedLessonMenuItem = menu?.findItem(R.id.menu_mod_promote_to_featured)
        val unpublishMenuItem = menu?.findItem(R.id.menu_admin_unpublish)

        editLessonMenuItem?.isVisible = false
        promoteToFeaturedLessonMenuItem?.isVisible = false
        unpublishMenuItem?.isVisible = false

        // Only show admin/mod buttons to admins/mods!
        //  (Our Firestore security rules also only allow writes from admins)
        if (prefs.userIsAdmin()) {
            editLessonMenuItem?.isVisible = true
        }

        if (header.resourceType == ResourceType.LESSON
                && !header.isFeatured
                && header.status == ResourceStatus.PUBLISHED
                && (prefs.userIsAdmin() || prefs.userIsMod())) {
            promoteToFeaturedLessonMenuItem?.isVisible = true
        }

        if (header.status == ResourceStatus.PUBLISHED && prefs.userIsAdmin()) {
            unpublishMenuItem?.isVisible = true
        }

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        super.onOptionsItemSelected(item)
        when (item.itemId) {
            R.id.menu_admin_edit_card_list_content -> openInEditor()
            R.id.menu_admin_unpublish -> unpublish()
            R.id.menu_mod_promote_to_featured -> showPromoteToFeaturedLessonDialog()
            R.id.menu_resource_share -> shareResourceLink()
        }
        return true
    }

    private fun openInEditor() {
        ResourceEditorActivity.launch(this, contentRef, header)
    }

    private fun unpublish() {
        showToast(R.string.unpublishing)
        contentRef.update(FirestoreKeys.STATUS, ResourceStatus.DRAFT)
                .addOnSuccessListener {
                    showToast(R.string.unpublished)
                }
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

    private fun shareResourceLink() {
        val deepLinkUrl = "http://happyteacher.jnanaprabodhini.org/${contentRef.path}"
        val appName = getString(R.string.app_name)
        val shareTitle = "${header.name} ($appName)"

        val dynamicLink = FirebaseDynamicLinks.getInstance().createDynamicLink()
                .setLink(Uri.parse(deepLinkUrl))
                .setDynamicLinkDomain(FirestoreKeys.DYNAMIC_LINK_DOMAIN)
                .setAndroidParameters(DynamicLink.AndroidParameters
                        .Builder()
                        .setMinimumVersion(27) // 27 is the version where deeplinking was introduced
                        .build())
                .buildDynamicLink().uri.toString().decode()

        val shareUrlIntent = Intent(Intent.ACTION_SEND)
        shareUrlIntent.apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, shareTitle)
            putExtra(Intent.EXTRA_TEXT, "$shareTitle $dynamicLink")
        }

        startActivity(shareUrlIntent)
    }

}