package com.animalmonitoring.service;

import com.animalmonitoring.dto.request.AnimalRequest;
import com.animalmonitoring.dto.response.AnimalResponse;
import com.animalmonitoring.entity.Animal;
import com.animalmonitoring.entity.RiskLevel;
import com.animalmonitoring.exception.DuplicateResourceException;
import com.animalmonitoring.exception.ResourceNotFoundException;
import com.animalmonitoring.repository.AnimalRepository;
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
@DisplayName("AnimalService unit tests")
class AnimalServiceTest {

    @Mock
    private AnimalRepository animalRepository;

    @InjectMocks
    private AnimalService animalService;

    // ------------------------------------------------------------------ create
    @Test
    @DisplayName("createAnimal: success")
    void createAnimal_success() {
        AnimalRequest req = new AnimalRequest();
        req.setName("Wolf");
        req.setDefaultRiskLevel(RiskLevel.HIGH);
        req.setActive(true);

        when(animalRepository.existsByNameIgnoreCase("Wolf")).thenReturn(false);

        Animal saved = Animal.builder().id(10L).name("Wolf")
                .defaultRiskLevel(RiskLevel.HIGH).active(true).build();
        when(animalRepository.save(any(Animal.class))).thenReturn(saved);

        AnimalResponse response = animalService.createAnimal(req);

        assertThat(response.getId()).isEqualTo(10L);
        assertThat(response.getName()).isEqualTo("Wolf");
        assertThat(response.getDefaultRiskLevel()).isEqualTo(RiskLevel.HIGH);
    }

    @Test
    @DisplayName("createAnimal: duplicate name throws DuplicateResourceException")
    void createAnimal_duplicateName_throws() {
        AnimalRequest req = new AnimalRequest();
        req.setName("Tiger");
        req.setDefaultRiskLevel(RiskLevel.CRITICAL);

        when(animalRepository.existsByNameIgnoreCase("Tiger")).thenReturn(true);

        assertThatThrownBy(() -> animalService.createAnimal(req))
                .isInstanceOf(DuplicateResourceException.class);
    }

    // ------------------------------------------------------------------ read
    @Test
    @DisplayName("getAnimalById: found returns AnimalResponse")
    void getAnimalById_found() {
        Animal animal = Animal.builder().id(1L).name("Elephant")
                .defaultRiskLevel(RiskLevel.HIGH).active(true).build();
        when(animalRepository.findById(1L)).thenReturn(Optional.of(animal));

        AnimalResponse response = animalService.getAnimalById(1L);

        assertThat(response.getName()).isEqualTo("Elephant");
    }

    @Test
    @DisplayName("getAnimalById: not found throws ResourceNotFoundException")
    void getAnimalById_notFound_throws() {
        when(animalRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> animalService.getAnimalById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Animal");
    }

    // ------------------------------------------------------------------ update
    @Test
    @DisplayName("updateAnimal: same name does not trigger duplicate check")
    void updateAnimal_sameName_success() {
        Animal existing = Animal.builder().id(1L).name("Deer")
                .defaultRiskLevel(RiskLevel.LOW).active(true).build();
        when(animalRepository.findById(1L)).thenReturn(Optional.of(existing));

        AnimalRequest req = new AnimalRequest();
        // Same name → the duplicate-check branch is NOT entered, no stub needed
        req.setName("Deer");
        req.setDefaultRiskLevel(RiskLevel.MEDIUM);
        req.setActive(true);

        // Return the same object back from save
        when(animalRepository.save(any(Animal.class))).thenAnswer(i -> i.getArgument(0));

        AnimalResponse response = animalService.updateAnimal(1L, req);

        assertThat(response.getDefaultRiskLevel()).isEqualTo(RiskLevel.MEDIUM);
    }

    @Test
    @DisplayName("updateAnimal: new name that is duplicate throws DuplicateResourceException")
    void updateAnimal_newDuplicateName_throws() {
        Animal existing = Animal.builder().id(1L).name("Deer")
                .defaultRiskLevel(RiskLevel.LOW).active(true).build();
        when(animalRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(animalRepository.existsByNameIgnoreCase("Tiger")).thenReturn(true);

        AnimalRequest req = new AnimalRequest();
        req.setName("Tiger");   // different name, already taken
        req.setDefaultRiskLevel(RiskLevel.CRITICAL);
        req.setActive(true);

        assertThatThrownBy(() -> animalService.updateAnimal(1L, req))
                .isInstanceOf(DuplicateResourceException.class);
    }

    // ------------------------------------------------------------------ delete
    @Test
    @DisplayName("deleteAnimal: success")
    void deleteAnimal_success() {
        Animal animal = Animal.builder().id(5L).name("Bear")
                .defaultRiskLevel(RiskLevel.HIGH).active(true).build();
        when(animalRepository.findById(5L)).thenReturn(Optional.of(animal));

        assertThatCode(() -> animalService.deleteAnimal(5L)).doesNotThrowAnyException();
        verify(animalRepository).delete(animal);
    }

    @Test
    @DisplayName("getAllAnimals: returns list")
    void getAllAnimals_returnsList() {
        Animal a1 = Animal.builder().id(1L).name("Elephant")
                .defaultRiskLevel(RiskLevel.HIGH).active(true).build();
        Animal a2 = Animal.builder().id(2L).name("Tiger")
                .defaultRiskLevel(RiskLevel.CRITICAL).active(true).build();
        when(animalRepository.findAll()).thenReturn(List.of(a1, a2));

        List<AnimalResponse> result = animalService.getAllAnimals();

        assertThat(result).hasSize(2);
        assertThat(result).extracting(AnimalResponse::getName)
                .containsExactlyInAnyOrder("Elephant", "Tiger");
    }
}
