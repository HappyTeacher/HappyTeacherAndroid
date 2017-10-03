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
                                var subtopic: String = "",
                                var topic: String = "",
                                var subjectName: String = "")

data class SubtopicLesson(var name: String = "",
                          var authorEmail: String = "",
                          var authorInstitution: String = "",
                          var authorLocation: String = "",
                          var authorName: String = "",
                          var dateEdited: Long = 0,
                          var cards: Map<String, LessonCard> = emptyMap())

data class LessonCard(var header: String = "",
                      var body: String = "",
                      var imageUrls: Map<String, String> = emptyMap(),
                      var youtubeId: String = "",
                      var attachmentUrl: String = "",
                      var type: String = "",
                      var number: Int = 0) {

    fun getCardType(): CardType = CardType.getTypeFromString(type)
    fun getCardImageUrls(): List<String> = imageUrls.toSortedMap().values.toList()

    enum class CardType(val typeId: Int) {
        VIDEO(0),
        VIDEO_AND_ATTACHMENT(1),
        IMAGE(2),
        IMAGE_AND_ATTACHMENT(3),
        PLAIN(4),
        ATTACHMENT(5),
        UNSUPPORTED(-1);

        companion object {
            fun getTypeFromString(typeString: String): CardType {
                when(typeString) {
                    "VIDEO" -> return VIDEO
                    "VIDEO_AND_ATTACHMENT" -> return VIDEO_AND_ATTACHMENT
                    "IMAGE" -> return IMAGE
                    "IMAGE_AND_ATTACHMENT" -> return IMAGE_AND_ATTACHMENT
                    "ATTACHMENT" -> return ATTACHMENT
                    "PLAIN" -> return PLAIN
                    else -> return UNSUPPORTED
                }
            }

            fun fromId(id: Int): CardType {
                when (id) {
                    VIDEO.typeId -> return VIDEO
                    VIDEO_AND_ATTACHMENT.typeId -> return VIDEO_AND_ATTACHMENT
                    IMAGE.typeId -> return IMAGE
                    IMAGE_AND_ATTACHMENT.typeId -> return IMAGE_AND_ATTACHMENT
                    PLAIN.typeId -> return PLAIN
                    ATTACHMENT.typeId -> return ATTACHMENT

                    else -> return UNSUPPORTED
                }
            }
        }

    }
}
