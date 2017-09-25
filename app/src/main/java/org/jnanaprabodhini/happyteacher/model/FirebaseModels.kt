package org.jnanaprabodhini.happyteacher.model

import java.util.*

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

data class SyllabusLesson(var board: String = "",
                          var lessonNumber: Int = 0,
                          var name: String = "",
                          var level: Int = 0,
                          var subject: String = "",
                          var topicCount: Int = 0)

data class SubtopicLessonHeader(var name: String = "",
                                var authorEmail: String = "",
                                var authorInstitution: String = "",
                                var authorLocation: String = "",
                                var authorName: String = "",
                                var dateEdited: Long = 0,
                                var lesson: String = "",
                                var subtopic: String = "")

data class SubtopicLesson(var name: String = "",
                          var authorEmail: String = "",
                          var authorInstitution: String = "",
                          var authorLocation: String = "",
                          var authorName: String = "",
                          var dateEdited: Long = 0,
                          var cards: Map<Int, LessonCard> = emptyMap()) {
    fun getCards(): Collection<LessonCard> = cards.values
}

data class LessonCard(var header: String = "",
                      var body: String = "",
                      var imageUrls: Map<Int, String> = emptyMap(),
                      var videoUrl: String = "",
                      var linkUrls: Map<Int, String> = emptyMap(),
                      var attachmentUrl: String = "",
                      var type: String = "",
                      var number: Int = 0) {

    fun getLinkUrls(): Collection<String> = linkUrls.values
    fun getImageUrls(): Collection<String> = imageUrls.values
    fun getCardType(): CardType = CardType.getTypeFromString(type)

    enum class CardType {
        VIDEO, IMAGE, PLAIN, UNSUPPORTED;

        companion object {
            fun getTypeFromString(typeString: String): CardType {
                when(typeString) {
                    "video" -> return VIDEO
                    "image" -> return IMAGE
                    "plain" -> return PLAIN
                    else -> return UNSUPPORTED
                }
            }
        }

    }
}
