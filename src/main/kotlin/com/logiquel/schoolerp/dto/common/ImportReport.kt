package com.logiquel.schoolerp.dto.common

data class ImportReport(
    val totalRows: Int,
    val successCount: Int,
    val skippedCount: Int,
    val errors: List<RowError>
)

data class RowError(
    val row: Int,
    val reason: String
)