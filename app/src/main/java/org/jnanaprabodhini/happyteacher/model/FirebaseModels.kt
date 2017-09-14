package org.jnanaprabodhini.happyteacher.model

/**
 * Created by grahamearley on 9/7/17.
 */

data class Subject(var isActive: Boolean = false,
                   var names: Map<String, String> = emptyMap())

data class Topic(var isActive: Boolean = false,
                 var names: Map<String, String> = emptyMap(),
                 var subject: String = "")

// TODO: Update with actual object structure when complete:
data class LessonHeader(var authorId: String = "",
                        var authorInstitution: String = "",
                        var authorLocation: String = "",
                        var authorName: String = "",
                        var dateEdited: Long = 0,
                        var name: String = "")

data class SyllabusLesson(var board: String = "",
                          var lessonNumber: Int = 0,
                          var names: Map<String, String> = emptyMap(),
                          var standard: Int = 0,
                          var subject: String = "",
                          var topicCount: Int = 0)

data class Board(var isActive: Boolean = false,
                 var names: Map<String, String> = emptyMap<String, String>())