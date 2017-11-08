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
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import org.jnanaprabodhini.happyteacher.adapter.EditableCardImageAdapter
import org.jnanaprabodhini.happyteacher.util.ObservableArrayList
import java.io.InputStream
import java.util.*
import kotlin.collections.ArrayList


class CardEditorActivity : HappyTeacherActivity() {

    companion object {
        fun launch(from: Activity, cardRef: DocumentReference, cardModel: ContentCard, subtopicId: String) {
            val lessonEditorIntent = Intent(from, CardEditorActivity::class.java)

            lessonEditorIntent.apply {
                putExtra(CARD_REF_PATH, cardRef.path)
                putExtra(CARD_MODEL, cardModel)
                putExtra(SUBTOPIC_ID, subtopicId)
            }

            from.startActivity(lessonEditorIntent)
        }

        private const val CARD_REF_PATH: String = "CARDS_REF_PATH"
        fun Intent.getCardRefPath(): String = getStringExtra(CARD_REF_PATH)

        private const val CARD_MODEL: String = "CARD_MODEL"
        fun Intent.getCardModel(): ContentCard = getParcelableExtra(CARD_MODEL)

        private const val SUBTOPIC_ID: String = "SUBTOPIC_ID"
        fun Intent.getSubtopicId(): String = getStringExtra(SUBTOPIC_ID)
    }

    object Constants {
        const val MAX_IMAGES_PER_CARD = 10

        const val IMAGE_REQUEST_CODE = 0

        const val ORIGINAL_CARD = "ORIGINAL_CARD"
        const val EDITED_CARD = "EDITED_CARD"
        const val IMAGE_UPLOAD_REFS = "IMAGE_UPLOAD_REFS"

        const val IMAGE_FROM_URL = "From URL" // todo: localize.
        const val IMAGE_FROM_GALLERY = "From Gallery"
        val IMAGE_OPTIONS = arrayOf(IMAGE_FROM_URL, IMAGE_FROM_GALLERY)
    }

    private val storageRef by lazy {
        FirebaseStorage.getInstance()
    }

    private val userStorageRef by lazy {
        storageRef.getReference("user_uploads/${auth.currentUser!!.uid}/$subtopicId")
    }

    private val userImageStorageRef by lazy {
        userStorageRef.child("images")
    }

    private val cardRef by lazy { firestoreRoot.document(intent.getCardRefPath()) }
    private val subtopicId by lazy { intent.getSubtopicId() }
    private val imageAdapter by lazy { EditableCardImageAdapter(editedCard, this) }
    private val activeImageUploadRefUrls = ObservableArrayList<String>(
            onPreAdd = { refUrl -> onImageUploadRefAdded(refUrl) },
            onPreClear = { refUrls -> onPreClearUploads(refUrls) }
    )
    private var cardTotalImageCount: Int = 0
        get() = editedCard.imageUrls.size + activeImageUploadRefUrls.size

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

