package com.example.spygamers.screens.friendrecommendation

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.spygamers.R
import com.example.spygamers.models.RecommendedFriend
import com.example.spygamers.models.WeightageCategory

@Composable
fun FriendRecommendationRow(
    recommendation: RecommendedFriend,
    onViewProfile: (targetID: Int) -> Unit,
    onRequestSent: (targetID: Int) -> Unit
){
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth().padding(2.dp)
    ) {
        // TODO: Fetch and show actual profile picture.
        Image(
            painter = painterResource(id = R.drawable.ic_launcher_foreground),
            contentDescription = "Profile Picture",
            modifier = Modifier
                .size(50.dp)
                .background(Color.Magenta)
                .padding(6.dp)
                .weight(0.75f)
        )

        val recommendationReason = generateReasonToBefriend(recommendation)

        // Name, and reason to befriend
        Column(modifier = Modifier
            .fillMaxWidth()
            .clickable{onViewProfile(recommendation.id)}
            .padding(8.dp)
            .weight(4f)
        ) {
            Text(text = recommendation.username, style = MaterialTheme.typography.subtitle2)
            if (recommendationReason.isNotEmpty()) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = recommendationReason,
                    style = MaterialTheme.typography.caption,
                    color = MaterialTheme.colors.secondary
                )
            }
        }

        // Accept icon
        IconButton(
            onClick = {
                onRequestSent(recommendation.id)
            },
            modifier = Modifier
                .padding(4.dp)
                .background(MaterialTheme.colors.primary)
                .weight(0.8f)
        ) {
            Icon(
                Icons.Default.Send,
                contentDescription = "Send",
                tint = Color.White
            )
        }
    }
}

private fun generateReasonToBefriend(recommendation: RecommendedFriend): String{
    var recommendationReason = ""
    if (recommendation.highestWeightage() == WeightageCategory.GAME_PREFERENCE) {
        recommendationReason = "Recommended due to similar game preferences."
    } else if (recommendation.highestWeightage() == WeightageCategory.TIMEZONE) {
        recommendationReason = "Recommended due to similar timezones."
    } else if (recommendation.highestWeightage() == WeightageCategory.SAME_GROUP) {
        recommendationReason = "Recommended due to being in same groups."
    }

    return recommendationReason
}