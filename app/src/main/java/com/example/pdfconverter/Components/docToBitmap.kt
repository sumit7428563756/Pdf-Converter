package com.example.pdfconverter.Components

import android.content.Context
import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.os.ParcelFileDescriptor
import androidx.compose.ui.graphics.Canvas
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

suspend fun renderPdfToBitmaps(context: Context,pdfFile : File): List<Bitmap> = withContext(
    Dispatchers.Default) {
    val bitmaps = mutableListOf<Bitmap>()
    var fileDescriptor: ParcelFileDescriptor? = null
    var renderer: PdfRenderer? = null

    try {
        // Open the PDF file
        fileDescriptor = ParcelFileDescriptor.open(pdfFile, ParcelFileDescriptor.MODE_READ_ONLY)
        // Create the PdfRenderer
        renderer = PdfRenderer(fileDescriptor)

        val displayMetrics = context.resources.displayMetrics
        val screenWidth = displayMetrics.widthPixels

        // Loop through each page in the PDF and render it to a bitmap
        for (pageIndex in 0 until renderer.pageCount) {
            renderer.openPage(pageIndex).use { page ->
                val aspectRatio = page.height.toFloat() / page.width.toFloat()
                // Set bitmap width to the phone's screen width
                val bitmapWidth = screenWidth
                val bitmapHeight = (bitmapWidth * aspectRatio).toInt()

                // Create a bitmap with the same aspect ratio as the PDF page
                val bitmap = Bitmap.createBitmap(
                    bitmapWidth,
                    bitmapHeight,
                    Bitmap.Config.ARGB_8888
                )

                android.graphics.Canvas(bitmap).apply {
                    // Give the bitmap a white background, remove this if you want a transparent background
                    drawColor(android.graphics.Color.WHITE)
                    // Render the PDF page to the bitmap
                    page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
                }

                bitmaps.add(bitmap)
            }
        }
    } catch (e: Exception) {
        e.printStackTrace()
        println("Error rendering PDF: ${pdfFile.absolutePath}")
        return@withContext emptyList()
    } finally {
        // Close the renderer and file descriptor
        renderer?.close()
        fileDescriptor?.close()
    }

    return@withContext bitmaps
}