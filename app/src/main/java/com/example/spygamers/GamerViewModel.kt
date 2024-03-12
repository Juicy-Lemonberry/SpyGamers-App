package com.example.spygamers

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class GamerViewModel(private val gamerRepository: GamerRepository) : ViewModel() {
    class GamerViewModelFactory(private val repository: GamerRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(GamerViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return GamerViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }

    fun insertUser(gamer: Gamer) {
        viewModelScope.launch {
            gamerRepository.insertUser(gamer)
        }
    }

    suspend fun checkUsernameAvailability(username: String): Boolean {
        return !gamerRepository.usernameExists(username)
    }

    suspend fun checkEmailAvailability(email: String): Boolean {
        return !gamerRepository.emailExists(email)
    }


}