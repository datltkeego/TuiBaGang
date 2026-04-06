package com.etu.tuibagang.ui.apkdetail

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.etu.tuibagang.data.model.ApkItem
import com.etu.tuibagang.ui.theme.TuiBaGangTheme
import com.etu.tuibagang.utils.formatCreatedAt

@Composable
@OptIn(ExperimentalMaterial3Api::class)
internal fun VersionsScreenContent(
    apks: List<ApkItem>,
    isRefreshing: Boolean,
    downloadingToken: String?,
    error: String?,
    onRefresh: () -> Unit,
    onInstallApk: (String, String) -> Unit,
    onOpenDetail: (Long) -> Unit
) {
    PullToRefreshBox(
        isRefreshing = isRefreshing,
        onRefresh = onRefresh,
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            Text(
                text = "Android Version Manager",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(top = 12.dp, bottom = 8.dp)
            )

            if (isRefreshing && apks.isEmpty()) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator()
                }
            } else if (error != null && apks.isEmpty()) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Load data failed")
                    Text(
                        text = error,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                    TextButton(onClick = onRefresh) {
                        Text("Try again")
                    }
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(bottom = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(apks, key = { it.id }) { item ->
                        ApkCard(
                            item = item,
                            downloadingToken = downloadingToken,
                            onInstallApk = onInstallApk,
                            onOpenDetail = onOpenDetail
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ApkCard(
    item: ApkItem,
    downloadingToken: String?,
    onInstallApk: (String, String) -> Unit,
    onOpenDetail: (Long) -> Unit
) {
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
                if (item.isLatest) {
                    FilterChip(
                        selected = true,
                        onClick = {},
                        label = { Text("Latest") }
                    )
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

@Composable
internal fun PlaceholderScreen(title: String) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "$title tab (temporary empty)")
    }
}

@Preview(showBackground = true)
@Composable
private fun VersionsScreenContentPreview() {
    val sampleApks = listOf(
        ApkItem(
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
        ApkItem(
            id = 2,
            appName = "Sample App",
            packageName = "com.example.sample",
            versionCode = "0.9.0",
            createdAt = "2023-10-20T10:00:00Z",
            isLatest = false,
            releaseTitle = "Beta Release",
            releaseNotes = "Beta version",
            devNote = "Some bugs may exist",
            debugUrl = "https://example.com/debug_beta.apk",
            releaseUrl = null
        )
    )
    TuiBaGangTheme {
        VersionsScreenContent(
            apks = sampleApks,
            isRefreshing = false,
            downloadingToken = null,
            error = null,
            onRefresh = {},
            onInstallApk = { _, _ -> },
            onOpenDetail = {}
        )
    }
}
