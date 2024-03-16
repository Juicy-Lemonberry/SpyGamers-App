package com.example.spygamers

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class GamerViewModel(private val gamerRepository: GamerRepository) : ViewModel() {

    private var _sessionToken: String? = null
    private var _accountID: Int? = null
    private var _timezoneCode: String? = null


    fun insertOrUpdateGamer(gamer: Gamer) {
        viewModelScope.launch {
            gamerRepository.insertOrUpdateGamer(gamer)
        }
    }
    fun getSessionToken(): String? {
        viewModelScope.launch {
            _sessionToken = gamerRepository.getSessionToken().toString()
        }
        return _sessionToken
    }

    fun getAccountID(): Int? {
        viewModelScope.launch {
            _accountID = gamerRepository.getAccountId().firstOrNull()
        }
        return _accountID
    }

    fun getTimezoneCode(): String? {
        viewModelScope.launch {
            _timezoneCode = gamerRepository.getTimezoneCode().toString()
        }
        return _timezoneCode
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