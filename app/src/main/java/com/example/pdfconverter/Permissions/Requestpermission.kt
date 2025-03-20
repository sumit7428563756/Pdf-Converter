package com.example.pdfconverter.Permissions

import android.Manifest
import android.app.Activity
import androidx.core.app.ActivityCompat



fun requestPermission(activity: Activity) {
    ActivityCompat.requestPermissions(
        activity,
        arrayOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ),5
    )
}