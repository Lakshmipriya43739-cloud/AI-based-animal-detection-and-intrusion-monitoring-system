package com.animalmonitoring.ai;

import com.animalmonitoring.entity.RiskLevel;
import org.springframework.stereotype.Service;

/**
 * Determines the risk level for a detection event.
 *
 * <h3>Rules (in order of precedence)</h3>
 * <ol>
 *   <li>No intrusion → {@code LOW}, regardless of animal type.</li>
 *   <li>Intrusion + {@code LOW}-risk animal → {@code MEDIUM}.</li>
 *   <li>Intrusion + {@code MEDIUM}-risk animal → {@code HIGH}.</li>
 *   <li>Intrusion + {@code HIGH}-risk animal → {@code HIGH}.</li>
 *   <li>Intrusion + {@code CRITICAL}-risk animal → {@code CRITICAL}.</li>
 * </ol>
 *
 * <p>The confidence score is currently used as a guard: if confidence is below
 * 50 % the assessed risk is capped at one level below the computed risk, because
 * a low-confidence detection should not trigger the highest-severity alerts.
 */
@Service
public class RiskAssessmentService {

    /**
     * Compute the final {@link RiskLevel} for a single detection.
     *
     * @param animalDefaultRisk  The animal's default risk from the database.
     * @param intrusionDetected  Whether the bounding-box centre is inside a restricted zone.
     * @param confidence         YOLO confidence score in [0.0, 1.0].
     * @return The assessed risk level.
     */
    public RiskLevel assess(RiskLevel animalDefaultRisk,
                            boolean intrusionDetected,
                            float confidence) {

        if (!intrusionDetected) {
            return RiskLevel.LOW;
        }

        RiskLevel base = computeIntrusionRisk(animalDefaultRisk);
        return applyConfidenceAdjustment(base, confidence);
    }

    // ------------------------------------------------------------------ private
    private RiskLevel computeIntrusionRisk(RiskLevel animalRisk) {
        return switch (animalRisk) {
            case LOW      -> RiskLevel.MEDIUM;
            case MEDIUM   -> RiskLevel.HIGH;
            case HIGH     -> RiskLevel.HIGH;
            case CRITICAL -> RiskLevel.CRITICAL;
        };
    }

    /**
     * Caps risk by one level when confidence is below 50 %.
     * A weak detection should not immediately trigger a CRITICAL alert.
     */
    private RiskLevel applyConfidenceAdjustment(RiskLevel computed, float confidence) {
        if (confidence >= 0.5f) return computed;

        return switch (computed) {
            case CRITICAL -> RiskLevel.HIGH;
            case HIGH     -> RiskLevel.MEDIUM;
            case MEDIUM   -> RiskLevel.LOW;
            case LOW      -> RiskLevel.LOW;
        };
    }
}
