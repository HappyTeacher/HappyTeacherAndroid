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


data class SubtopicLessonHeader(var name: String = "",
                                var authorId: String = "",
                                var authorInstitution: String = "",
                                var authorLocation: String = "",
                                var authorName: String = "",
                                var dateEdited: Long = 0)

data class Subtopic(var name: String = "",
                    var featured: SubtopicLessonHeader = SubtopicLessonHeader())

data class SyllabusLesson(var board: String = "",
                          var lessonNumber: Int = 0,
                          var name: String = "",
                          var level: Int = 0,
                          var subject: String = "",
                          var topicCount: Int = 0)

data class Board(var name: String = "")