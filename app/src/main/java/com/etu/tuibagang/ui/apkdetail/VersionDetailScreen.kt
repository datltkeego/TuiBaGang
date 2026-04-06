package com.etu.tuibagang.ui.apkdetail

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.etu.tuibagang.data.model.ApkItem
import com.etu.tuibagang.ui.theme.TuiBaGangTheme
import com.etu.tuibagang.utils.formatCreatedAt

@Composable
internal fun VersionDetailScreen(
    item: ApkItem?,
    downloadingToken: String?,
    onBack: () -> Unit,
    onInstallApk: (String, String) -> Unit
) {
    if (item == null) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("App not found")
            TextButton(onClick = onBack) {
                Text("Back")
            }
        }
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back")
            }
            Text(
                text = "App Detail",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
        }

        Text(
            text = "${item.appName} (${item.versionCode})",
            style = MaterialTheme.typography.titleSmall
        )
        Text(text = "Package: ${item.packageName}", style = MaterialTheme.typography.bodySmall)
        Text(text = "Created: ${formatCreatedAt(item.createdAt)}", style = MaterialTheme.typography.bodySmall)
        Text(text = "Latest: ${if (item.isLatest) "Yes" else "No"}", style = MaterialTheme.typography.bodySmall)
        Text(text = "Release title: ${item.releaseTitle ?: "-"}", style = MaterialTheme.typography.bodySmall)
        Text(text = "Release notes: ${item.releaseNotes ?: "-"}", style = MaterialTheme.typography.bodySmall)
        Text(
            text = "Dev note: ${if (item.devNote.isBlank()) "-" else item.devNote}",
            style = MaterialTheme.typography.bodySmall
        )
        Text(text = "Debug URL: ${item.debugUrl ?: "-"}", style = MaterialTheme.typography.bodySmall)
        Text(text = "Release URL: ${item.releaseUrl ?: "-"}", style = MaterialTheme.typography.bodySmall)

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            if (!item.debugUrl.isNullOrBlank()) {
                val debugToken = "${item.id}:debug"
                val isDownloadingDebug = downloadingToken == debugToken
                Text(
                    text = if (isDownloadingDebug) "Downloading..." else "Install Debug APK",
                    color = MaterialTheme.colorScheme.primary,
                    modifier = if (isDownloadingDebug) {
                        Modifier
                    } else {
                        Modifier.clickable { onInstallApk(item.debugUrl, debugToken) }
                    }
                )
            }
            if (!item.releaseUrl.isNullOrBlank()) {
                val releaseToken = "${item.id}:release"
                val isDownloadingRelease = downloadingToken == releaseToken
                Text(
                    text = if (isDownloadingRelease) "Downloading..." else "Install Release APK",
                    color = MaterialTheme.colorScheme.primary,
                    modifier = if (isDownloadingRelease) {
                        Modifier
                    } else {
                        Modifier.clickable { onInstallApk(item.releaseUrl, releaseToken) }
                    }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun VersionDetailScreenPreview() {
    TuiBaGangTheme {
        VersionDetailScreen(
            item = ApkItem(
                id = 1,
                appName = "Sample App",
                packageName = "com.example.sample",
                versionCode = "1.0.0",
                createdAt = "2023-10-27T10:00:00Z",
                isLatest = true,
                releaseTitle = "First Release",
                releaseNotes = "Initial version with all features",
                devNote = "Everything works fine",
                debugUrl = "https://example.com/debug.apk",
                releaseUrl = "https://example.com/release.apk"
            ),
            downloadingToken = null,
            onBack = {},
            onInstallApk = { _, _ -> }
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun VersionDetailScreenNullPreview() {
    TuiBaGangTheme {
        VersionDetailScreen(
            item = null,
            downloadingToken = null,
            onBack = {},
            onInstallApk = { _, _ -> }
        )
    }
}
