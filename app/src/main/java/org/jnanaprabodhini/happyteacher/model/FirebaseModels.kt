package org.jnanaprabodhini.happyteacher.model

/**
 * Data models for Firebase objects.
 */

data class Subject(var name: String = "",
                   var parentSubject: String = "",
                   var hasChildren: Boolean = false)

data class Topic(var name: String = "",
                 var subject: String = "")

// TODO: Update with actual object structure when complete:
data class Subtopic(var authorId: String = "",
                        var authorInstitution: String = "",
                        var authorLocation: String = "",
                        var authorName: String = "",
                        var dateEdited: Long = 0,
                        var name: String = "")

data class SyllabusLesson(var board: String = "",
                          var lessonNumber: Int = 0,
                          var name: String = "",
                          var level: Int = 0,
                          var subject: String = "",
                          var topicCount: Int = 0)

data class Board(var name: String = "")