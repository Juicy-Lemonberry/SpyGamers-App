package com.example.spygamers.components

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.spygamers.R

@Composable
fun ProfilePictureIcon(
    painter: Painter = painterResource(id = R.drawable.ic_launcher_foreground),

    @SuppressLint("ModifierParameter")
    modifier: Modifier = Modifier.size(50.dp).background(Color.Magenta).padding(8.dp)
){
    Image(
        painter = painter,
        contentDescription = "Profile Picture",
        modifier = modifier
    )
}