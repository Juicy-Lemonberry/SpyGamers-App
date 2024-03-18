package com.example.spygamers.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.spygamers.Screen
import com.example.spygamers.components.AppBar
import com.example.spygamers.components.DrawerBody
import com.example.spygamers.components.DrawerHeader
import com.example.spygamers.controllers.GamerViewModel
import com.example.spygamers.utils.generateDefaultDrawerItems
import com.example.spygamers.utils.handleDrawerItemClicked
import kotlinx.coroutines.launch

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: GamerViewModel
) {
    val scaffoldState = rememberScaffoldState()
    val scope = rememberCoroutineScope()

    var username by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var message by rememberSaveable { mutableStateOf("") }
    var auth_token by rememberSaveable { mutableStateOf("") }

    auth_token = viewModel.getSessionToken().toString()

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            AppBar(
                onNavigationIconClick = {
                    scope.launch {
                        scaffoldState.drawerState.open()
                    }
                }
            )
        },
        drawerGesturesEnabled = scaffoldState.drawerState.isOpen,
        drawerContent = {
            DrawerHeader()
            DrawerBody(
                items = generateDefaultDrawerItems(Screen.HomeScreen),
                onItemClick = {item ->
                    handleDrawerItemClicked(item, Screen.HomeScreen, navController)
                }
            )
        }
    ) {
        MainBody();
    }
}

@Composable
private fun MainBody(){
    Column(
        modifier = Modifier
            .fillMaxSize()){
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
        ) {
            Column(
                modifier = Modifier
                    .align(Alignment.Center)
            ) {
                // TODO: Load list of conversations (DM/Groups) instead...
                Text(
                    text = "No Messages",
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }
        }
    }
}