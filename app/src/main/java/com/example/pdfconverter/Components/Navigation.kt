package com.example.pdfconverter.Components

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.pdfconverter.Screen.Home
import com.example.pdfconverter.Screen.PdfView

@Composable
fun Navigate(){
     val NavHostController = rememberNavController()
    NavHost(navController = NavHostController, startDestination = start) {
         composable(start){
            Home(navController = NavHostController)
         }
        composable(End){
            PdfView(NavController = NavHostController)
        } 
    }
}

const val start = "Home"
const val End = "ViewScreen"