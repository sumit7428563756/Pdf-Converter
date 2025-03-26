package com.example.pdfconverter.Screen

import android.graphics.Bitmap
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import androidx.navigation.NavHostController
import com.example.pdfconverter.Components.PdfPageImage
import com.example.pdfconverter.Components.ZoomableImageView
import com.example.pdfconverter.Components.renderPdfToBitmaps
import java.io.File

@Composable
fun PdfView(NavController : NavHostController) {
    val context = LocalContext.current
    var pdfFile by remember { mutableStateOf<File?>(null) }
    var bitmaps by remember { mutableStateOf<List<Bitmap>>(emptyList()) }
    var selectedBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    val listState = rememberLazyListState()
    val result = remember { mutableStateOf<Uri?>(null) }

    LaunchedEffect(result?.value?.path) {
        isLoading = true
        pdfFile =  result?.value?.path?.let {
          File(it)
        }
        bitmaps = pdfFile?.let { renderPdfToBitmaps(context, it) } ?: emptyList()
        isLoading = false
    }

    DisposableEffect(pdfFile) {
        onDispose {
            pdfFile?.delete()
        }
    }


    when {
        isLoading -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    color = Color.White,
                    strokeWidth = 4.dp
                )
            }
        }

        selectedBitmap != null -> {
            ZoomableImageView(
                bitmap = selectedBitmap,
                onClose = { selectedBitmap = null }
            )
        }

        bitmaps.isEmpty() -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Error loading PDF",
                    color = Color.White,
                )
            }
        }

        else -> {
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black)
            ) {
                items(bitmaps.size) { index ->
                    PdfPageImage(bitmap = bitmaps[index]) {
                        selectedBitmap = it
                    }
                }
            }
        }
    }
}
