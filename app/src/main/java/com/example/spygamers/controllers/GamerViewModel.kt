package com.example.spygamers.controllers

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.spygamers.db.GamerRepository
import com.example.spygamers.db.schemas.Gamer
import com.example.spygamers.models.messaging.DirectMessage
import com.example.spygamers.models.GamePreference
import com.example.spygamers.models.RecommendedFriend
import com.example.spygamers.services.AuthOnlyBody
import com.example.spygamers.services.ServiceFactory
import com.example.spygamers.services.authentication.AuthenticationService
import com.example.spygamers.services.directmessaging.DirectMessagingService
import com.example.spygamers.services.directmessaging.GetDirectMessagesBody
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class GamerViewModel(private val gamerRepository: GamerRepository) : ViewModel() {

    private val serviceFactory = ServiceFactory()

    //#region Init Utils
    private val _isInitializing = MutableStateFlow(true)
    val isInitializing: StateFlow<Boolean> = _isInitializing

    private suspend fun loadSessionToken() {
        val foundValue =  gamerRepository.getSessionToken().firstOrNull()
        if (foundValue == null) {
            removeSessionToken()
        } else {
            _sessionToken.value = foundValue
        }
    }

    private suspend fun loadAccountInfo() {
        val service = serviceFactory.createService(AuthenticationService::class.java)
        val response = service.checkAuthentication(AuthOnlyBody(_sessionToken.value))

        if (!response.isSuccessful) {
            Log.w("GamerViewModel", "Unsuccessful Response, removing token...")
            _sessionToken.value = ""
            return
        }

        val responseBody = response.body()
        if (responseBody == null || responseBody.status != "SUCCESS") {
            Log.d("GamerViewModel","Invalid Session Token?")
            _sessionToken.value = ""
            return
        }

        _username.value = responseBody.result.username
        _accountID.value = responseBody.result.id
    }
    //#endregion

    //#region Currently Login Data
    private val _sessionToken = MutableStateFlow<String>("")
    val sessionToken: StateFlow<String> = _sessionToken

    private val _accountID = MutableStateFlow<Int>(-1)
    val accountID: StateFlow<Int> = _accountID

    private val _username = MutableStateFlow<String>("")
    val username: StateFlow<String> = _username

    /**
     * Call this function to check against the backend server,
     * if the currently existing authentication token is valid.
     */
    suspend fun checkTokenValidity(): Boolean {
        // No session token...
        if (_sessionToken.value.isBlank()) {
            return false
        }

        try {
            val service = serviceFactory.createService(AuthenticationService::class.java)
            val response = service.checkAuthentication(AuthOnlyBody(_sessionToken.value))

            if (!response.isSuccessful) {
                return false
            }

            // No proper response body, or not success status...
            val responseBody = response.body()
            if (responseBody == null || responseBody.status != "SUCCESS") {
                removeSessionToken()
                return false
            }

            _username.value = responseBody.result.username
            _accountID.value = responseBody.result.id
            return true
        } catch (e : Exception) {
            Log.e("GamerViewModel", "Failed to check for token validity :: ", e)
            return false
        }
    }

    /**
     * Use this when logging out...
     */
    fun removeSessionToken() {
        _sessionToken.value = ""
        _username.value = ""
        _accountID.value = -1
        // TODO: Send logout request to backend server...
        viewModelScope.launch {
            gamerRepository.deleteSessionToken()
        }
    }

    /**
     * Update view models with the inserted values,
     * Updates the Room Database with the new session token...
     */
    fun upsertUserData(sessionToken: String, username: String, accountID: Int){
        _sessionToken.value = sessionToken
        _username.value = username
        _accountID.value = accountID

        viewModelScope.launch {
            gamerRepository.insertOrUpdateGamer(Gamer(sessionToken))
        }
    }
    //#endregion

    //#region Profile Viewing...
    // TODO: Refactor into 1 new ViewModel?

    private val _targetViewingAccountID = MutableStateFlow<Int>(-1)
    val targetViewingAccountID: StateFlow<Int> = _targetViewingAccountID

    private val _gamePreferences = mutableStateListOf<GamePreference>()
    val gamePreferences: List<GamePreference>  = _gamePreferences

    fun setViewingUserAccount(accountID: Int) {
        _targetViewingAccountID.value = accountID
    }

    fun setGamePreferences(preferences: Collection<GamePreference>) {
        _gamePreferences.clear()
        preferences.forEach {
            addGamePreference(it)
        }
    }

    private fun addGamePreference(preference: GamePreference) {
        Log.d("addGamePreference", "ADD: ${preference.id}, ${preference.name}")
        _gamePreferences.add(preference)
    }

    fun removeGamePreferenceByID(id: Int) {
        Log.d("addGamePreference", "REMOVE: $id")
        _gamePreferences.removeIf {
            it.id == id
        }
    }
    //#endregion

    //#region Friends Recommendation
    private val _recommendedFriends = mutableStateListOf<RecommendedFriend>()
    val recommendedFriends: List<RecommendedFriend>  = _recommendedFriends

    fun setRecommendedFriends(recommendations: Collection<RecommendedFriend>) {
        _recommendedFriends.clear()
        recommendations.forEach {
            Log.d("setRecommendedFriends", "ADD: ${it.id}, ${it.username}")
            _recommendedFriends.add(it)
        }
    }

    fun removeFriendRecommendationsByID(id: Int) {
        Log.d("removeFriendRecommendationsByID", "REMOVE: $id")
        _recommendedFriends.removeIf {
            it.id == id
        }
    }
    //#endregion

    //#region Direct Messaging

    private val _targetMessagingAccountID = MutableStateFlow<Int>(-1)
    val targetMessagingAccountID: StateFlow<Int> = _targetMessagingAccountID

    private val _targetMessagingAccountUsername = MutableStateFlow<String>("")
    val targetMessagingAccountUsername: StateFlow<String> = _targetMessagingAccountUsername

    private val _directMessages = mutableStateListOf<DirectMessage>()
    val directMessages: List<DirectMessage>  = _directMessages

    fun setDirectMessageTarget(accountID: Int, accountUsername: String) {
        _targetMessagingAccountID.value = accountID
        _targetMessagingAccountUsername.value = accountUsername
        this.viewModelScope.launch(Dispatchers.IO) {
            fetchTargetDirectMessages()
        }
    }

    suspend fun fetchTargetDirectMessages(startID: Int? = null) {
        val service = serviceFactory.createService(DirectMessagingService::class.java)

        val response = service.getDirectMessages(
            GetDirectMessagesBody(
                authToken = _sessionToken.value,
                targetAccountID = _targetMessagingAccountID.value,
                startID = startID,
                chunkSize = 75
            )
        )

        if (!response.isSuccessful) {
            Log.e("GamerViewModel.fetchTargetDirectMessages", "Failed to fetch messages :: $response")
            return
        }

        // Fetch body response, ensure its not blank...
        val responseBody = response.body()
        if (responseBody == null) {
            Log.e("GamerViewModel.fetchTargetDirectMessages", "Response body is null")
            return
        }

        setDirectMessages(responseBody.messages)
    }

    private fun setDirectMessages(directMessages: Collection<DirectMessage>) {
        _directMessages.clear()
        // NOTE: Reversed since in actual GUI, the first element should be at the bottom of the message list...
        directMessages.reversed().forEach {
            Log.d("setDirectMessages", "ADD: ${it.messageID}")
            _directMessages.add(it)
        }
    }
    //#endregion

    //#region Group Messaging
    private val _targetGroupID = MutableStateFlow<Int>(-1)
    val targetGroupID: StateFlow<Int> = _targetGroupID

    private val _groupMessages = mutableStateListOf<DirectMessage>()
    val groupMessages: List<DirectMessage>  = _groupMessages

    fun setTargetGroupID(groupID: Int) {
        _targetGroupID.value = groupID
        this.viewModelScope.launch(Dispatchers.IO) {
            fetchTargetDirectMessages()
        }
    }

    fun fetchTargetGroupMessages(){

    }


    //#endregion

    init {
        viewModelScope.launch {
            _isInitializing.value = true
            loadSessionToken()
            loadAccountInfo()
            _isInitializing.value = false
        }
    }
}