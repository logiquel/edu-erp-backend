package com.logiquel.schoolerp.controller.v1


import com.logiquel.schoolerp.dto.common.ApiResponse
import com.logiquel.schoolerp.dto.common.ImportReport
import com.logiquel.schoolerp.service.v1.SampleStudentService
import io.swagger.v3.oas.annotations.Operation
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.awt.PageAttributes

@RestController
@RequestMapping("/api/v1/sample-students")
class SampleStudentController(
    private val service: SampleStudentService
) {

    @Operation(summary = "Import students from Excel")
    @PostMapping("/import", consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun importStudents(
        @RequestParam("file") file: MultipartFile
    ): ResponseEntity<ApiResponse<ImportReport>> {

        if (file.isEmpty) {
            return ResponseEntity.badRequest().body(
                ApiResponse(
                    success = false,
                    status  = 400,
                    message = "File is empty"
                )
            )
        }

        if (!file.originalFilename!!.endsWith(".xlsx")) {
            return ResponseEntity.badRequest().body(
                ApiResponse(
                    success = false,
                    status  = 400,
                    message = "Only .xlsx files are supported"
                )
            )
        }

        val report = service.importFromExcel(file).get()

        return ResponseEntity.ok(
            ApiResponse(
                success = true,
                status  = 200,
                message = "Import completed",
                data    = report
            )
        )
    }
}