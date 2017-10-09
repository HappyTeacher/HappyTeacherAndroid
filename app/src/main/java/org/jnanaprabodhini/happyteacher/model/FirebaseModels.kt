package org.jnanaprabodhini.happyteacher.model

/**
 * Data models for Firebase objects.
 */

data class Subject(var name: String = "",
                   var parentSubject: String = "",
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

data class SubtopicLessonHeader(val name: String = "",
                                val authorEmail: String = "",
                                val authorInstitution: String = "",
                                val authorLocation: String = "",
                                val authorName: String = "",
                                val dateEdited: Long = 0,
                                val lesson: String = "",
                                val subtopic: String = "",
                                val topic: String = "",
                                val subjectName: String = "",
                                val subtopicSubmissionCount: Int = 0)

data class SubtopicLesson(val name: String = "",
                          val authorEmail: String = "",
                          val authorInstitution: String = "",
                          val authorLocation: String = "",
                          val authorName: String = "",
                          val dateEdited: Long = 0,
                          val cards: Map<String, LessonCard> = emptyMap())

data class LessonCard(val header: String = "",
                      val body: String = "",
                      val imageUrls: Map<String, String> = emptyMap(),
                      val youtubeId: String = "",
                      val attachmentPath: String = "",
                      val attachmentMetadata: AttachmentMetadata = AttachmentMetadata(),
                      val type: String = "",
                      val number: Int = 0) {
    fun getCardImageUrls(): List<String> = imageUrls.toSortedMap().values.toList()
}

data class AttachmentMetadata(val contentType: String = "",
                              val size: Long = 0,
                              val timeCreated: Long = 0) {
    fun isEmpty() = contentType.isEmpty() && size == 0L && timeCreated == 0L
    fun isNotEmpty() = !isEmpty()
}