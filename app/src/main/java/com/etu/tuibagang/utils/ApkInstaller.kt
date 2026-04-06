package com.etu.tuibagang.utils

import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.net.HttpURLConnection
import java.net.URL

internal suspend fun downloadApkToCache(context: Context, url: String): File = withContext(Dispatchers.IO) {
    val requestUrl = URL(url)
    val connection = (requestUrl.openConnection() as HttpURLConnection).apply {
        connectTimeout = 15_000
        readTimeout = 60_000
        requestMethod = "GET"
        doInput = true
    }
    connection.connect()

    if (connection.responseCode !in 200..299) {
        connection.disconnect()
        error("HTTP ${connection.responseCode}")
    }

    val fileName = "download_${System.currentTimeMillis()}.apk"
    val apkFile = File(context.cacheDir, fileName)

    connection.inputStream.use { input ->
        apkFile.outputStream().use { output ->
            input.copyTo(output)
        }
    }
    connection.disconnect()
    apkFile
}

internal fun launchApkInstall(context: Context, apkFile: File) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O &&
        !context.packageManager.canRequestPackageInstalls()
    ) {
        val settingsIntent = Intent(
            Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES,
            "package:${context.packageName}".toUri()
        ).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(settingsIntent)
        Toast.makeText(
            context,
            "Allow installs from this app, then tap APK again.",
            Toast.LENGTH_LONG
        ).show()
        return
    }

    val apkUri = FileProvider.getUriForFile(
        context,
        "${context.packageName}.fileprovider",
        apkFile
    )

    val installIntent = Intent(Intent.ACTION_VIEW).apply {
        setDataAndType(apkUri, "application/vnd.android.package-archive")
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }
    context.startActivity(installIntent)
}
