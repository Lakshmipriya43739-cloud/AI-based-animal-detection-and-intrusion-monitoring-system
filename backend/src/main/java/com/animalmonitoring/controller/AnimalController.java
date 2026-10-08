package com.animalmonitoring.controller;

import com.animalmonitoring.dto.request.AnimalRequest;
import com.animalmonitoring.dto.response.AnimalResponse;
import com.animalmonitoring.dto.response.ApiResponse;
import com.animalmonitoring.service.AnimalService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/animals")
public class AnimalController {

    private final AnimalService animalService;

    public AnimalController(AnimalService animalService) {
        this.animalService = animalService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<AnimalResponse>>> getAllAnimals() {
        return ResponseEntity.ok(ApiResponse.success(animalService.getAllAnimals()));
    }

    @GetMapping("/active")
    public ResponseEntity<ApiResponse<List<AnimalResponse>>> getActiveAnimals() {
        return ResponseEntity.ok(ApiResponse.success(animalService.getActiveAnimals()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<AnimalResponse>> getAnimalById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(animalService.getAnimalById(id)));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<AnimalResponse>> createAnimal(
            @Valid @RequestBody AnimalRequest request) {
        AnimalResponse created = animalService.createAnimal(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Animal created successfully", created));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<AnimalResponse>> updateAnimal(
            @PathVariable Long id,
            @Valid @RequestBody AnimalRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Animal updated successfully",
                animalService.updateAnimal(id, request)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteAnimal(@PathVariable Long id) {
        animalService.deleteAnimal(id);
        return ResponseEntity.ok(ApiResponse.success("Animal deleted successfully", null));
    }
}
