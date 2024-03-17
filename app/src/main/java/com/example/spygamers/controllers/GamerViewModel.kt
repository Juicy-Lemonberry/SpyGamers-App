package com.example.spygamers.controllers

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.spygamers.db.schemas.Gamer
import com.example.spygamers.db.GamerRepository
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class GamerViewModel(private val gamerRepository: GamerRepository) : ViewModel() {
    private var _sessionToken: String? = null
    private var _accountID: Int? = null
    private var _username: String? = null

    fun insertOrUpdateGamer(gamer: Gamer) {
        viewModelScope.launch {
            gamerRepository.insertOrUpdateGamer(gamer)
        }
    }
    fun getSessionToken(): String? {
        viewModelScope.launch {
            _sessionToken = gamerRepository.getSessionToken().firstOrNull()
        }
        return _sessionToken
    }

    fun getAccountID(): Int? {
        viewModelScope.launch {
            _accountID = gamerRepository.getAccountId().firstOrNull()
        }
        return _accountID
    }

    fun getUsername(): String? {
        viewModelScope.launch {
            // TODO: Assign username from backend API
        }
        return _username
    }
}