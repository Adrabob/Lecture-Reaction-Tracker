// Student No: 2501529
package com.example.a2501529_lecturereactiontracker.data.entity
//This entity creates table for the course.
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "courses")
data class Course(
    @PrimaryKey(autoGenerate = true)
    val courseId: Long = 0,         // This is our course ID for the table.

    val courseName: String,         //This is for the course names.
    val createdTimestamp: Long = System.currentTimeMillis()     //This stores the created time with milliseconds
)