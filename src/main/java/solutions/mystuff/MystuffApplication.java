package solutions.mystuff;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Spring Boot entry point for the Mystuff application.
 */
@SpringBootApplication
public class MystuffApplication {

    /** Launches the Spring Boot application. */
    public static void main(String[] args) {
        SpringApplication.run(
                MystuffApplication.class, args);
    }
}
