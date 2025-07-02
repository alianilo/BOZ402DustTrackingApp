package com.example.dusttrackingapp.ui.logs

data class LogsState(
    val logs: List<DustLog> = emptyList(),
    val page: Int = 1,
    val hasNext: Boolean = false,
    val filter: LogsFilter = LogsFilter.ALL,
    val sort: LogsSort = LogsSort.DATE_DESC,
    val isLoading: Boolean = false,
    val error: String? = null
)