// Student No: 2501529
package com.example.a2501529_lecturereactiontracker.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.a2501529_lecturereactiontracker.data.dao.CourseDao
import com.example.a2501529_lecturereactiontracker.data.dao.ReactionDao
import com.example.a2501529_lecturereactiontracker.data.dao.SessionDao
import com.example.a2501529_lecturereactiontracker.data.entity.Course
import com.example.a2501529_lecturereactiontracker.data.entity.Reaction
import com.example.a2501529_lecturereactiontracker.data.entity.Session


@Database(
    entities = [Course::class, Session::class, Reaction::class],
    version = 1,
    exportSchema = false
)
 abstract class SystemDatabase: RoomDatabase() {

     // DAO access gates are below
    abstract fun courseDao(): CourseDao
    abstract fun sessionDao(): SessionDao
    abstract fun reactionDao(): ReactionDao

    companion object{
        @Volatile
        private var INSTANCE: SystemDatabase? = null

        fun getDatabase(context: Context): SystemDatabase{
            return INSTANCE ?: synchronized(this){      //We want create our database just once this is lock the Room if it try to create second database
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SystemDatabase::class.java,
                    "lecture_tracker_db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }

}