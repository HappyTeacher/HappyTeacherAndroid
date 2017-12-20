package org.jnanaprabodhini.happyteacherapp.activity

import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem
import com.google.firebase.firestore.DocumentReference
import kotlinx.android.synthetic.main.activity_card_editor.*
import kotlinx.android.synthetic.main.attachment_toolbar_layout.*
import org.jnanaprabodhini.happyteacherapp.R
import org.jnanaprabodhini.happyteacherapp.activity.base.HappyTeacherActivity
import org.jnanaprabodhini.happyteacherapp.extension.*
import org.jnanaprabodhini.happyteacherapp.model.ContentCard
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.helper.ItemTouchHelper
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import org.jnanaprabodhini.happyteacherapp.adapter.EditableCardImageAdapter
import org.jnanaprabodhini.happyteacherapp.adapter.helper.RecyclerHorizontalDragHelperCallback
import org.jnanaprabodhini.happyteacherapp.dialog.InputTextDialogBuilder
import org.jnanaprabodhini.happyteacherapp.util.ObservableArrayList
import java.io.InputStream
import java.util.*
import kotlin.collections.ArrayList
import org.jnanaprabodhini.happyteacherapp.model.AttachmentMetadata


class CardEditorActivity : HappyTeacherActivity() {

    companion object {
        fun launch(from: Activity, cardRef: DocumentReference, cardModel: ContentCard, parentContentId: String) {
            val lessonEditorIntent = Intent(from, CardEditorActivity::class.java)

            lessonEditorIntent.apply {
                putExtra(CARD_REF_PATH, cardRef.path)
                putExtra(CARD_MODEL, cardModel)
                putExtra(PARENT_CONTENT_ID, parentContentId)
            }

            from.startActivity(lessonEditorIntent)
        }

        private const val CARD_REF_PATH: String = "CARDS_REF_PATH"
        fun Intent.getCardRefPath(): String = getStringExtra(CARD_REF_PATH)

        private const val CARD_MODEL: String = "CARD_MODEL"
        fun Intent.getCardModel(): ContentCard = getParcelableExtra(CARD_MODEL)

        private const val PARENT_CONTENT_ID: String = "PARENT_CONTENT_ID"
        fun Intent.getParentContentId(): String = getStringExtra(PARENT_CONTENT_ID)
    }

    object Constants {
        const val MAX_IMAGES_PER_CARD = 10

        const val IMAGE_REQUEST_CODE = 0
        const val ATTACHMENT_REQUEST_CODE = 1

        const val ORIGINAL_CARD = "ORIGINAL_CARD"
        const val EDITED_CARD = "EDITED_CARD"
        const val IMAGE_UPLOAD_REFS = "IMAGE_UPLOAD_REFS"
        const val COMPLETE_IMAGE_UPLOADS = "COMPLETE_IMAGE_UPLOADS"
        const val ATTACHMENT_FILE_NAME = "ATTACHMENT_FILE_NAME"
    }

    private val storageRef by lazy {
        FirebaseStorage.getInstance()
    }

    private val cardStorageRef by lazy {
        storageRef.getReference("user_uploads/${auth.currentUser!!.uid}/$parentContentId/${cardRef.id}")
    }

    private val cardImageStorageRef by lazy {
        cardStorageRef.child("images")
    }

    private val cardFileStorageRef by lazy {
        cardStorageRef.child("files")
    }

    private val cardRef by lazy { firestoreRoot.document(intent.getCardRefPath()) }
    private val parentContentId by lazy { intent.getParentContentId() }
    private val imageAdapter by lazy { EditableCardImageAdapter(editedCard, this) }
    private val activeImageUploadRefUrls = ObservableArrayList<String>(
            onPreAdd = this::onImageUploadRefAdded,
            onPreClear = this::onPreClearUploads
    )
    private var attachmentFileName: String? = null
        set(value) {
            field = value
            onSetFileUploadTask()
        }
    private var uploadedImageUrls = mutableListOf<String>()
    private var cardTotalImageCount: Int = 0
        get() = editedCard.imageUrls.size + activeImageUploadRefUrls.size
    private var pendingUploadCount: Int = 0
        get() = activeImageUploadRefUrls.size + if (isUploadingAttachmentFile) 1 else 0
    private var hasPendingUploads: Boolean = pendingUploadCount > 0
        get() = pendingUploadCount > 0
    private var hasChanges: Boolean = false
        get() = editedCard != originalCard
    private var isUploadingAttachmentFile: Boolean = false
        get() {
            attachmentFileName?.let {
                val tasks = cardFileStorageRef.child(it).activeUploadTasks
                return tasks.isNotEmpty() && !tasks.first().isCanceled
            }
            return false
        }

