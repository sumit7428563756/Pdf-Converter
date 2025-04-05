package com.example.pdfconverter

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.pdfconverter.ui.theme.PdfConverterTheme
import java.io.File
import java.io.FileNotFoundException
import java.io.InputStream

class MainActivity : ComponentActivity() {
    val PICK_PDF_FILE = 2

    val storageDir: String =
        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString() + File.separator
    val outputPDF: String = storageDir + "Converted_PDF.pdf"

    var textView: TextView? = null
    var documentUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            @Composable
            fun PdfViewerScreen() {
                Box(modifier = Modifier.fillMaxSize()) {
                    // FloatingActionButton
                    FloatingActionButton(
                        onClick = {  try {
                            openAndConvertFile()
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }},
                        containerColor = Color(0xFF00BCD4),
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(end = 36.dp, bottom = 140.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add, // Or use any drawable you like
                            contentDescription = "Upload"
                        )
                    }
                }
            }
            }
        }
    fun openAndConvertFile() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "*/*"
            putExtra(
                Intent.EXTRA_MIME_TYPES, arrayOf(
                    "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
                    "application/msword"
                )
            )
        }
        startActivityForResult(intent, PICK_PDF_FILE)
    }

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK && requestCode == PICK_PDF_FILE) {
            data?.data?.let { uri ->
                documentUri = uri
                try {
                    val inputStream: InputStream? = contentResolver.openInputStream(uri)
                    val doc = com.aspose.words.Document(inputStream)
                    doc.save(outputPDF)

                    Toast.makeText(this, "File saved in: $outputPDF", Toast.LENGTH_LONG).show()
                    textView?.text = "PDF saved at: $outputPDF"

                } catch (e: FileNotFoundException) {
                    e.printStackTrace()
                    Toast.makeText(this, "File not found: ${e.message}", Toast.LENGTH_LONG).show()
                } catch (e: Exception) {
                    e.printStackTrace()
                    Toast.makeText(this, e.message, Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}




