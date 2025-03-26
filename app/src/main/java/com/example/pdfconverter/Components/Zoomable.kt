package com.example.pdfconverter.Components

import android.graphics.Bitmap
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize


@Composable
fun ZoomableImageView(bitmap: Bitmap?, onClose: () -> Unit) {
    // Enables pinch-to-zoom and panning
    var scale by remember { mutableFloatStateOf(1f) }
    var offsetX by remember { mutableFloatStateOf(0f) }
    var offsetY by remember { mutableFloatStateOf(0f) }
    val imageSize = remember { mutableStateOf(Size.Zero) }
    val containerSize = remember { mutableStateOf(Size.Zero) }

    val gestureModifier = Modifier.pointerInput(Unit) {
        detectTransformGestures { _, pan, zoom, _ ->
            val newScale = (scale * zoom).coerceIn(1f, 5f)
            scale = newScale

            val scaledWidth = imageSize.value.width * newScale

            val containerWidth = containerSize.value.width
            val containerHeight = containerSize.value.height

            val newOffsetX = offsetX + pan.x * newScale
            val newOffsetY = offsetY + pan.y * newScale

            // Constrain image panning to screen edges
            val maxX = ((scaledWidth - containerWidth).coerceAtLeast(0f)) / 2f
            val maxY = containerHeight.coerceAtLeast(0f) / 2f

            offsetX = newOffsetX.coerceIn(-maxX, maxX)
            offsetY = newOffsetY.coerceIn(-maxY, maxY)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .onGloballyPositioned { containerSize.value = it.size.toSize() }
            .clickable { onClose() }
    ) {
        bitmap?.let {
            Image(
                bitmap = it.asImageBitmap(),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer(
                        scaleX = scale,
                        scaleY = scale,
                        translationX = offsetX,
                        translationY = offsetY
                    )
                    .onGloballyPositioned { imageSize.value = it.size.toSize() }
                    .then(gestureModifier)
            )
        }
        Icon(
            imageVector = Icons.Default.Close,
            contentDescription = "Close",
            modifier = Modifier
                .align(Alignment.TopEnd)
                .size(72.dp)
                .padding(16.dp)
                .clickable { onClose() },
            tint = Color.White
        )

    }

    BackHandler(onBack = onClose)
}