    private var isAttachmentDeletionPending = false

    private lateinit var originalCard: ContentCard
    private lateinit var editedCard: ContentCard

    private var saveMenuItem: MenuItem? = null


    /**
     * Dialog for warning a user about unsaved changes and pending uploads,
     *  displayed before closing the activity
     */
    private val unsavedChangesAndPendingUploadsWarningDialog by lazy {
        AlertDialog.Builder(this).apply {
            setTitle(resources.getQuantityString(R.plurals.unsaved_changes_and_pending_uploads, pendingUploadCount))
            setMessage(resources.getQuantityString(R.plurals.you_have_unsaved_changes_and_n_pending_uploads, pendingUploadCount, pendingUploadCount))
            setPositiveButton(resources.getQuantityString(R.plurals.save_and_cancel_uploads, pendingUploadCount), {_,_ ->
                cancelUploads()
                saveAndFinish()
            })
            setNegativeButton(resources.getQuantityString(R.plurals.discard_changes_and_cancel_uploads, pendingUploadCount), {_,_ ->
                cancelUploads()
                discardChangesAndFinish()
            })
        }
    }

    /**
     * Dialog for warning a user about unsaved changes,
     *  displayed before closing the activity
     */
    private val unsavedChangesWarningDialog by lazy {
        AlertDialog.Builder(this).apply {
            setTitle(R.string.unsaved_changes)
            setMessage(R.string.you_have_changed_this_section_would_you_like_to_save_your_changes)
            setPositiveButton(R.string.save, {_,_ ->
                cancelUploads()
                saveAndFinish()
            })
            setNegativeButton(R.string.discard_changes, {_, _ ->
                cancelUploads()
                discardChangesAndFinish()
            })
        }
    }

    /**
     * Dialog for warning a user about pending uploads,
     *  displayed before closing the activity
     */
    private val pendingUploadsWarningDialog by lazy {
        AlertDialog.Builder(this).apply {
            setTitle(resources.getQuantityString(R.plurals.pending_uploads, pendingUploadCount))
            setMessage(resources.getQuantityString(R.plurals.you_have_n_pending_uploads, pendingUploadCount, pendingUploadCount))
            setPositiveButton(resources.getQuantityString(R.plurals.cancel_uploads, pendingUploadCount), {_,_ ->
                cancelUploads()
                discardChangesAndFinish()
            })
            setNegativeButton(R.string.dont_close, {dialog, _ ->
                dialog.dismiss()
            })
        }
    }

