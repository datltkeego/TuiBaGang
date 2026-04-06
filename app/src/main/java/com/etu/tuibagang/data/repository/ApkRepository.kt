package com.etu.tuibagang.data.repository

import com.etu.tuibagang.data.local.ApkDao
import com.etu.tuibagang.data.remote.SupabaseApi
import com.etu.tuibagang.data.model.ApkItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ApkRepository(
    private val api: SupabaseApi,
    private val dao: ApkDao,
    private val anonKey: String
) {
    fun observeApks(): Flow<List<ApkItem>> {
        return dao.observeApks().map { entities ->
            entities.map { it.toModel() }
        }
    }

    suspend fun refresh() {
        val auth = "Bearer $anonKey"
        val remote = api.getApks(
            apiKey = anonKey,
            authorization = auth
        )
        dao.upsertAll(remote.map { it.toEntity() })
    }
}
