package com.etu.tuibagang.data.model

data class ApkItem(
    val id: Long,
    val appName: String,
    val packageName: String,
    val versionCode: String,
    val createdAt: String,
    val isLatest: Boolean,
    val releaseTitle: String?,
    val releaseNotes: String?,
    val devNote: String,
    val debugUrl: String?,
    val releaseUrl: String?
)