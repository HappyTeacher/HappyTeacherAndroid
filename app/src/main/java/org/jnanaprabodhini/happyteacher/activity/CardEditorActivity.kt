package org.jnanaprabodhini.happyteacher.activity

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem
import com.google.firebase.firestore.DocumentReference
import kotlinx.android.synthetic.main.activity_card_editor.*
import kotlinx.android.synthetic.main.attachment_buttons_layout.*
import org.jnanaprabodhini.happyteacher.R
import org.jnanaprabodhini.happyteacher.activity.base.HappyTeacherActivity
import org.jnanaprabodhini.happyteacher.extension.*
import org.jnanaprabodhini.happyteacher.model.ContentCard
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.widget.EditText
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import org.jnanaprabodhini.happyteacher.adapter.EditableCardImageAdapter
import java.io.InputStream
import java.util.*
import kotlin.collections.ArrayList


class CardEditorActivity : HappyTeacherActivity() {

    companion object {
        fun launch(from: Activity, cardRef: DocumentReference, cardModel: ContentCard) {
            val lessonEditorIntent = Intent(from, CardEditorActivity::class.java)

            lessonEditorIntent.apply {
                putExtra(CARD_REF_PATH, cardRef.path)
                putExtra(CARD_MODEL, cardModel)
            }

            from.startActivity(lessonEditorIntent)
        }

        private const val CARD_REF_PATH: String = "CARDS_REF_PATH"
        fun Intent.getCardRefPath(): String = getStringExtra(CARD_REF_PATH)

        private const val CARD_MODEL: String = "CARD_MODEL"
        fun Intent.getCardModel(): ContentCard = getParcelableExtra(CARD_MODEL)
    }

    object Constants {
        const val IMAGE_REQUEST_CODE = 0

        const val ORIGINAL_CARD = "ORIGINAL_CARD"
        const val EDITED_CARD = "EDITED_CARD"
        const val IMAGE_UPLOAD_REFS = "IMAGE_UPLOAD_REFS"

        const val IMAGE_FROM_URL = "From URL"
        const val IMAGE_FROM_GALLERY = "From Gallery"
        val IMAGE_OPTIONS = arrayOf(IMAGE_FROM_URL, IMAGE_FROM_GALLERY)
    }

    private val storageRef by lazy {
        FirebaseStorage.getInstance()
    }

    private val userStorageRef by lazy {
        storageRef.getReference("user_uploads/${auth.currentUser!!.uid}")
    }

    private val cardRef by lazy { firestoreRoot.document(intent.getCardRefPath()) }
    private val imageAdapter by lazy { EditableCardImageAdapter(editedCard, this) }
    private val imageUploadRefs = ArrayList<String>()

    private lateinit var originalCard: ContentCard
    private lateinit var editedCard: ContentCard

