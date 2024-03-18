package com.example.spygamers.models

import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp
import com.example.spygamers.utils.ImageResource

/**
 * Represents a menu item in the main navigation drawer...
 */
data class DrawerMenuItem(
    val id: String,
    val title: String,
    val contentDescription: String,
    val icon: ImageResource,
    val isSelected: Boolean = false
)