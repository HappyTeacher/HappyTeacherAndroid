package org.jnanaprabodhini.happyteacherapp.model

import android.annotation.SuppressLint
import android.os.Parcel
import android.os.Parcelable
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.Exclude
import kotlinx.android.parcel.Parcelize
import java.util.*

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

data class ResourceHeader(var name: String = "",
                          val authorId: String = "",
                          val authorInstitution: String = "",
                          val authorLocation: String = "",
                          val authorName: String = "",
                          val dateUpdated: Date = Date(),
                          val subtopic: String = "",
                          val topic: String = "",
                          val topicName: String = "",
                          val subjectName: String = "",
                          val subtopicSubmissionCount: Int = 0,
                          val status: String = "",
                          val resourceType: String = "",
                          val isFeatured: Boolean = false): Parcelable {

    // The Kotlin @Parcelize annotation crashes when parcelizing a class
    //  with booleans in Android 4.4 or below. So we do our own parcelizing here.
    //  https://youtrack.jetbrains.com/issue/KT-20597

    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            Date(parcel.readLong()),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readInt(),
            parcel.readString(),
            parcel.readString(),
            parcel.readByte() != 0.toByte())

    override fun writeToParcel(out: Parcel?, flags: Int) {
        out?.apply {
            writeString(name)
            writeString(authorId)
            writeString(authorInstitution)
            writeString(authorLocation)
            writeString(authorName)
            writeLong(dateUpdated.time)
            writeString(subtopic)
            writeString(topic)
            writeString(topicName)
            writeString(subjectName)
            writeInt(subtopicSubmissionCount)
            writeString(status)
            writeString(resourceType)
            writeInt(if (isFeatured) 1 else 0)
        }
    }

    override fun describeContents() = 0

    companion object {
        @JvmField
        val CREATOR = object : Parcelable.Creator<ResourceHeader> {
            override fun createFromParcel(parcel: Parcel): ResourceHeader {
                return ResourceHeader(parcel)
            }

            override fun newArray(size: Int): Array<ResourceHeader?> {
                return arrayOfNulls(size)
            }
        }
    }

}

@SuppressLint("ParcelCreator")
@Parcelize
data class ContentCard(var header: String = "",
                       var body: String = "",
                       var imageUrls: List<String> = emptyList(),
                       var youtubeId: String = "",
                       var attachmentPath: String = "",
                       var attachmentMetadata: AttachmentMetadata = AttachmentMetadata(),
                       var feedbackPreviewComment: String = "",
                       var feedbackPreviewCommentPath: String = "",
                       var orderNumber: Int = 0): Parcelable {
    @Exclude
    fun isEmpty(): Boolean {
        // Ignore order number when checking for emptiness:
        val emptyCard = ContentCard( orderNumber = orderNumber )
        return this == emptyCard
    }
}

@SuppressLint("ParcelCreator")
@Parcelize
data class CardComment(val commenterId: String = "",
                       val commenterName: String = "",
                       var commentText: String = "",
                       var dateUpdated: Date = Date(),
                       val reviewerComment: Boolean = false,
                       val locked: Boolean = false): Parcelable

@SuppressLint("ParcelCreator")
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
                val role: String = "")