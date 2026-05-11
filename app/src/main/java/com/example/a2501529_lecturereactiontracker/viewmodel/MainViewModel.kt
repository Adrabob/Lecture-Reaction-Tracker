// Student No: 2501529
package com.example.a2501529_lecturereactiontracker.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.a2501529_lecturereactiontracker.data.entity.Course
import com.example.a2501529_lecturereactiontracker.repository.LectureRepo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class MainViewModel( private val repository: LectureRepo) : ViewModel() {

    val allCourses: Flow<List<Course>> = repository.getAllCourses()

    //This function for adding new course
    fun insertCourse(name: String){
        viewModelScope.launch {
            val course = Course(courseName = name)
            repository.insertCourse(course)
        }
    }

    //Deleting course
    fun deleteCourse(course: Course) {
        viewModelScope.launch {
            repository.deleteCourse(course)
        }
    }
}