package org.jnanaprabodhini.happyteacher.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import com.google.firebase.firestore.DocumentReference
import kotlinx.android.synthetic.main.activity_card_editor.*
import kotlinx.android.synthetic.main.attachment_buttons_layout.*
import org.jnanaprabodhini.happyteacher.R
import org.jnanaprabodhini.happyteacher.activity.base.HappyTeacherActivity
import org.jnanaprabodhini.happyteacher.extension.getYoutubeUrlId
import org.jnanaprabodhini.happyteacher.extension.onTextChanged
import org.jnanaprabodhini.happyteacher.extension.setVisibilityGone
import org.jnanaprabodhini.happyteacher.extension.setVisible
import org.jnanaprabodhini.happyteacher.model.ContentCard

class CardEditorActivity : HappyTeacherActivity() {

    companion object {
        fun launch(from: Activity, cardRef: DocumentReference, newCardNumber: Int = 0, isNewCard: Boolean = true) {
            val lessonEditorIntent = Intent(from, CardEditorActivity::class.java)

            lessonEditorIntent.apply {
                putExtra(CARD_REF_PATH, cardRef.path)
                putExtra(NEW_CARD_NUMBER, newCardNumber)
                putExtra(IS_NEW_CARD, isNewCard)
            }

            from.startActivity(lessonEditorIntent)
        }

        private const val CARD_REF_PATH: String = "CARDS_REF_PATH"
        fun Intent.getCardRefPath(): String = getStringExtra(CARD_REF_PATH)

        private const val NEW_CARD_NUMBER: String = "NEW_CARD_NUMBER"
        fun Intent.getNewCardNumber(): Int = getIntExtra(NEW_CARD_NUMBER, 0)

        private const val IS_NEW_CARD: String = "IS_NEW_CARD"
        fun Intent.isNewCard(): Boolean = getBooleanExtra(IS_NEW_CARD, true)
    }

    object Constants {
        const val HEADER_TEXT = "HEADER_TEXT"
        const val BODY_TEXT = "BODY_TEXT"
    }

    private val cardRef by lazy { firestoreRoot.document(intent.getCardRefPath()) }
    private val newCardNumber by lazy { intent.getNewCardNumber() }
    private val isNewCard by lazy { intent.isNewCard() }
    private var card = ContentCard()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_card_editor)

        if (savedInstanceState?.isEmpty == false) {
            restoreInstanceState(savedInstanceState)
            initializeUi()
            return
        }

        if (isNewCard) {
            card.orderNumber = newCardNumber
            initializeUi()
        } else {
            progressBar.setVisible()
            cardRef.get().addOnSuccessListener { snapshot ->
                progressBar.setVisibilityGone()
                card = snapshot.toObject(ContentCard::class.java)
                populateFieldsFromCard()
                initializeUi()
            }
        }
    }

    private fun initializeUi() {
        updateFieldInputVisibility()
        saveButton.setVisible()

        saveButton.setOnClickListener {
            saveValuesToCard()
            finish()
        }

        removeVideoButton.setOnClickListener {
            hideVideoInput()
            youtubeUrlTextInput.setText("")
        }

        initializeYoutubeUrlValidation()

        initializeAttachmentButtons()
    }

    private fun initializeYoutubeUrlValidation() {
        youtubeUrlInputLayout.isErrorEnabled = true

        youtubeUrlTextInput.onTextChanged { text ->

            if (text?.getYoutubeUrlId() == null) {
                youtubeUrlInputLayout.error = getString(R.string.youtube_url_not_recognized)
                saveButton.isEnabled = false
            } else {
                youtubeUrlInputLayout.error = null
                saveButton.isEnabled = true
            }

        }
    }

    private fun updateFieldInputVisibility() {
        headerEditText.setVisible()
        bodyEditText.setVisible()

        if (card.youtubeId.isNotEmpty()) {
            showVideoInput()
        } else {
            hideVideoInput()
        }

        // Todo: Image visibility
        // Todo: attachment visibility
    }

    private fun initializeAttachmentButtons() {
        attachmentButtonsLayout.setVisible()
        addImageButton.isEnabled = false
        addFileButton.isEnabled = false

        addVideoButton.setOnClickListener {
            showVideoInput()
        }
    }

    private fun showVideoInput() {
        addVideoButton.isEnabled = false

        // Cards don't have images AND videos
        addImageButton.isEnabled = false

        youtubeUrlTextInput.setVisible()
        removeVideoButton.setVisible()
    }

    private fun hideVideoInput() {
        addVideoButton.isEnabled = true
        addImageButton.isEnabled = true
        youtubeUrlTextInput.setVisibilityGone()
        removeVideoButton.setVisibilityGone()
    }

    private fun populateFieldsFromCard() {
        headerEditText.setText(card.header)
        bodyEditText.setText(card.body)
    }

    private fun updateCardFromFields() {
        card.header = headerEditText.text.toString()
        card.body = bodyEditText.text.toString()
        card.youtubeId = youtubeUrlTextInput.text.toString() // todo: extract ID
    }

    private fun saveValuesToCard() {
        updateCardFromFields()
        cardRef.set(card)
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        outState?.putString(Constants.HEADER_TEXT, headerEditText.text.toString())
        outState?.putString(Constants.BODY_TEXT, bodyEditText.text.toString())

        super.onSaveInstanceState(outState)
    }

    private fun restoreInstanceState(savedInstanceState: Bundle?) {
        val headerText = savedInstanceState?.getString(Constants.HEADER_TEXT)
        val bodyText = savedInstanceState?.getString(Constants.BODY_TEXT)

        if (!headerText.isNullOrEmpty()) {
            headerEditText.setText(headerText)
        }

        if (!bodyText.isNullOrEmpty()) {
            bodyEditText.setText(bodyText)
        }
    }
}
