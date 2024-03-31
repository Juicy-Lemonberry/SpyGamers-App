package com.example.spygamers.screens.recommendgroups

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import com.example.spygamers.models.RecommendedGroup

@Composable
fun GroupRecommendationRow(
    recommendation: RecommendedGroup,
    onJoinRequest: (group: RecommendedGroup) -> Unit
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

        // Name and group description
        Column(modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .weight(4f)
        ) {
            Text(text = recommendation.name, style = MaterialTheme.typography.subtitle2)
            if (recommendation.description.isNotEmpty()) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = recommendation.description,
                    style = MaterialTheme.typography.caption,
                    color = MaterialTheme.colors.secondary
                )
            }
        }

        // Accept icon
        IconButton(
            onClick = {
                onJoinRequest(recommendation)
            },
            modifier = Modifier
                .padding(4.dp)
                .background(MaterialTheme.colors.primary)
                .weight(0.8f)
        ) {
            Icon(
                Icons.Default.Send,
                contentDescription = "Join",
                tint = Color.White
            )
        }
    }
}