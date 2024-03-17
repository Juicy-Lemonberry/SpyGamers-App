package com.example.spygamers.utils

import androidx.compose.ui.graphics.vector.ImageVector

sealed class ImageResource {
    data class Vector(val imageVector: ImageVector) : ImageResource()
    data class Drawable(val resourceId: Int) : ImageResource()
}