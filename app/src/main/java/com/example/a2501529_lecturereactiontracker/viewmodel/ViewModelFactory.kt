// Student No: 2501529
package com.example.a2501529_lecturereactiontracker.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

import com.example.a2501529_lecturereactiontracker.repository.LectureRepo

class ViewModelFactory(
    private val repository: LectureRepo) : ViewModelProvider.Factory{

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(MainViewModel::class.java) ->
                MainViewModel(repository) as T

            modelClass.isAssignableFrom(DetailsViewModel::class.java) ->
                DetailsViewModel(repository) as T

            modelClass.isAssignableFrom(TrackingViewModel::class.java) ->
                TrackingViewModel(repository) as T

            modelClass.isAssignableFrom(SessionDetailViewModel::class.java) ->
                SessionDetailViewModel(repository) as T

            else -> throw IllegalArgumentException("Unknown ViewModel class")
        }
    }

}