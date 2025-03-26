package com.example.pdfconverter.Components

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap


@Composable
fun PdfPageImage(bitmap: Bitmap, onClick: (Bitmap) -> Unit) {
    // Displays a single page thumbnail
    Box(
        modifier = Modifier.fillMaxWidth().clickable { onClick(bitmap) }
    ) {
        Image(
            bitmap = bitmap.asImageBitmap(),
            contentDescription = null,
            modifier = Modifier.fillMaxWidth()
        )
    }
}