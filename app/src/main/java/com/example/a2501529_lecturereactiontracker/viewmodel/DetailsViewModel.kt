// Student No: 2501529
package com.example.a2501529_lecturereactiontracker.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.a2501529_lecturereactiontracker.data.entity.Session
import com.example.a2501529_lecturereactiontracker.repository.LectureRepo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class DetailsViewModel(private val repository: LectureRepo) : ViewModel()  {

    fun getSessions(courseId: Long): Flow<List<Session>> {
        return repository.getSessionsForCourse(courseId)
    }

    suspend fun startSession(courseId: Long): Long {
        val session = Session(courseId = courseId)
        return repository.insertSession(session)
    }


    suspend fun finishSession(courseId: Long): Long {
        val session = Session(courseId = courseId)
        return repository.insertSession(session)
    }

    fun deleteSession(session: Session) {
        viewModelScope.launch {
            repository.deleteReactionsBySession(session.sessionId)
            repository.deleteSessionById(session.sessionId)
        }
    }

    suspend fun restoreSession(session: Session) {
        val newSession = session.copy(sessionId = 0)
        repository.insertSession(newSession)
    }



}