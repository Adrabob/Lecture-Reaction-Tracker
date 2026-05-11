// Student No: 2501529
package com.example.a2501529_lecturereactiontracker.data.entity
// Session entity creates sql table to store the session data
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "sessions")
data class Session(
    @PrimaryKey(autoGenerate = true)
    val sessionId: Long = 0,            //This is our session ID for each session.

    val courseId: Long,                 //Each session needs one course so this is our session's course ID
    val startedTimestamp: Long = System.currentTimeMillis(),            //It keeps the started timestamp with millisecond.
    val endedTimestamp: Long? = null            //It keeps the ended timestamp with millisecond.
)