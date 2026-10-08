package com.animalmonitoring.config;

import com.animalmonitoring.entity.*;
import com.animalmonitoring.repository.AnimalRepository;
import com.animalmonitoring.repository.RecommendationRepository;
import com.animalmonitoring.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Seeds the database with initial animals, recommendations, and a default ADMIN user
 * on first run. Skipped if data already exists to ensure idempotency.
 *
 * Not active during tests — the "test" profile uses H2 with its own lifecycle.
 */
@Component
@Profile("!test")
public class DataSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataSeeder.class);

    private final AnimalRepository animalRepository;
    private final RecommendationRepository recommendationRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataSeeder(AnimalRepository animalRepository,
                      RecommendationRepository recommendationRepository,
                      UserRepository userRepository,
                      PasswordEncoder passwordEncoder) {
        this.animalRepository = animalRepository;
        this.recommendationRepository = recommendationRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) {
        seedAnimalsAndRecommendations();
        seedDefaultAdmin();
    }

    // ------------------------------------------------------------------ animals
    private void seedAnimalsAndRecommendations() {
        if (animalRepository.count() > 0) {
            log.info("Animals already seeded — skipping.");
            return;
        }

        log.info("Seeding animals and recommendations...");

        Animal elephant = saveAnimal("Elephant", "Elephas maximus", RiskLevel.HIGH,
                "Large mammal; highly dangerous when startled or protecting young");

        Animal tiger = saveAnimal("Tiger", "Panthera tigris", RiskLevel.CRITICAL,
                "Apex predator; extremely dangerous to humans and livestock");

        Animal leopard = saveAnimal("Leopard", "Panthera pardus", RiskLevel.CRITICAL,
                "Stealthy apex predator; attacks swiftly and without warning");

        Animal deer = saveAnimal("Deer", "Cervidae spp.", RiskLevel.LOW,
                "Herbivore; generally not dangerous but may damage crops");

        Animal boar = saveAnimal("Wild Boar", "Sus scrofa", RiskLevel.MEDIUM,
                "Aggressive when cornered; causes significant crop damage");

        Animal bear = saveAnimal("Bear", "Ursus arctos", RiskLevel.HIGH,
                "Omnivore; dangerous when food-conditioned or with cubs");

        Animal monkey = saveAnimal("Monkey", "Macaca mulatta", RiskLevel.LOW,
                "Raids crops and food stores; rarely physically dangerous");

        seedRecommendations(elephant, tiger, leopard, deer, boar, bear, monkey);
        log.info("Seeded {} animals.", animalRepository.count());
    }

    private void seedRecommendations(Animal elephant, Animal tiger, Animal leopard,
                                     Animal deer, Animal boar, Animal bear, Animal monkey) {

        List<Recommendation> recs = List.of(
            buildRec(elephant, RiskLevel.HIGH,
                "Keep all personnel away from the area immediately. Alert forest department. " +
                "Do not approach or provoke. Use sound deterrents from a safe distance."),
            buildRec(elephant, RiskLevel.MEDIUM,
                "Monitor the animal's movement from a safe distance. Inform nearby farmers " +
                "to secure livestock and avoid open areas."),

            buildRec(tiger, RiskLevel.CRITICAL,
                "EVACUATE immediately. Alert the wildlife rescue team and local authorities. " +
                "Do not go outdoors alone. Secure all livestock in closed shelters."),
            buildRec(tiger, RiskLevel.HIGH,
                "Restrict movement in the affected area. Inform forest department. " +
                "Keep livestock indoors. Use floodlights to deter the animal at night."),

            buildRec(leopard, RiskLevel.CRITICAL,
                "EVACUATE immediately. Secure all entry points of buildings. Alert forest " +
                "department for emergency response. Never corner or confront the animal."),
            buildRec(leopard, RiskLevel.HIGH,
                "Keep children and elderly indoors. Inform forest department. " +
                "Secure goats and sheep. Install motion-activated lights."),

            buildRec(deer, RiskLevel.LOW,
                "Install fencing around crop fields to prevent grazing damage. " +
                "No immediate danger to humans. Monitor frequency of visits."),

            buildRec(boar, RiskLevel.MEDIUM,
                "Do not approach, especially if with piglets. Install trenches or fencing " +
                "around crops. Report persistent intrusions to the forest office."),
            buildRec(boar, RiskLevel.LOW,
                "Monitor crop damage. Use noise-based deterrents at night. " +
                "Install low-voltage electric fence around vulnerable crops."),

            buildRec(bear, RiskLevel.HIGH,
                "Alert all villagers to stay indoors. Secure food and waste bins. " +
                "Contact forest department immediately. Do not leave food waste outdoors."),
            buildRec(bear, RiskLevel.MEDIUM,
                "Avoid the area after dark. Secure livestock and beehives. " +
                "Report the sighting to forest authorities for tracking."),

            buildRec(monkey, RiskLevel.LOW,
                "Secure food stores and granaries. Do not feed monkeys. " +
                "Use noise deterrents to drive them away from inhabited areas.")
        );

        recommendationRepository.saveAll(recs);
    }

    // ------------------------------------------------------------------ admin
    private void seedDefaultAdmin() {
        if (userRepository.existsByEmail("admin@animalmonitoring.com")) {
            log.info("Default admin already exists — skipping.");
            return;
        }

        User admin = User.builder()
                .name("System Administrator")
                .email("admin@animalmonitoring.com")
                .password(passwordEncoder.encode("Admin@1234"))
                .role(Role.ADMIN)
                .phone("+910000000000")
                .location("System")
                .enabled(true)
                .build();

        userRepository.save(admin);
        log.info("Default admin created  →  email: admin@animalmonitoring.com  password: Admin@1234");
    }

    // ------------------------------------------------------------------ helpers
    private Animal saveAnimal(String name, String sci, RiskLevel risk, String desc) {
        Animal animal = Animal.builder()
                .name(name)
                .scientificName(sci)
                .defaultRiskLevel(risk)
                .description(desc)
                .active(true)
                .build();
        return animalRepository.save(animal);
    }

    private Recommendation buildRec(Animal animal, RiskLevel riskLevel, String text) {
        return Recommendation.builder()
                .animal(animal)
                .riskLevel(riskLevel)
                .recommendation(text)
                .active(true)
                .build();
    }
}
