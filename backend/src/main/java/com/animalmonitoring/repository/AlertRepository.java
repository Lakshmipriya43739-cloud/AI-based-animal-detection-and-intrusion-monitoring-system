package com.animalmonitoring.repository;

import com.animalmonitoring.entity.Alert;
import com.animalmonitoring.entity.AlertStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AlertRepository extends JpaRepository<Alert, Long> {

    List<Alert> findByRecipientId(Long recipientId);

    List<Alert> findByStatus(AlertStatus status);

    List<Alert> findByDetectionId(Long detectionId);

    List<Alert> findByRecipientIdAndStatus(Long recipientId, AlertStatus status);
}
