package com.example.spygamers.screens.viewprofile

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.spygamers.models.GamePreference

/**
 * @param isSelfPreference True if this section is for viewing the user's own preferences.
 * @param gamePreferences Game preferences to show
 * @param onAddPreferenceRequest Callback when user requests to add a new game preference. (Only used when `isSelfPreference` is true)
 * @param onDeletePreferenceRequest Callback when a user requests to delete a selected game preference. (Only used when `isSelfPreference` is true)
 */
@Composable
fun GamePreferenceSection(
    isSelfPreference: Boolean,
    gamePreferences: Collection<GamePreference>,
    onAddPreferenceRequest: () -> Unit = {},
    onDeletePreferenceRequest: (GamePreference) -> Unit = {},
) {
    if (!isSelfPreference) {
        Column(modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)) {
            Text(text = "Game Preferences", style = MaterialTheme.typography.h5)
            if (gamePreferences.isEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "User has no game preferences...", style = MaterialTheme.typography.body1)
            }
        }
    } else {
        // Game preference header, but can tap to request edit...
        Column(modifier = Modifier
            .fillMaxWidth()
            .clickable { onAddPreferenceRequest() }
            .padding(16.dp)) {
            Text(text = "Game Preferences", style = MaterialTheme.typography.h5)
            Spacer(modifier = Modifier.height(2.dp))

            val subtitleText = (if (gamePreferences.isEmpty()) "No game preferences set...\n" else "") + "Tap to add new preference"
            Text(
                text = subtitleText,
                style = MaterialTheme.typography.caption,
                color = MaterialTheme.colors.secondary
            )
        }
    }

    for (preference in gamePreferences) {
        Log.v("GamePreferenceSection", "CREATE ROW: ${preference.id}")
        GamePreferenceRow(isSelfPreference, preference, onDeletePreferenceRequest)
    }
}

@Composable
private fun GamePreferenceRow(
    isSelfPreference: Boolean,
    gamePreference: GamePreference,
    onDeletePreferenceRequest: (GamePreference) -> Unit
) {
    if (!isSelfPreference) {
        Column(modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)) {
                Text(text = gamePreference.name, style = MaterialTheme.typography.body1)
            }
    } else {
        // Game preference header, but can tap to request edit...
        Column(modifier = Modifier
            .fillMaxWidth()
            .clickable { onDeletePreferenceRequest(gamePreference) }
            .padding(16.dp)) {
            Text(text = gamePreference.name, style = MaterialTheme.typography.body1)
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "Tap to delete preference",
                style = MaterialTheme.typography.caption,
                color = MaterialTheme.colors.secondary
            )
        }
    }
}