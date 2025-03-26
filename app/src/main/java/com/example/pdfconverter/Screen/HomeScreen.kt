package com.example.pdfconverter.Screen

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.DocumentsContract
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
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
import com.example.pdfconverter.Components.End

import com.example.pdfconverter.Permissions.checkPermissions
import com.example.pdfconverter.Permissions.requestPermission
import com.example.pdfconverter.R
import java.io.File

@SuppressLint("ContextCastToActivity")
@Composable
fun Home(navController : NavHostController) {

    val context = LocalContext.current
    val activity = (LocalContext.current as? Activity)
    val result = remember { mutableStateOf<Uri?>(null) }
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.OpenMultipleDocuments()) {
        result.value = it[0]
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
                       launcher.launch(arrayOf("*/*"))
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

           result?.value?.path?.let {
               Text(text = File(it).name)
           }

            Button(onClick = { navController.navigate(End) },
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Black,
                    contentColor = Color.White
                )) {
               Text(text =  "View Document", fontSize = 30.sp, fontWeight = FontWeight.W600)
            }
        }
    }
}
