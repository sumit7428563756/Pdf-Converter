package com.example.pdfconverter.Screen

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.os.Environment
import android.provider.DocumentsContract
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.navigation.NavHostController
import com.example.pdfconverter.Components.AppBar


import com.example.pdfconverter.Permissions.checkPermissions
import com.example.pdfconverter.Permissions.requestPermission
import com.example.pdfconverter.R


import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStreamReader


@SuppressLint("ContextCastToActivity")
@Composable
fun Home() {

    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    val activity = (LocalContext.current as? Activity)
    var pdfUri by remember { mutableStateOf<Uri?>(null) }
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) {uri : Uri? ->
        pdfUri = uri
    }


    Box{
        AppBar()
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
           Box(modifier = Modifier
               .height(200.dp)
               .width(200.dp)
               .clickable {
                   if (checkPermissions(context)) {
                       Toast
                           .makeText(context, "Permissions Granted..", Toast.LENGTH_SHORT)
                           .show()
                       launcher.launch("*/*")
                   } else {
                       requestPermission(activity!!)
                   }
               }
               .border(2.dp, color = Color.Black, shape = RoundedCornerShape(8.dp)),
               contentAlignment = Alignment.Center){
                 Icon(painter = painterResource(id = R.drawable.documentation), tint = Color.Unspecified, contentDescription = null,)
             }
            Spacer(modifier = Modifier.height(10.dp))
            Text(text = "Select Files", textAlign = TextAlign.Center, fontSize = 20.sp, fontWeight = FontWeight.W400)
            Spacer(modifier = Modifier.height(20.dp))

            pdfUri?.path?.let {
                File(it)
            }
            Spacer(modifier = Modifier.height(20.dp))

            Button(onClick = {
                if(pdfUri == null){
                    Toast
                        .makeText(context, "Please Select file...", Toast.LENGTH_SHORT)
                        .show()
            }else{
               coroutineScope.launch {
                pdfUri?.let {
                    createPdfFromFile(context,it)
                }
               }
                }
            },
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Black,
                    contentColor = Color.White
                )) {
               Text(text =  "Convert to Pdf", fontSize = 30.sp, fontWeight = FontWeight.W600)
            }
        }
    }
}


 suspend fun createPdfFromFile(context: Context, uri: Uri) {
    val pdfDocument = PdfDocument()
    val pageInfo = PdfDocument.PageInfo.Builder(600, 800, 1).create()
    val page = pdfDocument.startPage(pageInfo)
    val canvas = page.canvas
    val paint = android.graphics.Paint()


    context.contentResolver.openInputStream(uri)?.use { inputStream ->
        val fileType = context.contentResolver.getType(uri)
        if (fileType?.startsWith("image/") == true) {
            val bitmap = BitmapFactory.decodeStream(inputStream)
            val scaledBitmap = Bitmap.createScaledBitmap(bitmap, 500, 700, true)
            canvas.drawBitmap(scaledBitmap, 50f, 50f, paint)
        } else {
            val reader = BufferedReader(InputStreamReader(inputStream))
            var line: String?
            var yPosition = 50f
            while (reader.readLine().also { line = it } != null) {
                canvas.drawText(line!!, 50f, yPosition, paint)
                yPosition += 30f
            }
        }
    }

    pdfDocument.finishPage(page)
    val file = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "Converted.pdf")
    try {
        val fos = FileOutputStream(file)
        pdfDocument.writeTo(fos)
        pdfDocument.close()
        fos.close()
        Toast.makeText(context, "PDF Saved: ${file.absolutePath}", Toast.LENGTH_LONG).show()
    } catch (e: IOException) {
        e.printStackTrace()
        Toast.makeText(context, "Error saving PDF", Toast.LENGTH_LONG).show()
    }
}

