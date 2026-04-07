package com.etu.tuibagang.feature.versions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.etu.tuibagang.data.repository.ApkRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class SortDirection { NONE, ASC, DESC }

class VersionsViewModel(
    private val repository: ApkRepository
) : ViewModel() {
    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _sortByDate = MutableStateFlow(SortDirection.NONE)
    val sortByDate: StateFlow<SortDirection> = _sortByDate.asStateFlow()

    private val _sortByName = MutableStateFlow(SortDirection.NONE)
    val sortByName: StateFlow<SortDirection> = _sortByName.asStateFlow()

    private val allApks = repository.observeApks().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList()
    )

    val apks = combine(allApks, _searchQuery, _sortByDate, _sortByName) { list, query, dateSort, nameSort ->
        val filtered = if (query.isBlank()) list
        else list.filter { apk ->
            apk.appName.contains(query, ignoreCase = true) ||
                    apk.packageName.contains(query, ignoreCase = true) ||
                    apk.versionCode.contains(query, ignoreCase = true)
        }
        when {
            dateSort == SortDirection.ASC -> filtered.sortedBy { it.createdAt }
            dateSort == SortDirection.DESC -> filtered.sortedByDescending { it.createdAt }
            nameSort == SortDirection.ASC -> filtered.sortedBy { it.appName.lowercase() }
            nameSort == SortDirection.DESC -> filtered.sortedByDescending { it.appName.lowercase() }
            else -> filtered
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList()
    )

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun toggleSortByDate() {
        _sortByName.value = SortDirection.NONE
        _sortByDate.value = _sortByDate.value.next()
    }

    fun toggleSortByName() {
        _sortByDate.value = SortDirection.NONE
        _sortByName.value = _sortByName.value.next()
    }

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            _isRefreshing.value = true
            _error.value = null
            runCatching { repository.refresh() }
                .onFailure { throwable ->
                    _error.value = throwable.message ?: "Failed to refresh data"
                }
            _isRefreshing.value = false
        }
    }

    companion object {
        fun factory(repository: ApkRepository): ViewModelProvider.Factory {
            return object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return VersionsViewModel(repository) as T
                }
            }
        }
    }
}

private fun SortDirection.next(): SortDirection = when (this) {
    SortDirection.NONE -> SortDirection.ASC
    SortDirection.ASC -> SortDirection.DESC
    SortDirection.DESC -> SortDirection.NONE
}
