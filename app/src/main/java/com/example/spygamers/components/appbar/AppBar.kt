package com.example.spygamers.components.appbar

import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import com.example.spygamers.R

@Composable
fun AppBar(
    onNavigationIconClick: () -> Unit = {},
    navigationIconImage: ImageVector = Icons.Default.Menu,
    navigationIconDescription: String = "No Description Set",
    appBarTitle: String = stringResource(id = R.string.app_name)
) {
    TopAppBar(
        title = {
            Text(text = appBarTitle)
        },
        backgroundColor = MaterialTheme.colors.primary,
        contentColor = MaterialTheme.colors.onPrimary,
        navigationIcon = {
            IconButton(onClick = onNavigationIconClick) {
                Icon(
                    imageVector = navigationIconImage,
                    contentDescription = navigationIconDescription
                )
            }
        }
    )
}