package com.logiquel.schoolerp.controller.v1

import com.logiquel.schoolerp.dto.common.ApiResponse
import com.logiquel.schoolerp.dto.v1.CreateTenantRequest
import com.logiquel.schoolerp.dto.v1.TenantResponse
import com.logiquel.schoolerp.dto.v1.UpdateTenantRequest
import com.logiquel.schoolerp.service.v1.TenantsV1Service
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
@RequestMapping("/api/v1/tenants")
class TenantsV1Controller(private val tenantsV1Service: TenantsV1Service) {

    // GET /api/v1/tenants
    @GetMapping
    fun findAll(): ResponseEntity<ApiResponse<List<TenantResponse>>> {
        val response = tenantsV1Service.findAll()
        return ResponseEntity.status(response.status).body(response)
    }

    // GET /api/v1/tenants/{id}
    @GetMapping("/{id}")
    fun findById(@PathVariable id: UUID): ResponseEntity<ApiResponse<TenantResponse>> {
        val response = tenantsV1Service.findById(id)
        return ResponseEntity.status(response.status).body(response)
    }

    // GET /api/v1/tenants/slug/{slug}
    @GetMapping("/slug/{slug}")
    fun findBySlug(@PathVariable slug: String): ResponseEntity<ApiResponse<TenantResponse>> {
        val response = tenantsV1Service.findBySlug(slug)
        return ResponseEntity.status(response.status).body(response)
    }

    // POST /api/v1/tenants
    @PostMapping
    fun create(@RequestBody request: CreateTenantRequest): ResponseEntity<ApiResponse<TenantResponse>> {
        val response = tenantsV1Service.create(request)
        return ResponseEntity.status(response.status).body(response)
    }

    // PATCH /api/v1/tenants/{id}
    @PutMapping("/{id}")
    fun update(
        @PathVariable id: UUID,
        @RequestBody request: UpdateTenantRequest
    ): ResponseEntity<ApiResponse<TenantResponse>> {
        val response = tenantsV1Service.update(id, request)
        return ResponseEntity.status(response.status).body(response)
    }

    // DELETE /api/v1/tenants/{id}
    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: UUID): ResponseEntity<ApiResponse<Nothing>> {
        val response = tenantsV1Service.delete(id)
        return ResponseEntity.status(response.status).body(response)
    }
}