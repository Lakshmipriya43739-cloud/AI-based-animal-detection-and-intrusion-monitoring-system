package com.animalmonitoring.controller;

import com.animalmonitoring.dto.request.ZoneRequest;
import com.animalmonitoring.dto.response.ApiResponse;
import com.animalmonitoring.dto.response.ZoneResponse;
import com.animalmonitoring.service.ZoneService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/zones")
public class ZoneController {

    private final ZoneService zoneService;

    public ZoneController(ZoneService zoneService) {
        this.zoneService = zoneService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ZoneResponse>>> getAllZones() {
        return ResponseEntity.ok(ApiResponse.success(zoneService.getAllZones()));
    }

    @GetMapping("/active")
    public ResponseEntity<ApiResponse<List<ZoneResponse>>> getActiveZones() {
        return ResponseEntity.ok(ApiResponse.success(zoneService.getActiveZones()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ZoneResponse>> getZoneById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(zoneService.getZoneById(id)));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ZoneResponse>> createZone(
            @Valid @RequestBody ZoneRequest request) {
        ZoneResponse created = zoneService.createZone(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Zone created successfully", created));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ZoneResponse>> updateZone(
            @PathVariable Long id,
            @Valid @RequestBody ZoneRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Zone updated successfully",
                zoneService.updateZone(id, request)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteZone(@PathVariable Long id) {
        zoneService.deleteZone(id);
        return ResponseEntity.ok(ApiResponse.success("Zone deleted successfully", null));
    }
}
