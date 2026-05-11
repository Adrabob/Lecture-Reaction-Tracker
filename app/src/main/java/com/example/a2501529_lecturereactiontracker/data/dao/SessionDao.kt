// Student No: 2501529
package com.example.a2501529_lecturereactiontracker.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.a2501529_lecturereactiontracker.data.entity.Course
import com.example.a2501529_lecturereactiontracker.data.entity.Session
import kotlinx.coroutines.flow.Flow

@Dao
interface SessionDao {

    @Insert
    suspend fun insertSession(session: Session): Long

    @Update
    suspend fun update(session: Session)


    @Query("SELECT * FROM sessions WHERE courseId = :courseId ORDER BY startedTimestamp DESC")
    fun getSessionsForCourse(courseId: Long): Flow<List<Session>>

    @Query("SELECT * FROM sessions WHERE sessionId = :sessionId LIMIT 1")
    suspend fun getSessionsById(sessionId: Long): Session?

    @Query("UPDATE sessions SET endedTimestamp = :endTime WHERE sessionId = :sessionId")
    suspend fun updateSessionEndTime(sessionId: Long, endTime: Long)

    @Query("DELETE FROM sessions WHERE sessionId = :sessionId")
    suspend fun deleteSessionById(sessionId: Long)

}