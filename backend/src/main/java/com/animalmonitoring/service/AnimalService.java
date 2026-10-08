package com.animalmonitoring.service;

import com.animalmonitoring.dto.request.AnimalRequest;
import com.animalmonitoring.dto.response.AnimalResponse;
import com.animalmonitoring.entity.Animal;
import com.animalmonitoring.exception.DuplicateResourceException;
import com.animalmonitoring.exception.ResourceNotFoundException;
import com.animalmonitoring.repository.AnimalRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AnimalService {

    private static final Logger log = LoggerFactory.getLogger(AnimalService.class);

    private final AnimalRepository animalRepository;

    public AnimalService(AnimalRepository animalRepository) {
        this.animalRepository = animalRepository;
    }

    @Transactional(readOnly = true)
    public List<AnimalResponse> getAllAnimals() {
        return animalRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<AnimalResponse> getActiveAnimals() {
        return animalRepository.findByActive(true).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public AnimalResponse getAnimalById(Long id) {
        return mapToResponse(findById(id));
    }

    @Transactional
    public AnimalResponse createAnimal(AnimalRequest request) {
        if (animalRepository.existsByNameIgnoreCase(request.getName())) {
            throw new DuplicateResourceException("Animal already exists with name: " + request.getName());
        }
        Animal animal = Animal.builder()
                .name(request.getName())
                .scientificName(request.getScientificName())
                .defaultRiskLevel(request.getDefaultRiskLevel())
                .description(request.getDescription())
                .active(request.isActive())
                .build();
        Animal saved = animalRepository.save(animal);
        log.info("Created animal '{}' with id {}", saved.getName(), saved.getId());
        return mapToResponse(saved);
    }

    @Transactional
    public AnimalResponse updateAnimal(Long id, AnimalRequest request) {
        Animal animal = findById(id);

        if (!animal.getName().equalsIgnoreCase(request.getName())
                && animalRepository.existsByNameIgnoreCase(request.getName())) {
            throw new DuplicateResourceException("Animal already exists with name: " + request.getName());
        }

        animal.setName(request.getName());
        animal.setScientificName(request.getScientificName());
        animal.setDefaultRiskLevel(request.getDefaultRiskLevel());
        animal.setDescription(request.getDescription());
        animal.setActive(request.isActive());

        Animal saved = animalRepository.save(animal);
        log.info("Updated animal id {}", saved.getId());
        return mapToResponse(saved);
    }

    @Transactional
    public void deleteAnimal(Long id) {
        Animal animal = findById(id);
        animalRepository.delete(animal);
        log.info("Deleted animal id {}", id);
    }

    private Animal findById(Long id) {
        return animalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Animal", "id", id));
    }

    public AnimalResponse mapToResponse(Animal animal) {
        return AnimalResponse.builder()
                .id(animal.getId())
                .name(animal.getName())
                .scientificName(animal.getScientificName())
                .defaultRiskLevel(animal.getDefaultRiskLevel())
                .description(animal.getDescription())
                .active(animal.isActive())
                .build();
    }
}
