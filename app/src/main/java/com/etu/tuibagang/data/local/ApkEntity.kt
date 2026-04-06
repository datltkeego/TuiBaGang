package com.etu.tuibagang.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.etu.tuibagang.data.model.ApkItem

@Entity(tableName = "apks")
data class ApkEntity(
    @PrimaryKey val id: Long,
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
) {
    fun toModel(): ApkItem {
        return ApkItem(
            id = id,
            appName = appName,
            packageName = packageName,
            versionCode = versionCode,
            createdAt = createdAt,
            isLatest = isLatest,
            releaseTitle = releaseTitle,
            releaseNotes = releaseNotes,
            devNote = devNote,
            debugUrl = debugUrl,
            releaseUrl = releaseUrl
        )
    }
}
