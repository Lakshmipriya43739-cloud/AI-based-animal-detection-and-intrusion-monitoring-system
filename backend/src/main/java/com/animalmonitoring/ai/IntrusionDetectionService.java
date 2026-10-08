package com.animalmonitoring.ai;

import com.animalmonitoring.entity.RestrictedZone;
import com.animalmonitoring.repository.RestrictedZoneRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Determines whether a detected animal has entered a restricted zone.
 *
 * <h3>Zone boundary format</h3>
 * Each {@link RestrictedZone} stores boundary geometry in its {@code boundaryData} field.
 * Two formats are supported:
 *
 * <ol>
 *   <li><b>GeoJSON Polygon</b> (preferred):
 *   <pre>{"type":"Polygon","coordinates":[[[x1,y1],[x2,y2],[x3,y3],[x1,y1]]]}</pre>
 *   </li>
 *   <li><b>Simple CSV</b> (fallback): comma-separated {@code x1,y1,x2,y2,...,xn,yn}</li>
 * </ol>
 *
 * <h3>Point-in-polygon algorithm</h3>
 * A ray-casting algorithm is used.  The test point is the <em>centre</em> of the
 * detected bounding box.  The choice of using the centre (rather than any corner)
 * is conservative — it fires the alert only when the animal's body is substantially
 * inside the zone.
 */
@Service
public class IntrusionDetectionService {

    private static final Logger log = LoggerFactory.getLogger(IntrusionDetectionService.class);

    private final RestrictedZoneRepository zoneRepository;
    private final ObjectMapper objectMapper;

    public IntrusionDetectionService(RestrictedZoneRepository zoneRepository,
                                     ObjectMapper objectMapper) {
        this.zoneRepository = zoneRepository;
        this.objectMapper   = objectMapper;
    }

    /**
     * Check whether the centre of the detection's bounding box falls inside
     * any active restricted zone.
     *
     * @param detection  The animal detection to test.
     * @return The first matching {@link RestrictedZone}, or empty if none matched.
     */
    public Optional<RestrictedZone> findIntrudedZone(DetectionResult detection) {
        float cx = detection.getBoundingBox().getCenterX();
        float cy = detection.getBoundingBox().getCenterY();

        List<RestrictedZone> activeZones = zoneRepository.findByActive(true);
        for (RestrictedZone zone : activeZones) {
            if (isPointInZone(cx, cy, zone)) {
                log.debug("Intrusion detected: animal '{}' at ({},{}) is inside zone '{}'",
                        detection.getAnimalName(), cx, cy, zone.getName());
                return Optional.of(zone);
            }
        }
        return Optional.empty();
    }

    // ------------------------------------------------------------------ geometry
    /**
     * Test whether the point (px, py) is inside the polygon defined by the zone's
     * {@code boundaryData}.  Returns {@code false} for zones with no boundary data.
     */
    boolean isPointInZone(float px, float py, RestrictedZone zone) {
        String boundaryData = zone.getBoundaryData();
        if (boundaryData == null || boundaryData.isBlank()) {
            return false;
        }

        float[][] polygon = parseBoundary(boundaryData.trim());
        if (polygon == null || polygon.length < 3) {
            return false;
        }

        return raycastPointInPolygon(px, py, polygon);
    }

    /**
     * Parse boundary data into a polygon array of [x, y] pairs.
     * Tries GeoJSON first, then falls back to CSV.
     */
    private float[][] parseBoundary(String data) {
        if (data.startsWith("{") || data.startsWith("[")) {
            return parseGeoJsonPolygon(data);
        }
        return parseCsvPolygon(data);
    }

    /**
     * Parse a GeoJSON Polygon.
     * Expected format: {@code {"type":"Polygon","coordinates":[[[x1,y1],[x2,y2],...]]}}
     */
    private float[][] parseGeoJsonPolygon(String json) {
        try {
            JsonNode root  = objectMapper.readTree(json);
            JsonNode rings = root.path("coordinates");
            if (rings.isMissingNode() || !rings.isArray() || rings.isEmpty()) return null;

            JsonNode ring = rings.get(0); // outer ring
            float[][] pts = new float[ring.size()][2];
            for (int i = 0; i < ring.size(); i++) {
                pts[i][0] = (float) ring.get(i).get(0).asDouble();
                pts[i][1] = (float) ring.get(i).get(1).asDouble();
            }
            return pts;
        } catch (Exception e) {
            log.warn("GeoJSON boundary parse error: {}", e.getMessage());
            return null;
        }
    }

    /**
     * Parse a CSV boundary string: {@code x1,y1,x2,y2,...,xn,yn}.
     */
    private float[][] parseCsvPolygon(String csv) {
        try {
            String[] parts = csv.split(",");
            if (parts.length < 6 || parts.length % 2 != 0) return null;
            float[][] pts = new float[parts.length / 2][2];
            for (int i = 0; i < parts.length; i += 2) {
                pts[i / 2][0] = Float.parseFloat(parts[i].trim());
                pts[i / 2][1] = Float.parseFloat(parts[i + 1].trim());
            }
            return pts;
        } catch (NumberFormatException e) {
            log.warn("CSV boundary parse error: {}", e.getMessage());
            return null;
        }
    }

    /**
     * Ray-casting point-in-polygon test.
     *
     * <p>Casts a horizontal ray from (px, py) to +∞ and counts how many
     * polygon edges it crosses.  An odd count means the point is inside.
     *
     * @param px      Test point X.
     * @param py      Test point Y.
     * @param polygon Array of [x, y] pairs forming the polygon vertices.
     */
    boolean raycastPointInPolygon(float px, float py, float[][] polygon) {
        int n = polygon.length;
        boolean inside = false;

        for (int i = 0, j = n - 1; i < n; j = i++) {
            float xi = polygon[i][0], yi = polygon[i][1];
            float xj = polygon[j][0], yj = polygon[j][1];

            boolean intersects = ((yi > py) != (yj > py))
                    && (px < (xj - xi) * (py - yi) / (yj - yi) + xi);
            if (intersects) inside = !inside;
        }

        return inside;
    }
}
