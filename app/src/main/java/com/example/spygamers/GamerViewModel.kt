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

    private val _allUsers = MutableLiveData<List<Gamer>>()

    private val _usernameAvailable = MutableLiveData<Boolean>()
    val usernameAvailable: LiveData<Boolean> = _usernameAvailable

    private val _emailAvailable = MutableLiveData<Boolean>()
    val emailAvailable: LiveData<Boolean> = _emailAvailable

    val allUsers: LiveData<List<Gamer>>
        get() = _allUsers

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

    suspend fun checkAndRegisterUser(username: String, email: String) {
        viewModelScope.launch {
            val isUsernameAvailable = checkUsernameAvailability(username)
            val isEmailAvailable = checkEmailAvailability(email)

            if (isUsernameAvailable && isEmailAvailable) {
                // Proceed with registration
            } else {
                // Display error messages
            }
        }
    }

}