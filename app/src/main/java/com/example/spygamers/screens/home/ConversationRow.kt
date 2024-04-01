package com.example.spygamers.screens.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.spygamers.R
import com.example.spygamers.components.ProfilePictureIcon
import com.example.spygamers.models.ConversationActivity
import com.example.spygamers.utils.shortenText

@Composable
fun ConversationRow(
    conversation: ConversationActivity,
    onConversationSelected: (conversation: ConversationActivity) -> Unit
){
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        // TODO: Replace with actual group picture...
        ProfilePictureIcon()

        // Name of group, with onclick callback to selected...
        Column(modifier = Modifier
            .fillMaxWidth()
            .clickable { onConversationSelected(conversation) }
            .padding(8.dp)
            .weight(4f)
        ) {

            if (conversation.type == "GROUP") {
                Row {
                    // Image indicating its a group, and name of group
                    Image(
                        painter = painterResource(id = R.drawable.ic_friends),
                        contentDescription = "Group Chat",
                        modifier = Modifier.size(25.dp).padding(8.dp)
                    )
                    Spacer(modifier = Modifier.height(1.dp))
                    Text(
                        text = conversation.name,
                        style = MaterialTheme.typography.body1
                    )
                }
            } else {
                // Just name of direct message target...
                Text(
                    text = conversation.name,
                    style = MaterialTheme.typography.body1
                )
            }

            // Small spacer, before showing content of activity...
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = shortenText(conversation.content),
                style = MaterialTheme.typography.caption,
                color = MaterialTheme.colors.secondary,
            )
        }
    }
}