    /**
     * Dialog for warning a user about saving an empty card and asking
     *  if they want to delete it instead
     */
    private val emptyCardWarningDialog by lazy {
        AlertDialog.Builder(this).apply {
            setTitle(R.string.empty_section)
            setMessage(R.string.this_section_is_empty_would_you_like_to_delete_it)
            setPositiveButton(R.string.delete_section, { _, _ ->
                cardRef.delete()
                discardChangesAndFinish()
            })
            setNegativeButton(R.string.save_empty_section, { _, _ ->
                saveAndFinish()
            })
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

    private val imageUploadSuccessListener = OnSuccessListener<UploadTask.TaskSnapshot> { snapshot ->
        val url = snapshot.downloadUrl.toString()
        uploadedImageUrls.add(url)
        addImageFromUrl(url)

        showToast(R.string.image_added_to_section)

        val fileRef = storageRef.getReferenceFromUrl(url)
        activeImageUploadRefUrls.remove(fileRef.toString())

        if (activeImageUploadRefUrls.isEmpty()) {
            imageUploadProgressBar.setVisibilityGone()
        }
    }

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
        val dragHelper = ItemTouchHelper(RecyclerHorizontalDragHelperCallback(imageAdapter))
        dragHelper.attachToRecyclerView(imageRecycler)

        if (editedCard.youtubeId.isNotEmpty()) {
            showVideoInput()
            youtubeUrlEditText.setText(editedCard.youtubeId.asIdInYoutubeUrl())
        }

        if (editedCard.attachmentPath.isNotEmpty()) {
            val ref = storageRef.getReference(editedCard.attachmentPath)
            attachmentFileName = ref.name
            showFileAttachmentView()
        } else if (!attachmentFileName.isNullOrEmpty()) {
            showFileAttachmentView()
        }

        removeVideoButton.setOnClickListener {
            removeVideo()
        }

        initializeAttachmentToolbar()
    }

    private fun removeVideo() {
        hideVideoInput()
        youtubeUrlEditText.setText("")
    }

    private fun initializeAttachmentToolbar() {
        attachmentToolbarLayout.setVisible()

        addImageButton.setDrawableTop(R.drawable.ic_image_primary_32dp)
        addImageButton.setOnClickListener {
            when {
                youtubeUrlInputLayout.isVisible() -> {
                    addImageButton.jiggle()
                    parentConstraintLayout.showSnackbarWithAction(
                            message = getString(R.string.a_section_cannot_have_images_and_a_video),
                            actionName = getString(R.string.remove_video),
                            action = { removeVideo() }
                    )
                }
                cardTotalImageCount >= Constants.MAX_IMAGES_PER_CARD -> {
                    addImageButton.jiggle()
                    parentConstraintLayout.showSnackbar(getString(R.string.you_can_only_have_n_images_per_section, Constants.MAX_IMAGES_PER_CARD))
                }
                else -> showAddImageDialog()
            }
        }

        addVideoButton.setDrawableTop(R.drawable.ic_videocam_accent_32dp)
        addVideoButton.setOnClickListener {
            when {
                editedCard.imageUrls.isNotEmpty() -> {
                    addVideoButton.jiggle()
                    parentConstraintLayout.showSnackbar(R.string.a_section_cannot_have_images_and_a_video)
                }
                activeImageUploadRefUrls.isNotEmpty() -> {
                    addVideoButton.jiggle()
                    parentConstraintLayout.showSnackbarWithAction(
                            message = getString(R.string.a_section_cannot_have_images_and_a_video),
                            actionName = getString(R.string.cancel_uploads),
                            action = { cancelImageUploads() })
                }
                youtubeUrlInputLayout.isVisible() -> addVideoButton.jiggle()
                else -> showVideoInput()
            }
        }

        addFileButton.setDrawableTop(R.drawable.ic_attach_file_accent_32dp)
        addFileButton.setOnClickListener {
            if (fileAttachmentView.isVisible()) {
                addFileButton.jiggle()
                showToast(R.string.you_can_only_have_one_file_attachment_per_section)
            } else {
                getFileFromDevice()
            }
        }

    }

    private fun showAddImageDialog() {
        val fromGalleryOptionName = getString(R.string.image_from_gallery)
        val fromUrlOptionName = getString(R.string.image_from_url)
        val options = arrayOf(fromGalleryOptionName, fromUrlOptionName)

        AlertDialog.Builder(this).apply {
            setTitle(R.string.add_an_image)
            setItems(options, { dialog, which ->
                when (options[which]) {
                    fromGalleryOptionName -> getImageFromGallery()
                    fromUrlOptionName -> showAddImageUrlDialog()
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

    private fun getFileFromDevice() {
        val intent = Intent()
        intent.action = Intent.ACTION_GET_CONTENT
        intent.type = "*/*"
        startActivityForResult(Intent.createChooser(intent, getString(R.string.attach_a_file)), Constants.ATTACHMENT_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            val fileUri = data?.data
            val stream = contentResolver.openInputStream(fileUri)

            // If file name is not available, use current time
            val fileName = fileUri?.getFileName(this) ?: Date().time.toString()

            when (requestCode) {
                Constants.IMAGE_REQUEST_CODE ->
                    stream?.let { uploadImageFromStream(fileName, stream) }
                Constants.ATTACHMENT_REQUEST_CODE ->
                    stream?.let { uploadFileFromStream(fileName, stream) }
            }

        }
    }

    private fun uploadImageFromStream(fileName: String, stream: InputStream) {
        showToast(R.string.uploading_image)

        val fileRef = cardImageStorageRef.child(fileName)
        fileRef.putStream(stream)
        activeImageUploadRefUrls.add(fileRef.toString())
    }

    private fun uploadFileFromStream(fileName: String, stream: InputStream) {
        fileAttachmentView.setVisible()

        val fileRef = cardFileStorageRef.child(fileName)
        fileRef.putStream(stream)
        this.attachmentFileName = fileName
    }

    private fun onSetFileUploadTask() = attachmentFileName?.let { fileName ->
        val fileRef = cardFileStorageRef.child(fileName)

        if (fileRef.activeUploadTasks.isEmpty()) {
            onAttachmentUploadComplete(editedCard.attachmentMetadata)
        } else {
            val uploadTask = fileRef.activeUploadTasks.first()

            fileAttachmentView.setLoadingWithText(fileName)

            fileAttachmentView.setOnClickListener {
                showToast(R.string.click_and_hold_to_cancel_upload)
            }

            fileAttachmentView.setOneTimeOnLongClickListener {
                cancelAttachmentUpload()
            }

            uploadTask.addOnSuccessListener(this, { snapshot ->
                val attachmentMetadata = AttachmentMetadata(
                        contentType = snapshot.metadata?.contentType.orEmpty(),
                        size = snapshot.metadata?.sizeBytes ?: 0,
                        timeCreated = snapshot.metadata?.creationTimeMillis ?: 0
                )
                onAttachmentUploadComplete(attachmentMetadata)
            })
        }
    }

    private fun onAttachmentUploadComplete(metadata: AttachmentMetadata) {
        fileAttachmentView.setCancelIconWithText(attachmentFileName.orEmpty())
        fileAttachmentView.setProgressComplete()

        fileAttachmentView.setIconOnClickListener {
            isAttachmentDeletionPending = true
            clearAttachment()
            hideFileAttachmentView()
        }

        attachmentFileName?.let { name ->
            editedCard.attachmentPath = cardFileStorageRef.child(name).path
            editedCard.attachmentMetadata = metadata
        }
    }

    /**
     * When an upload ref (in Firebase Storage) for an image
     *  is added, get its active upload task(s) and add
     *  a success listener.
     */
    private fun onImageUploadRefAdded(refUrl: String) {
        imageUploadProgressBar.setVisible()
        val decodedUrl = refUrl.decode()
        val ref = storageRef.getReferenceFromUrl(decodedUrl)

        ref.activeUploadTasks.forEach { task ->
            task.addOnSuccessListener(this, imageUploadSuccessListener)
        }
    }

    private fun showAddImageUrlDialog() {
        InputTextDialogBuilder(this).apply {
            setTitle(R.string.add_image_by_url)
            setInputHint(getString(R.string.image_url))
            setPositiveButton(R.string.add, { dialog, url ->
                addImageFromUrl(url)
                dialog.dismiss()
            })
            setNegativeButton(R.string.cancel, DialogInterface.OnClickListener { dialog, _ ->
                dialog.dismiss()
            })
            show()
        }
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

    private fun showFileAttachmentView() {
        fileAttachmentView.setVisible()
    }

    private fun hideFileAttachmentView() {
        fileAttachmentView.setVisibilityGone()
        fileAttachmentView.resetView()
    }

    private fun updateEditedCardFromFields() {
        editedCard.header = headerEditText.text.toString()
        editedCard.body = bodyEditText.text.toString()

        val youtubeId = youtubeUrlEditText.text.toString().getYoutubeUrlId()
        editedCard.youtubeId = youtubeId.orEmpty()
    }

    private fun save() {
        updateEditedCardFromFields()

        if (isAttachmentDeletionPending) {
            deleteAttachmentFromServer()
            isAttachmentDeletionPending = false
        }

        cardRef.set(editedCard)
    }

    private fun saveAndFinishWithAlert() {
        save()

        if (hasPendingUploads) {
            pendingUploadsWarningDialog.show()
        } else {
            super.finish()
        }
    }

    private fun saveAndFinish() {
        save()
        deleteRemovedImagesFromFirebase()
        super.finish()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_card_editor, menu)
        saveMenuItem = menu?.findItem(R.id.menu_save_card)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_save_card -> saveAndFinishWithAlert()
        }
        return super.onOptionsItemSelected(item)
    }

    /**
     * Check for any photos that were added to the card and remove them
     *  from Firebase storage.
     */
    private fun discardChangesAndFinish() {
        uploadedImageUrls.forEach { storageRef.deleteIfAvailable(it) }
        super.finish()
    }

    /**
     * Check for any photos that were in the card that
     *  are no longer in the card and remove them from Firebase storage.
     */
    private fun deleteRemovedImagesFromFirebase() {
        // Create a set of all images originally in the card AND
        //  added (and perhaps removed) from the card in this editing session
        val originalImages = originalCard.imageUrls
        val imagesPreviouslyInCard = originalImages.union(uploadedImageUrls)

        val finalImages = editedCard.imageUrls

        val imagesToDiscard = imagesPreviouslyInCard.minus(finalImages)

        imagesToDiscard.forEach { storageRef.deleteIfAvailable(it) }
    }

    private fun cancelImageUploads() {
        activeImageUploadRefUrls.clear()
    }

    private fun cancelAttachmentUpload() {
        attachmentFileName?.let { name ->
            cardFileStorageRef.child(name).activeUploadTasks
                    .forEach { task -> task.cancel() }
        }
        attachmentFileName = null
        hideFileAttachmentView()
    }

    private fun cancelUploads() {
        cancelImageUploads()
        cancelAttachmentUpload()
    }

    private fun clearAttachment() {
        attachmentFileName = null
        editedCard.attachmentPath = ""
        editedCard.attachmentMetadata = AttachmentMetadata()
    }

    private fun deleteAttachmentFromServer() {
        attachmentFileName?.let { name ->
            cardFileStorageRef.child(name).delete()
        }
    }

    private fun onPreClearUploads(refUrls: List<String>) {
        refUrls.map { storageRef.getReferenceFromUrl(it) }
                .flatMap { it.activeUploadTasks }
                .forEach { it.cancel() }
        imageUploadProgressBar.setVisibilityGone()
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        updateEditedCardFromFields()
        outState?.putParcelable(Constants.ORIGINAL_CARD, originalCard)
        outState?.putParcelable(Constants.EDITED_CARD, editedCard)
        outState?.putStringArrayList(Constants.IMAGE_UPLOAD_REFS, activeImageUploadRefUrls)
        outState?.putStringArray(Constants.COMPLETE_IMAGE_UPLOADS, uploadedImageUrls.toTypedArray())
        outState?.putString(Constants.ATTACHMENT_FILE_NAME, attachmentFileName)

        super.onSaveInstanceState(outState)
    }

    private fun restoreInstanceState(savedInstanceState: Bundle?) {
        val savedOriginalCard: ContentCard? = savedInstanceState?.getParcelable(Constants.ORIGINAL_CARD)
        val savedEditedCard: ContentCard? = savedInstanceState?.getParcelable(Constants.EDITED_CARD)
        val savedImageUploadRefs: ArrayList<String>? = savedInstanceState?.getStringArrayList(Constants.IMAGE_UPLOAD_REFS)
        val savedAttachmentFileName: String? = savedInstanceState?.getString(Constants.ATTACHMENT_FILE_NAME)
        val savedCompleteImageUploads: Array<String>? = savedInstanceState?.getStringArray(Constants.COMPLETE_IMAGE_UPLOADS)

        savedOriginalCard?.let{ originalCard = savedOriginalCard }
        savedEditedCard?.let{ editedCard = savedEditedCard }
        savedImageUploadRefs?.let { restoreImageUploads(it) }
        savedAttachmentFileName?.let { attachmentFileName = it }
        savedCompleteImageUploads?.let { uploadedImageUrls = it.toMutableList() }
    }

    private fun restoreImageUploads(uploadRefs: ArrayList<String>) {
        this.activeImageUploadRefUrls.addAll(uploadRefs)
    }

    override fun finish() {
        updateEditedCardFromFields()

        // If there are any unsaved changes or pending uploads,
        //  or if the card is empty, show a warning dialog.
        if (hasChanges && hasPendingUploads) {
            unsavedChangesAndPendingUploadsWarningDialog.show()
        } else if (hasChanges) {
            unsavedChangesWarningDialog.show()
        } else if (hasPendingUploads) {
            pendingUploadsWarningDialog.show()
        } else if (editedCard.isEmpty()) {
            emptyCardWarningDialog.show()
        } else {
            super.finish()
        }
    }
}
