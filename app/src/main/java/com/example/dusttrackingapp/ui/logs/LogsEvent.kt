package com.example.dusttrackingapp.ui.logs

sealed class LogsEvent {
    data class SelectFilter(val filter: LogsFilter) : LogsEvent()
    data class SelectSort(val sort: LogsSort)       : LogsEvent()
    data class SelectPage(val page: Int)            : LogsEvent()
    object Refresh                                  : LogsEvent()
}