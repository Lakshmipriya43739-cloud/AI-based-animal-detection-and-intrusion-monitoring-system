package com.animalmonitoring.service;

import com.animalmonitoring.dto.request.ZoneRequest;
import com.animalmonitoring.dto.response.ZoneResponse;
import com.animalmonitoring.entity.RestrictedZone;
import com.animalmonitoring.exception.DuplicateResourceException;
import com.animalmonitoring.exception.ResourceNotFoundException;
import com.animalmonitoring.repository.RestrictedZoneRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ZoneService unit tests")
class ZoneServiceTest {

    @Mock
    private RestrictedZoneRepository zoneRepository;

    @InjectMocks
    private ZoneService zoneService;

    @Test
    @DisplayName("createZone: success")
    void createZone_success() {
        ZoneRequest req = new ZoneRequest();
        req.setName("North Perimeter");
        req.setLocation("North Farm");
        req.setActive(true);

        when(zoneRepository.existsByNameIgnoreCase("North Perimeter")).thenReturn(false);

        RestrictedZone saved = RestrictedZone.builder().id(1L).name("North Perimeter")
                .location("North Farm").active(true).build();
        when(zoneRepository.save(any(RestrictedZone.class))).thenReturn(saved);

        ZoneResponse response = zoneService.createZone(req);

        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getName()).isEqualTo("North Perimeter");
    }

    @Test
    @DisplayName("createZone: duplicate name throws")
    void createZone_duplicateName_throws() {
        ZoneRequest req = new ZoneRequest();
        req.setName("Existing Zone");
        req.setActive(true);

        when(zoneRepository.existsByNameIgnoreCase("Existing Zone")).thenReturn(true);

        assertThatThrownBy(() -> zoneService.createZone(req))
                .isInstanceOf(DuplicateResourceException.class);
    }

    @Test
    @DisplayName("getZoneById: not found throws ResourceNotFoundException")
    void getZoneById_notFound_throws() {
        when(zoneRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> zoneService.getZoneById(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("getAllZones: returns list")
    void getAllZones_returnsList() {
        RestrictedZone z1 = RestrictedZone.builder().id(1L).name("Zone A").active(true).build();
        RestrictedZone z2 = RestrictedZone.builder().id(2L).name("Zone B").active(false).build();
        when(zoneRepository.findAll()).thenReturn(List.of(z1, z2));

        List<ZoneResponse> result = zoneService.getAllZones();

        assertThat(result).hasSize(2);
    }

    @Test
    @DisplayName("deleteZone: success")
    void deleteZone_success() {
        RestrictedZone zone = RestrictedZone.builder().id(3L).name("Zone C").active(true).build();
        when(zoneRepository.findById(3L)).thenReturn(Optional.of(zone));

        assertThatCode(() -> zoneService.deleteZone(3L)).doesNotThrowAnyException();
        verify(zoneRepository).delete(zone);
    }
}
