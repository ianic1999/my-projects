package com.example.demo;

import com.example.demo.model.User;
import com.example.demo.model.UserRole;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public CommandLineRunner init(UserRepository userRepository,
                                  BCryptPasswordEncoder passwordEncoder) {
        return args -> {
            boolean addUser = false;
            Optional<User> optional = userRepository.findAll().stream()
                    .filter(user -> user.getEmail().equals("info@dots.md") && user.getRole().equals(UserRole.USER))
                    .findFirst();
            optional.ifPresent(user -> userRepository.deleteById(user.getId()));
            optional = userRepository.findByEmail("info@dots.md");
            if (optional.isPresent()) {
                if (optional.get().getRole().equals(UserRole.USER)) {
                    addUser = true;
                }
            } else {
                addUser = true;
            }
            if (addUser) {
                User user = User.builder()
                        .name("DOTS")
                        .email("info@dots.md")
                        .password(passwordEncoder.encode("Dots1!"))
                        .role(UserRole.ADMIN)
                        .build();
                userRepository.save(user);
            }
        };
    }


}
