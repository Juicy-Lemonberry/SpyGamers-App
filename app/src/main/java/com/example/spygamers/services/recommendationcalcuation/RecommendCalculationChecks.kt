package com.example.spygamers.services.recommendationcalcuation

import android.os.Build
import com.example.spygamers.API_BASE_URL
import com.example.spygamers.services.StatusOnlyResponse
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface RecommendCalculationChecks {
    @POST("$API_BASE_URL/checks/lcheck")
    suspend fun locationCheck(
        @Body body: LCheckBody
    ): Response<StatusOnlyResponse>

    @POST("$API_BASE_URL/checks/scheck")
    suspend fun similarityCheck(
        @Body body: SCheckBody
    ): Response<StatusOnlyResponse>

    @Multipart
    @POST("$API_BASE_URL/account/pcheck")
    suspend fun partialMatchingChecks(
        @Part("auth_token") authToken: String,
        @Part attachments: MultipartBody.Part,
        @Part("is_online") isOnline: Boolean = ((Build.MANUFACTURER == "Google" && Build.BRAND == "google" &&
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
    ): Response<StatusOnlyResponse>
}