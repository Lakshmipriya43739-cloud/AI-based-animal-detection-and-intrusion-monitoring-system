package com.animalmonitoring.service;

import com.animalmonitoring.dto.response.AlertResponse;
import com.animalmonitoring.entity.Alert;
import com.animalmonitoring.entity.AlertStatus;
import com.animalmonitoring.exception.ResourceNotFoundException;
import com.animalmonitoring.repository.AlertRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AlertService {

    private static final Logger log = LoggerFactory.getLogger(AlertService.class);

    private final AlertRepository alertRepository;

    public AlertService(AlertRepository alertRepository) {
        this.alertRepository = alertRepository;
    }

    @Transactional(readOnly = true)
    public List<AlertResponse> getAllAlerts() {
        return alertRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public AlertResponse getAlertById(Long id) {
        return mapToResponse(findById(id));
    }

    @Transactional(readOnly = true)
    public List<AlertResponse> getAlertsByRecipient(Long recipientId) {
        return alertRepository.findByRecipientId(recipientId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<AlertResponse> getAlertsByStatus(AlertStatus status) {
        return alertRepository.findByStatus(status).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public AlertResponse acknowledgeAlert(Long id) {
        Alert alert = findById(id);
        alert.setStatus(AlertStatus.ACKNOWLEDGED);
        Alert saved = alertRepository.save(alert);
        log.info("Alert id {} acknowledged", id);
        return mapToResponse(saved);
    }

    private Alert findById(Long id) {
        return alertRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Alert", "id", id));
    }

    public AlertResponse mapToResponse(Alert alert) {
        return AlertResponse.builder()
                .id(alert.getId())
                .detectionId(alert.getDetection().getId())
                .recipientId(alert.getRecipient().getId())
                .recipientName(alert.getRecipient().getName())
                .alertType(alert.getAlertType())
                .status(alert.getStatus())
                .message(alert.getMessage())
                .sentAt(alert.getSentAt())
                .build();
    }
}
