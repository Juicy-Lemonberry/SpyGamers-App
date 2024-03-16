package com.example.spygamers

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class GamerViewModel(private val gamerRepository: GamerRepository) : ViewModel() {

    private var _sessionToken: String? = null


    fun insertOrUpdateSessionToken(sessionToken: String) {
        viewModelScope.launch {
            gamerRepository.insertOrUpdateSessionToken(sessionToken)
        }
    }
    fun getSessionToken(): String? {
        viewModelScope.launch {
            _sessionToken = gamerRepository.getSessionToken().toString()
        }
        return _sessionToken
    }

    class GamerViewModelFactory(private val repository: GamerRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(GamerViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return GamerViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}