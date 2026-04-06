package com.etu.tuibagang.ui.apkdetail

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.shrinkHorizontally
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
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.etu.tuibagang.data.model.ApkItem
import com.etu.tuibagang.ui.theme.TuiBaGangTheme

@Composable
@OptIn(ExperimentalMaterial3Api::class)
internal fun VersionsScreenContent(
    apks: List<ApkItem>,
    isRefreshing: Boolean,
    downloadingToken: String?,
    error: String?,
    searchQuery: String = "",
    onSearchQueryChange: (String) -> Unit = {},
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
            var searchExpanded by remember { mutableStateOf(false) }
            val focusRequester = remember { FocusRequester() }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp, bottom = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                AnimatedVisibility(
                    visible = !searchExpanded,
                    exit = shrinkHorizontally()
                ) {
                    Text(
                        text = "Android Version Manager",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                AnimatedVisibility(
                    visible = searchExpanded,
                    enter = expandHorizontally(expandFrom = Alignment.End),
                    modifier = Modifier.weight(1f)
                ) {
                    LaunchedEffect(Unit) { focusRequester.requestFocus() }
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = onSearchQueryChange,
                        modifier = Modifier
                            .fillMaxWidth()
                            .focusRequester(focusRequester),
                        placeholder = { Text("Search...") },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                        trailingIcon = {
                            IconButton(onClick = {
                                onSearchQueryChange("")
                                searchExpanded = false
                            }) {
                                Icon(Icons.Default.Clear, contentDescription = "Close")
                            }
                        },
                        singleLine = true
                    )
                }

                if (!searchExpanded) {
                    IconButton(onClick = { searchExpanded = true }) {
                        Icon(Icons.Default.Search, contentDescription = "Search")
                    }
                }
            }

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
