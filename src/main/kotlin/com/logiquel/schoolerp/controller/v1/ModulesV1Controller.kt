package com.logiquel.schoolerp.controller.v1


import com.logiquel.schoolerp.dto.common.ApiResponse
import com.logiquel.schoolerp.dto.v1.CreateModuleRequest
import com.logiquel.schoolerp.dto.v1.ModuleResponse
import com.logiquel.schoolerp.dto.v1.UpdateModuleRequest
import com.logiquel.schoolerp.service.v1.ModulesV1Service
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/api/v1/modules")
class ModulesV1Controller(private val modulesV1Service: ModulesV1Service) {

    // GET /api/v1/modules
    @GetMapping
    fun findAll(): ResponseEntity<ApiResponse<List<ModuleResponse>>> {
        val response = modulesV1Service.findAll()
        return ResponseEntity.status(response.status).body(response)
    }

    // GET /api/v1/modules/{id}
    @GetMapping("/{id}")
    fun findById(@PathVariable id: UUID): ResponseEntity<ApiResponse<ModuleResponse>> {
        val response = modulesV1Service.findById(id)
        return ResponseEntity.status(response.status).body(response)
    }

    // POST /api/v1/modules
    @PostMapping
    fun create(@RequestBody request: CreateModuleRequest): ResponseEntity<ApiResponse<ModuleResponse>> {
        val response = modulesV1Service.create(request)
        return ResponseEntity.status(response.status).body(response)
    }

    // PATCH /api/v1/modules/{id}
    @PutMapping("/{id}")
    fun update(
        @PathVariable id: UUID,
        @RequestBody request: UpdateModuleRequest
    ): ResponseEntity<ApiResponse<ModuleResponse>> {
        val response = modulesV1Service.update(id, request)
        return ResponseEntity.status(response.status).body(response)
    }

    // DELETE /api/v1/modules/{id}
    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: UUID): ResponseEntity<ApiResponse<Nothing>> {
        val response = modulesV1Service.delete(id)
        return ResponseEntity.status(response.status).body(response)
    }
}