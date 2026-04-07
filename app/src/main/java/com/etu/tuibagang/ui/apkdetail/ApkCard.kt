package com.etu.tuibagang.ui.apkdetail

import android.content.pm.PackageManager
import android.os.Build
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.etu.tuibagang.data.model.ApkItem
import com.etu.tuibagang.ui.theme.TuiBaGangTheme
import com.etu.tuibagang.utils.formatCreatedAt

@Composable
internal fun ApkCard(
    item: ApkItem,
    downloadingToken: String?,
    onInstallApk: (String, String) -> Unit,
    onOpenDetail: (Long) -> Unit
) {
    val context = LocalContext.current
    val installedVersion = remember(item.packageName) {
        try {
            val packageInfo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                context.packageManager.getPackageInfo(
                    item.packageName,
                    PackageManager.PackageInfoFlags.of(0)
                )
            } else {
                @Suppress("DEPRECATION")
                context.packageManager.getPackageInfo(item.packageName, 0)
            }
            packageInfo.versionName
        } catch (_: PackageManager.NameNotFoundException) {
            null
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onOpenDetail(item.id) }
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${item.appName} (${item.versionCode})",
                    style = MaterialTheme.typography.titleSmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    if (item.isLatest) {
                        FilterChip(
                            selected = true,
                            onClick = {},
                            label = { Text("Latest") }
                        )
                    }
                    if (installedVersion != null) {
                        FilterChip(
                            selected = item.versionCode == installedVersion,
                            onClick = {},
                            label = { Text("Installed: $installedVersion") }
                        )
                    }
                }
            }

            Text(
                text = "Created: ${formatCreatedAt(item.createdAt)}",
                style = MaterialTheme.typography.bodySmall
            )

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                if (!item.debugUrl.isNullOrBlank()) {
                    val debugToken = "${item.id}:debug"
                    val isDownloadingDebug = downloadingToken == debugToken
                    Text(
                        text = if (isDownloadingDebug) "Downloading..." else "Debug APK",
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
                        text = if (isDownloadingRelease) "Downloading..." else "Release APK",
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
}

@Preview(showBackground = true)
@Composable
private fun ApkCardPreview() {
    TuiBaGangTheme {
        ApkCard(
            item = ApkItem(
                id = 1,
                appName = "Sample App",
                packageName = "com.example.sample",
                versionCode = "1.0.0",
                createdAt = "2023-10-27T10:00:00Z",
                isLatest = true,
                releaseTitle = "First Release",
                releaseNotes = "Initial version",
                devNote = "Everything works fine",
                debugUrl = "https://example.com/debug.apk",
                releaseUrl = "https://example.com/release.apk"
            ),
            downloadingToken = null,
            onInstallApk = { _, _ -> },
            onOpenDetail = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ApkCardDownloadingPreview() {
    TuiBaGangTheme {
        ApkCard(
            item = ApkItem(
                id = 1,
                appName = "Sample App",
                packageName = "com.example.sample",
                versionCode = "1.0.0",
                createdAt = "2023-10-27T10:00:00Z",
                isLatest = false,
                releaseTitle = null,
                releaseNotes = null,
                devNote = "",
                debugUrl = "https://example.com/debug.apk",
                releaseUrl = null
            ),
            downloadingToken = "1:debug",
            onInstallApk = { _, _ -> },
            onOpenDetail = {}
        )
    }
}
