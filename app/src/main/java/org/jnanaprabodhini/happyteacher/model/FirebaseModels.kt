package org.jnanaprabodhini.happyteacher.model

import android.annotation.SuppressLint
import android.os.Parcelable
import android.util.Log
import com.google.firebase.firestore.Exclude
import kotlinx.android.parcel.Parcelize

/**
 * Data models for Firebase objects.
 */

data class Subject(val name: String = "",
                   val parentSubject: String? = null,
                   val boardStandards: Map<String, @JvmSuppressWildcards ArrayList<Int>> = emptyMap(),
                   val hasChildren: Boolean = false)

data class Topic(val name: String = "",
                 val subject: String = "")

data class Board(val name: String = "")

data class Subtopic(val name: String = "",
                    val topic: String = "",
                    val subject: String = "",
                    val subjectName: String = "",
                    val topicName: String = "")

data class SyllabusLesson(val board: String = "",
                          val lessonNumber: Int = 0,
                          val name: String = "",
                          val level: Int = 0,
                          val subject: String = "",
                          val topicCount: Int = 0)

@Parcelize // TODO: Remove dateEdited, use @ServerTimestamp
data class CardListContentHeader(val name: String = "",
                                 val authorId: String = "",
                                 val authorInstitution: String = "",
                                 val authorLocation: String = "",
                                 val authorName: String = "",
                                 val dateEdited: Long = 0,
                                 val subtopic: String = "",
                                 val topic: String = "",
                                 val topicName: String = "",
                                 val subjectName: String = "",
                                 val subtopicSubmissionCount: Int = 0,
                                 val status: String = ""): Parcelable

data class CardListContent(val name: String = "",
                           val authorEmail: String = "",
                           val authorInstitution: String = "",
                           val authorLocation: String = "",
                           val authorName: String = "",
                           val dateEdited: Long = 0,
                           val cards: Map<String, ContentCard> = emptyMap())

@Parcelize
data class ContentCard(var header: String = "",
                       var body: String = "",
                       var imageUrls: List<String> = emptyList(),
                       var youtubeId: String = "",
                       var attachmentPath: String = "",
                       var attachmentMetadata: AttachmentMetadata = AttachmentMetadata(),
                       var orderNumber: Int = 0): Parcelable {
    @Exclude
    fun isEmpty(): Boolean {
        // Ignore order number when checking for emptiness:
        val emptyCard = ContentCard( orderNumber = orderNumber )
        return this == emptyCard
    }
}

@Parcelize
data class AttachmentMetadata(val contentType: String = "",
                              val size: Long = 0,
                              val timeCreated: Long = 0): Parcelable {
    @Exclude
    fun isEmpty() = contentType.isEmpty() && size == 0L && timeCreated == 0L

    @Exclude
    fun isNotEmpty() = !isEmpty()
}

data class User(val displayName: String = "",
                val email: String = "",
                val phoneNumber: String = "",
                val location: String = "",
                val institution: String = "",
                val role: String = "") {
    companion object Roles {
        const val ADMIN = "admin"
        const val MODERATOR = "moderator"
    }
}