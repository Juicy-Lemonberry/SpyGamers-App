package com.example.spygamers.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.spygamers.models.DrawerMenuItem
import com.example.spygamers.utils.ImageResource

@Composable
fun DrawerBody(
    items: List<DrawerMenuItem>,
    modifier: Modifier = Modifier,
    itemTextStyle: TextStyle = TextStyle(fontSize = 18.sp),
    onItemClick: (DrawerMenuItem) -> Unit
) {
    LazyColumn(modifier) {
        items(items) { item ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        onItemClick(item)
                    }
                    .padding(16.dp)
            ) {
                RenderIcon(item)
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = item.title,
                    style = itemTextStyle,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun RenderIcon(item: DrawerMenuItem) {
    val iconSize = 24.dp
    when (val icon = item.icon) {
        is ImageResource.Vector -> {
            Icon(
                imageVector = icon.imageVector,
                contentDescription = item.contentDescription,
                modifier = Modifier.size(iconSize)
            )
        }
        is ImageResource.Drawable -> {
            Icon(
                painter = painterResource(id = icon.resourceId),
                contentDescription = item.contentDescription,
                modifier = Modifier.size(iconSize)
            )
        }
    }
}