        initializeUi()
    }

    private fun initializeUi() {
        headerTextInputLayout.setVisible()
        bodyTextInputLayout.setVisible()
        headerEditText.setText(editedCard.header)
        bodyEditText.setText(editedCard.body)

        imageRecycler.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        imageRecycler.adapter = imageAdapter

        if (editedCard.youtubeId.isNotEmpty()) {
            showVideoInput()
            youtubeUrlEditText.setText(editedCard.youtubeId.asIdInYoutubeUrl())
        }

        removeVideoButton.setOnClickListener {
            removeVideo()
        }

        initializeAttachmentButtons()
    }

    private fun removeVideo() {
        hideVideoInput()
        youtubeUrlEditText.setText("")
    }

    private fun initializeAttachmentButtons() {
        attachmentButtonsLayout.setVisible()

        addImageButton.setOnClickListener {
            when {
                youtubeUrlInputLayout.isVisible() -> {
                    addImageButton.jiggle()
                    parentConstraintLayout.showSnackbarWithAction(
                            message = getString(R.string.a_card_cannot_have_images_and_a_video),
                            actionName = getString(R.string.remove_video),
                            action = { _ -> removeVideo() }
                    )
                }
                cardTotalImageCount >= Constants.MAX_IMAGES_PER_CARD -> {
                    addImageButton.jiggle()
                    parentConstraintLayout.showSnackbar(getString(R.string.you_can_only_have_n_images_per_card, Constants.MAX_IMAGES_PER_CARD))
                }
                else -> showAddImageDialog()
            }
        }

        addVideoButton.setOnClickListener {
            when {
                editedCard.imageUrls.isNotEmpty() -> {
                    addVideoButton.jiggle()
                    parentConstraintLayout.showSnackbar(getString(R.string.a_card_cannot_have_images_and_a_video_remove_your_images_before_adding_a_video))
                }
                youtubeUrlInputLayout.isVisible() -> addVideoButton.jiggle()
                else -> showVideoInput()
            }
        }

        addFileButton.setOnClickListener {
            addFileButton.jiggle()
            showToast("Not yet!")
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
                    Constants.IMAGE_FROM_URL -> showAddImageUrlDialog()
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
            val fileRef = userImageStorageRef.child(Date().time.toString())
            fileRef.putStream(stream)
            activeImageUploadRefUrls.add(fileRef.toString())
        }
    }

    private fun onImageUploadRefAdded(refUrl: String) {
        imageUploadProgressBar.setVisible()
        val decodedUrl = refUrl.decode()
        val ref = storageRef.getReferenceFromUrl(decodedUrl)

        ref.activeUploadTasks.forEach { task ->
            task.addOnSuccessListener(this, imageUploadSuccessListener)
        }
    }

    private val imageUploadSuccessListener = OnSuccessListener<UploadTask.TaskSnapshot> { snapshot ->
        val url = snapshot.downloadUrl.toString()
        addImageFromUrl(url)

        showToast(R.string.image_added_to_card)

        val fileRef = storageRef.getReferenceFromUrl(url)
        activeImageUploadRefUrls.remove(fileRef.toString())

        if (activeImageUploadRefUrls.isEmpty()) {
            imageUploadProgressBar.setVisibilityGone()
        }
    }

    private fun showAddImageUrlDialog() {
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
        youtubeUrlInputLayout.setVisible()
        youtubeUrlEditText.addTextChangedListener(youtubeValidationTextWatcher)

        removeVideoButton.setVisible()
    }

    private fun hideVideoInput() {
        youtubeUrlInputLayout.isErrorEnabled = false

        youtubeUrlInputLayout.setVisibilityGone()
        removeVideoButton.setVisibilityGone()

        youtubeUrlEditText.removeTextChangedListener(youtubeValidationTextWatcher)
        saveMenuItem?.isEnabled = true
    }

    private fun updateEditedCardFromFields() {
        editedCard.header = headerEditText.text.toString()
        editedCard.body = bodyEditText.text.toString()

        val youtubeId = youtubeUrlEditText.text.toString().getYoutubeUrlId()
        editedCard.youtubeId = youtubeId.orEmpty()
    }

    private fun saveEditsToOriginalCard() {
        updateEditedCardFromFields()
        cardRef.set(editedCard)
    }

    private fun saveAndFinish() {
        deleteRemovedImagesFromFirebase()
        saveEditsToOriginalCard()
        super.finish()
    }

    private fun hasChanges(): Boolean {
        updateEditedCardFromFields()
        return editedCard != originalCard
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

    /**
     * Check for any photos that were added to the card and remove them
     *  from Firebase storage.
     */
    private fun discardChangesAndFinish() {
        val originalImages = originalCard.imageUrls
        val newImages = editedCard.imageUrls
        val imagesToDiscard = newImages.minus(originalImages)

        imagesToDiscard.forEach { storageRef.deleteIfAvailable(it) }

        super.finish()
    }

    /**
     * Check for any photos that were in the original card that
     *  are no longer in the card and remove them from Firebase storage.
     */
    private fun deleteRemovedImagesFromFirebase() {
        val originalImages = originalCard.imageUrls
        val newImages = editedCard.imageUrls
        val imagesToDiscard = originalImages.minus(newImages)

        imagesToDiscard.forEach { storageRef.deleteIfAvailable(it) }
    }

    private fun cancelUploads() {
        activeImageUploadRefUrls.clear()
    }

    private fun onPreClearUploads(refUrls: List<String>) {
        refUrls.map { storageRef.getReferenceFromUrl(it) }
                .flatMap { it.activeUploadTasks }
                .forEach { it.cancel() }
    }

    override fun finish() {
        if (hasChanges() || activeImageUploadRefUrls.size > 0) {
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

    override fun onSaveInstanceState(outState: Bundle?) {
        updateEditedCardFromFields()
        outState?.putParcelable(Constants.ORIGINAL_CARD, originalCard)
        outState?.putParcelable(Constants.EDITED_CARD, editedCard)
        outState?.putStringArrayList(Constants.IMAGE_UPLOAD_REFS, activeImageUploadRefUrls)

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
        this.activeImageUploadRefUrls.addAll(uploadRefs)
    }

}
