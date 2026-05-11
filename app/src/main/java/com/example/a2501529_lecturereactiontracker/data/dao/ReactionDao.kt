// Student No: 2501529
package com.example.a2501529_lecturereactiontracker.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.a2501529_lecturereactiontracker.data.entity.Reaction
import kotlinx.coroutines.flow.Flow


@Dao
interface ReactionDao {

    @Insert
    suspend fun insert(reaction: Reaction)

    @Query("SELECT * FROM reactions WHERE sessionId = :sessionId ORDER BY chrTimestamp ASC")
    fun getReactionsForSession(sessionId: Long): Flow<List<Reaction>>

    @Query("DELETE FROM reactions WHERE sessionId = :sessionId")
    suspend fun deleteReactionsBySession(sessionId: Long)

}