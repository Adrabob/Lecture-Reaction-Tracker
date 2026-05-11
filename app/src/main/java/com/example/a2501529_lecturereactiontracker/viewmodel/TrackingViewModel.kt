// Student No: 2501529
package com.example.a2501529_lecturereactiontracker.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.a2501529_lecturereactiontracker.data.entity.Reaction
import com.example.a2501529_lecturereactiontracker.repository.LectureRepo
import kotlinx.coroutines.launch

class TrackingViewModel(
    private val repository: LectureRepo
) : ViewModel() {

    var sessionId: Long = -1L
    var chronometerBaseTime: Long = 0L
    var chronometerRunning: Boolean = false

     // Save reaction event to database
    fun saveReaction(timestamp: Long, type: String, note: String? = null) {
        viewModelScope.launch {
            repository.insertReaction(
                Reaction(
                    sessionId = sessionId,
                    chrTimestamp = timestamp,
                    reactionType = type,
                    reactionNotes = note
                )
            )
        }
    }
    fun finishSession(endTime: Long) {
        viewModelScope.launch {
            repository.updateSessionEndTime(sessionId, endTime)
        }
    }

    fun discardSession() {
        viewModelScope.launch {
            repository.deleteReactionsBySession(sessionId)
            repository.deleteSessionById(sessionId)
        }
    }

}
