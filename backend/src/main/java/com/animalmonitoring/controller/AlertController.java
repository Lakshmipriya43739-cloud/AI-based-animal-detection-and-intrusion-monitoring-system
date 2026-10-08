package com.animalmonitoring.controller;

import com.animalmonitoring.dto.response.AlertResponse;
import com.animalmonitoring.dto.response.ApiResponse;
import com.animalmonitoring.entity.AlertStatus;
import com.animalmonitoring.service.AlertService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/alerts")
public class AlertController {

    private final AlertService alertService;

    public AlertController(AlertService alertService) {
        this.alertService = alertService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<AlertResponse>>> getAllAlerts() {
        return ResponseEntity.ok(ApiResponse.success(alertService.getAllAlerts()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<AlertResponse>> getAlertById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(alertService.getAlertById(id)));
    }

    @GetMapping("/recipient/{recipientId}")
    public ResponseEntity<ApiResponse<List<AlertResponse>>> getAlertsByRecipient(
            @PathVariable Long recipientId) {
        return ResponseEntity.ok(ApiResponse.success(
                alertService.getAlertsByRecipient(recipientId)));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<ApiResponse<List<AlertResponse>>> getAlertsByStatus(
            @PathVariable AlertStatus status) {
        return ResponseEntity.ok(ApiResponse.success(alertService.getAlertsByStatus(status)));
    }

    @PutMapping("/{id}/acknowledge")
    public ResponseEntity<ApiResponse<AlertResponse>> acknowledgeAlert(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success("Alert acknowledged",
                alertService.acknowledgeAlert(id)));
    }
}
