// Student No: 2501529
package com.example.a2501529_lecturereactiontracker.viewmodel

import androidx.lifecycle.ViewModel
import com.example.a2501529_lecturereactiontracker.data.entity.Reaction
import com.example.a2501529_lecturereactiontracker.repository.LectureRepo
import kotlinx.coroutines.flow.Flow

class SessionDetailViewModel(private val repository: LectureRepo): ViewModel() {

    fun getReactions(sessionId: Long): Flow<List<Reaction>> {
        return repository.getReactionsForSession(sessionId)
    }
}