package com.example.spygamers.controllers

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.spygamers.db.GamerRepository

class GamerViewModelFactory(private val repository: GamerRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GamerViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return GamerViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}