    private var saveMenuItem: MenuItem? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_card_editor)

        originalCard = intent.getCardModel()
        editedCard = originalCard.copy()

        if (savedInstanceState?.containsKey(Constants.ORIGINAL_CARD) == true
                && savedInstanceState.containsKey(Constants.EDITED_CARD)) {
            restoreInstanceState(savedInstanceState)
        }

        populateFieldsFromCard()
        initializeUi()
    }

    private fun initializeUi() {
        updateFieldInputVisibility()

        removeVideoButton.setOnClickListener {
            hideVideoInput()
            youtubeUrlEditText.setText("")
        }

        if (editedCard.imageUrls.size >= 10) {
            addImageButton.isEnabled = false // todo: this will be undone by youtube button..
        }

        addImageButton.setOnClickListener {
            showAddImageDialog()
        }

        initializeAttachmentButtons()
    }

    private fun updateFieldInputVisibility() {
        headerTextInputLayout.setVisible()
        bodyTextInputLayout.setVisible()

        if (editedCard.youtubeId.isNotEmpty()) {
            showVideoInput()
        }

        imageRecycler.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        imageRecycler.adapter = imageAdapter

        // Todo: attachment visibility
    }

    private fun initializeAttachmentButtons() {
        attachmentButtonsLayout.setVisible()

        addVideoButton.setOnClickListener {
            showVideoInput()
        }
    }

    private val youtubeValidationTextWatcher = object: TextWatcher {
        override fun afterTextChanged(editable: Editable?) {}
        override fun beforeTextChanged(text: CharSequence?, start: Int, before: Int, count: Int) {}
        override fun onTextChanged(text: CharSequence?, star: Int, before: Int, count: Int) {
            if (text?.getYoutubeUrlId() == null) {
                youtubeUrlInputLayout.isErrorEnabled = true
                youtubeUrlInputLayout.error = getString(R.string.youtube_url_not_recognized)
                saveMenuItem?.isEnabled = false
            } else {
                youtubeUrlInputLayout.isErrorEnabled = false
                saveMenuItem?.isEnabled = true
            }
        }
    }

    private fun showAddImageDialog() {
        AlertDialog.Builder(this).apply {
            setTitle(R.string.add_an_image)
            setItems(Constants.IMAGE_OPTIONS, { dialog, which ->
                when (Constants.IMAGE_OPTIONS[which]) {
                    Constants.IMAGE_FROM_GALLERY -> getImageFromGallery()
                    Constants.IMAGE_FROM_URL -> showImageFromUrlDialog()
                }
                dialog.dismiss()
            })
            show()
        }
    }

    private fun getImageFromGallery() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, getString(R.string.choose_a_picture)), Constants.IMAGE_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == Constants.IMAGE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val stream = contentResolver.openInputStream(data?.data)
            uploadImageFromStream(stream)
        }

    }

    private fun uploadImageFromStream(stream: InputStream?) {
        showToast(R.string.uploading_image)
        stream?.let {
            // TODO: Image size limit enforcing
            // TODO: Better progress indicator
            val fileRef = userStorageRef.child(Date().time.toString())
            val fileUploadTask = fileRef.putStream(stream)

            // TODO: observable list. add listeners in one place when list is modified.
            imageUploadRefs.add(fileRef.toString())
            imageUploadProgressBar.setVisible()
            fileUploadTask.addOnSuccessListener(this, {snapshot ->
                onImageUploadSuccess(snapshot)
            })
        }
    }

    private fun onImageUploadSuccess(snapshot: UploadTask.TaskSnapshot) {
        val url = snapshot.downloadUrl.toString()
        addImageFromUrl(url)
        showToast(R.string.image_added_to_card)
        val fileRef = storageRef.getReferenceFromUrl(url)

        imageUploadRefs.remove(fileRef.toString())

        if (imageUploadRefs.isEmpty()) {
            imageUploadProgressBar.setVisibilityGone()
        }
    }

    private fun showImageFromUrlDialog() {
        val urlTextEdit = EditText(this)
        urlTextEdit.hint = getString(R.string.image_url)

        AlertDialog.Builder(this)
                .setTitle(R.string.add_image_by_url)
                .setView(urlTextEdit)
                .setPositiveButton(R.string.add, { dialog, _ ->
                    val url = urlTextEdit.text.toString()
                    addImageFromUrl(url)
                    dialog.dismiss()
                })
                .setNegativeButton(R.string.cancel, { dialog, _ ->
                    dialog.dismiss()
                })
                .show()
    }

    private fun addImageFromUrl(url: String) {
        val newImageUrls = editedCard.imageUrls.toMutableList()
        newImageUrls.add(url)
        editedCard.imageUrls = newImageUrls
        imageAdapter.notifyItemInserted(newImageUrls.lastIndex)
    }

    private fun showVideoInput() {
        addVideoButton.isEnabled = false

        // Cards don't have images AND videos
        addImageButton.isEnabled = false

        youtubeUrlInputLayout.setVisible()
        youtubeUrlEditText.addTextChangedListener(youtubeValidationTextWatcher)

        removeVideoButton.setVisible()
    }

    private fun hideVideoInput() {
        addVideoButton.isEnabled = true
        addImageButton.isEnabled = true
        youtubeUrlInputLayout.isErrorEnabled = false

        youtubeUrlInputLayout.setVisibilityGone()
        removeVideoButton.setVisibilityGone()

        youtubeUrlEditText.removeTextChangedListener(youtubeValidationTextWatcher)
        saveMenuItem?.isEnabled = true
    }

    private fun populateFieldsFromCard() {
        headerEditText.setText(editedCard.header)
        bodyEditText.setText(editedCard.body)

        if (editedCard.youtubeId.isNotEmpty()) {
            youtubeUrlEditText.setText(editedCard.youtubeId.asIdInYoutubeUrl())
        }

    }

    private fun updateEditedCardFromFields() {
        editedCard.header = headerEditText.text.toString()
        editedCard.body = bodyEditText.text.toString()

        val youtubeId = youtubeUrlEditText.text.toString().getYoutubeUrlId()
        editedCard.youtubeId = youtubeId.orEmpty()
    }

    private fun saveEditsToOriginalCard() {
        updateEditedCardFromFields()
        originalCard = editedCard
        cardRef.set(originalCard)
    }

    private fun saveAndFinish() {
        saveEditsToOriginalCard()
        finish()
    }

    private fun hasChanges(): Boolean {
        updateEditedCardFromFields()
        return editedCard != originalCard
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        updateEditedCardFromFields()
        outState?.putParcelable(Constants.ORIGINAL_CARD, originalCard)
        outState?.putParcelable(Constants.EDITED_CARD, editedCard)
        outState?.putStringArrayList(Constants.IMAGE_UPLOAD_REFS, imageUploadRefs)

        super.onSaveInstanceState(outState)
    }

    private fun restoreInstanceState(savedInstanceState: Bundle?) {
        val savedOriginalCard: ContentCard? = savedInstanceState?.getParcelable(Constants.ORIGINAL_CARD)
        val savedEditedCard: ContentCard? = savedInstanceState?.getParcelable(Constants.EDITED_CARD)
        val savedImageUploadRefs: ArrayList<String>? = savedInstanceState?.getStringArrayList(Constants.IMAGE_UPLOAD_REFS)

        savedOriginalCard?.let{ originalCard = savedOriginalCard }
        savedEditedCard?.let{ editedCard = savedEditedCard }
        savedImageUploadRefs?.let { restoreImageUploads(it) }
    }

    private fun restoreImageUploads(uploadRefs: ArrayList<String>) {
        this.imageUploadRefs.addAll(uploadRefs)
        uploadRefs.map { storageRef.getReferenceFromUrl(it) }
                .flatMap { it.activeUploadTasks }
                .forEach { task ->

                    task.addOnSuccessListener(this, { snapshot ->
                        onImageUploadSuccess(snapshot)
                    })

                }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_card_editor, menu)
        saveMenuItem = menu?.findItem(R.id.menu_save_card)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_save_card -> saveAndFinish()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun discardChangesAndFinish() {
        val originalImages = originalCard.imageUrls
        val newImages = editedCard.imageUrls
        val imagesToDiscard = newImages.minus(originalImages)

        imagesToDiscard.forEach { storageRef.deleteIfAvailable(it) }

        super.finish()
    }

    private fun cancelUploads() {
        imageUploadRefs.map { storageRef.getReferenceFromUrl(it) }
                .flatMap { it.activeUploadTasks }
                .forEach { it.cancel() }
    }

    override fun finish() {
        if (hasChanges() || imageUploadRefs.size > 0) {
            AlertDialog.Builder(this)
                    .setTitle(R.string.unsaved_changes)
                    .setMessage(R.string.you_have_changed_this_card_would_you_like_to_save_your_changes)
                    .setPositiveButton(R.string.save, {_,_ ->
                        cancelUploads()
                        saveAndFinish()
                    })
                    .setNegativeButton(R.string.discard_changes, {_, _ ->
                        cancelUploads()
                        discardChangesAndFinish()
                    })
                    .show()
        } else {
            cancelUploads()
            super.finish()
        }
    }
}
