package com.etu.tuibagang.ui

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.etu.tuibagang.BuildConfig
import com.etu.tuibagang.core.network.NetworkModule
import com.etu.tuibagang.data.local.AppDatabase
import com.etu.tuibagang.data.repository.ApkRepository
import com.etu.tuibagang.data.model.ApkItem
import com.etu.tuibagang.feature.versions.VersionsViewModel
import com.etu.tuibagang.navagation.AppRootContent
import com.etu.tuibagang.ui.theme.TuiBaGangTheme
import com.etu.tuibagang.utils.downloadApkToCache
import com.etu.tuibagang.utils.launchApkInstall
import kotlinx.coroutines.launch

@Composable
fun AppRoot() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var downloadingToken by remember { mutableStateOf<String?>(null) }
    val repository = rememberRepository()
    val viewModel: VersionsViewModel = viewModel(
        factory = VersionsViewModel.factory(repository)
    )

    val apks by viewModel.apks.collectAsStateWithLifecycle()
    val isRefreshing by viewModel.isRefreshing.collectAsStateWithLifecycle()
    val error by viewModel.error.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()

    AppRootContent(
            apks = apks,
            isRefreshing = isRefreshing,
            downloadingToken = downloadingToken,
            error = error,
            searchQuery = searchQuery,
            onSearchQueryChange = viewModel::onSearchQueryChange,
            onRefresh = viewModel::refresh,
            onInstallApk = { url, token ->
                if (downloadingToken != null) return@AppRootContent
                downloadingToken = token
                scope.launch {
                    val apkFile = runCatching {
                        downloadApkToCache(context, url)
                    }.onFailure {
                        Toast.makeText(context, "Download failed: ${it.message}", Toast.LENGTH_LONG)
                            .show()
                    }.getOrNull()

                    downloadingToken = null

                    if (apkFile != null) {
                        launchApkInstall(context, apkFile)
                    }
                }
            })
}

@Composable
private fun rememberRepository(): ApkRepository {
    val context = LocalContext.current
    val db = AppDatabase.getInstance(context)
    val api = NetworkModule.createSupabaseApi(BuildConfig.SUPABASE_URL)
    return ApkRepository(
        api = api,
        dao = db.apkDao(),
        anonKey = BuildConfig.SUPABASE_ANON_KEY
    )
}

@Preview(showBackground = true)
@Composable
fun AppRootPreview() {
    TuiBaGangTheme {
        AppRootContent(
                apks = listOf(
                ApkItem(
                        id = 1,
                        appName = "TuiBaGang",
                        packageName = "com.etu.tuibagang",
                        versionCode = "100",
                        createdAt = "2023-10-27 10:00:00",
                        isLatest = true,
                        releaseTitle = "v1.0.0",
                        releaseNotes = "Initial release",
                        devNote = "Stable version",
                        debugUrl = "https://example.com/debug.apk",
                        releaseUrl = "https://example.com/release.apk"
                ), ApkItem(
                id = 2,
                appName = "TuiBaGang",
                packageName = "com.etu.tuibagang",
                versionCode = "99",
                createdAt = "2023-10-26 10:00:00",
                isLatest = false,
                releaseTitle = "v0.9.9",
                releaseNotes = "Beta release",
                devNote = "Beta version",
                debugUrl = "https://example.com/debug99.apk",
                releaseUrl = null
        )
        ),
                isRefreshing = false,
                downloadingToken = null,
                error = null,
                onRefresh = {},
                onInstallApk = { _, _ -> })
    }
}
