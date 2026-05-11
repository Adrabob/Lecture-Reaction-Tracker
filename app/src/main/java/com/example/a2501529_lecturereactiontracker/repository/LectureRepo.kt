// Student No: 2501529
package com.example.a2501529_lecturereactiontracker.repository

import com.example.a2501529_lecturereactiontracker.data.dao.CourseDao
import com.example.a2501529_lecturereactiontracker.data.dao.ReactionDao
import com.example.a2501529_lecturereactiontracker.data.dao.SessionDao
import com.example.a2501529_lecturereactiontracker.data.entity.Course
import com.example.a2501529_lecturereactiontracker.data.entity.Reaction
import com.example.a2501529_lecturereactiontracker.data.entity.Session
import kotlinx.coroutines.flow.Flow

class LectureRepo (
    private val courseDao: CourseDao,
    private val sessionDao: SessionDao,
    private val reactionDao: ReactionDao
){

    // Implement course functions

    suspend fun insertCourse(course: Course){
        courseDao.insert(course)
    }
    suspend fun deleteCourse(course: Course){
        courseDao.delete(course)
    }

    fun getAllCourses(): Flow<List<Course>>{
        return courseDao.getALlCourses()
    }


    // Implement session function

    suspend fun insertSession(session: Session): Long {
        return sessionDao.insertSession(session)
    }


    suspend fun updateSession(session: Session){
        sessionDao.update(session)
    }

    suspend fun deleteSessionById(sessionId: Long) {
        sessionDao.deleteSessionById(sessionId)
    }

    suspend fun deleteReactionsBySession(sessionId: Long) {
        reactionDao.deleteReactionsBySession(sessionId)
    }


    fun getSessionsForCourse(courseId: Long): Flow<List<Session>>{
        return sessionDao.getSessionsForCourse(courseId)
    }

    suspend fun getSessionById(sessionId: Long): Session? {
        return sessionDao.getSessionsById(sessionId)
    }
    suspend fun updateSessionEndTime(sessionId: Long, endTime: Long) {
        sessionDao.updateSessionEndTime(sessionId, endTime)
    }



    //Implementation of reaction functions

    suspend fun insertReaction(reaction: Reaction){
        reactionDao.insert(reaction)
    }

    fun getReactionsForSession(sessionId: Long): Flow<List<Reaction>>{
        return reactionDao.getReactionsForSession(sessionId)
    }



}