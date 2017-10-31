package org.jnanaprabodhini.happyteacher.model

import android.annotation.SuppressLint
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * Data models for Firebase objects.
 */

data class Subject(var name: String = "",
                   var parentSubject: String? = null,
                   var boardStandards: Map<String, @JvmSuppressWildcards ArrayList<Int>> = emptyMap(),
                   var hasChildren: Boolean = false)

data class Topic(var name: String = "",
                 var subject: String = "")

data class Board(var name: String = "")

data class Subtopic(var name: String = "")

data class SyllabusLesson(val board: String = "",
                          val lessonNumber: Int = 0,
                          val name: String = "",
                          val level: Int = 0,
                          val subject: String = "",
                          val topicCount: Int = 0)

@Parcelize
data class CardListContentHeader(val name: String = "",
                                 val authorEmail: String = "",
                                 val authorInstitution: String = "",
                                 val authorLocation: String = "",
                                 val authorName: String = "",
                                 val dateEdited: Long = 0,
                                 val contentKey: String = "",
                                 val subtopic: String = "",
                                 val topic: String = "",
                                 val subjectName: String = "",
                                 val subtopicSubmissionCount: Int = 0): Parcelable

data class CardListContent(val name: String = "",
                           val authorEmail: String = "",
                           val authorInstitution: String = "",
                           val authorLocation: String = "",
                           val authorName: String = "",
                           val dateEdited: Long = 0,
                           val cards: Map<String, ContentCard> = emptyMap())

data class ContentCard(val header: String = "",
                       val body: String = "",
                       val imageUrls: List<String> = emptyList(),
                       val youtubeId: String = "",
                       val attachmentPath: String = "",
                       val attachmentMetadata: AttachmentMetadata = AttachmentMetadata(),
                       val type: String = "",
                       val number: Int = 0)

data class AttachmentMetadata(val contentType: String = "",
                              val size: Long = 0,
                              val timeCreated: Long = 0) {
    fun isEmpty() = contentType.isEmpty() && size == 0L && timeCreated == 0L
    fun isNotEmpty() = !isEmpty()
}

data class User(val displayName: String = "",
                val email: String = "",
                val phoneNumber: String = "",
                val location: String = "",
                val institution: String = "")