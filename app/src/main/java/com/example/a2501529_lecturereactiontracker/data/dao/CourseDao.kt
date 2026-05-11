// Student No: 2501529
package com.example.a2501529_lecturereactiontracker.data.dao


import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Delete
import androidx.room.Query
import com.example.a2501529_lecturereactiontracker.data.entity.Course
import kotlinx.coroutines.flow.Flow

@Dao
interface CourseDao {

    @Insert
    suspend fun insert(course: Course)

    @Delete
    suspend fun delete(course: Course)

    @Query("SELECT * FROM courses ORDER BY createdTimestamp DESC")
    fun getALlCourses(): Flow<List<Course>>

}