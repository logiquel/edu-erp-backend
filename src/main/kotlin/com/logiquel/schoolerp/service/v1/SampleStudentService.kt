package com.logiquel.schoolerp.service.v1

import com.logiquel.schoolerp.dto.common.ImportReport
import com.logiquel.schoolerp.dto.common.RowError
import com.logiquel.schoolerp.entities.SampleStudentEntity
import com.logiquel.schoolerp.repo.SampleStudentRepository
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.util.concurrent.CompletableFuture

@Service
class SampleStudentService(
    private val repository: SampleStudentRepository
) {

    @Async("importExecutor")
    fun importFromExcel(file: MultipartFile): CompletableFuture<ImportReport> {

        val errors = mutableListOf<RowError>()
        var successCount = 0
        var totalRows = 0

        val workbook = XSSFWorkbook(file.inputStream)
        val sheet = workbook.getSheetAt(0)

        // skip header row, collect all data rows
        val dataRows = sheet.drop(1).filter { row ->
            row.getCell(0) != null
        }

        totalRows = dataRows.size

        // process in chunks of 100
        dataRows.chunked(100).forEach { chunk ->
            val toSave = mutableListOf<SampleStudentEntity>()

            chunk.forEach { row ->
                val rowNum = row.rowNum + 1 // human readable row number

                try {
                    val firstName  = row.getCell(0)?.stringCellValue?.trim() ?: ""
                    val lastName   = row.getCell(1)?.stringCellValue?.trim() ?: ""
                    val email      = row.getCell(2)?.stringCellValue?.trim() ?: ""
                    val rollNumber = row.getCell(3)?.stringCellValue?.trim() ?: ""
                    val className  = row.getCell(4)?.stringCellValue?.trim() ?: ""

                    // validation
                    when {
                        firstName.isEmpty() -> {
                            errors.add(RowError(rowNum, "First name is missing"))
                            return@forEach
                        }
                        lastName.isEmpty() -> {
                            errors.add(RowError(rowNum, "Last name is missing"))
                            return@forEach
                        }
                        email.isEmpty() -> {
                            errors.add(RowError(rowNum, "Email is missing"))
                            return@forEach
                        }
                        rollNumber.isEmpty() -> {
                            errors.add(RowError(rowNum, "Roll number is missing"))
                            return@forEach
                        }
                        className.isEmpty() -> {
                            errors.add(RowError(rowNum, "Class name is missing"))
                            return@forEach
                        }
                        repository.existsByEmail(email) -> {
                            errors.add(RowError(rowNum, "Email already exists: $email"))
                            return@forEach
                        }
                        repository.existsByRollNumber(rollNumber) -> {
                            errors.add(RowError(rowNum, "Roll number already exists: $rollNumber"))
                            return@forEach
                        }
                    }

                    toSave.add(
                        SampleStudentEntity(
                            firstName  = firstName,
                            lastName   = lastName,
                            email      = email,
                            rollNumber = rollNumber,
                            className  = className
                        )
                    )

                } catch (e: Exception) {
                    errors.add(RowError(rowNum, "Unexpected error: ${e.message}"))
                }
            }

            // save entire chunk at once
            if (toSave.isNotEmpty()) {
                repository.saveAll(toSave)
                successCount += toSave.size
            }
        }

        workbook.close()

        return CompletableFuture.completedFuture(
            ImportReport(
                totalRows = totalRows,
                successCount = successCount,
                skippedCount = errors.size,
                errors = errors
            )
        )
    }
}