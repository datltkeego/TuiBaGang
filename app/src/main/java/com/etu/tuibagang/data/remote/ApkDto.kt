package com.etu.tuibagang.data.remote

import com.etu.tuibagang.data.local.ApkEntity
import com.google.gson.annotations.SerializedName

data class ApkDto(
    @SerializedName("id") val id: Long,
    @SerializedName("app_name") val appName: String?,
    @SerializedName("package_name") val packageName: String?,
    @SerializedName("version_code") val versionCode: String?,
    @SerializedName("created_at") val createdAt: String?,
    @SerializedName("is_latest") val isLatest: Boolean?,
    @SerializedName("release_title") val releaseTitle: String?,
    @SerializedName("release_notes") val releaseNotes: String?,
    @SerializedName("dev_note") val devNote: String?,
    @SerializedName("debug_url") val debugUrl: String?,
    @SerializedName("release_url") val releaseUrl: String?
) {
    fun toEntity(): ApkEntity {
        return ApkEntity(
            id = id,
            appName = appName.orEmpty(),
            packageName = packageName.orEmpty(),
            versionCode = versionCode.orEmpty(),
            createdAt = createdAt.orEmpty(),
            isLatest = isLatest ?: false,
            releaseTitle = releaseTitle,
            releaseNotes = releaseNotes,
            devNote = devNote.orEmpty(),
            debugUrl = debugUrl,
            releaseUrl = releaseUrl
        )
    }
}
