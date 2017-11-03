package org.jnanaprabodhini.happyteacher.adapter.contentlist

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.google.firebase.firestore.DocumentReference
import kotlinx.android.synthetic.main.activity_card_editor.*
import org.jnanaprabodhini.happyteacher.R
import org.jnanaprabodhini.happyteacher.activity.base.HappyTeacherActivity
import org.jnanaprabodhini.happyteacher.model.ContentCard

class CardEditorActivity : HappyTeacherActivity() {

    companion object {
        fun launch(from: Activity, cardRef: DocumentReference) {
            val lessonEditorIntent = Intent(from, CardEditorActivity::class.java)

            lessonEditorIntent.apply {
                putExtra(CARD_REF_PATH, cardRef.path)
            }

            from.startActivity(lessonEditorIntent)
        }

        private const val CARD_REF_PATH: String = "CARDS_REF_PATH"
        fun Intent.getCardRefPath(): String = getStringExtra(CARD_REF_PATH)
    }

    private val cardRef by lazy { firestoreRoot.document(intent.getCardRefPath()) }

    private var card: ContentCard = ContentCard()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_card_editor)

        // TODO: Add progress bar
        cardRef.get().addOnSuccessListener { snapshot ->

            if (snapshot.exists()) {
                card = snapshot.toObject(ContentCard::class.java)
            }

            initializeUi()
        }
    }

    private fun initializeUi() {
        populateFieldsFromCard(card)

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
