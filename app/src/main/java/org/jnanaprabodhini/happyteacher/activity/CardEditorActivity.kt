package org.jnanaprabodhini.happyteacher.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.google.firebase.firestore.DocumentReference
import kotlinx.android.synthetic.main.activity_card_editor.*
import org.jnanaprabodhini.happyteacher.R
import org.jnanaprabodhini.happyteacher.activity.base.HappyTeacherActivity
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

    private val cardRef by lazy { firestoreRoot.document(intent.getCardRefPath()) }
    private val newCardNumber by lazy { intent.getNewCardNumber() }
    private val isNewCard by lazy { intent.isNewCard() }
    private var card = ContentCard()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_card_editor)

        if (isNewCard) {
            card.orderNumber = newCardNumber
            initializeUi()
        } else {
            // TODO: Add progress bar
            cardRef.get().addOnSuccessListener { snapshot ->
                card = snapshot.toObject(ContentCard::class.java)
                initializeUi()
            }
        }
    }

    private fun initializeUi() {
        populateFieldsFromCard(card)

        headerEditText.setVisible()
        bodyEditText.setVisible()
        saveButton.setVisible()

        saveButton.setOnClickListener {
            saveValuesToCard()
            finish()
        }
    }

    private fun populateFieldsFromCard(card: ContentCard) {
        headerEditText.setText(card.header)
        bodyEditText.setText(card.body)
    }

    private fun updateCardFromFields() {
        card.header = headerEditText.text.toString()
        card.body = bodyEditText.text.toString()
    }

    private fun saveValuesToCard() {
        updateCardFromFields()
        cardRef.set(card)
    }
}
