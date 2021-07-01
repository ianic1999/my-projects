package com.example.demo;

import com.example.demo.model.User;
import com.example.demo.model.enums.ServiceType;
import com.example.demo.model.enums.UserRole;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication
public class DemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

    @Bean
    public CommandLineRunner init(UserService userService,
                                  UserRepository userRepository,
                                  BCryptPasswordEncoder passwordEncoder) {
        return args -> {
            boolean addUser = true;
            if (userRepository.findByEmail("info@dots.md").isPresent()) {
                User user = userRepository.findByEmail("info@dots.md").get();
                if (user.getRole().equals(UserRole.USER))
                    userService.remove(user.getId());
                else {
                    addUser = false;
                    if (user.getImage() == null) {
                        user.setImage("images/users/man-300x300.png");
                        userRepository.save(user);
                    }
                }
            }
            if (addUser) {
                User admin = User.builder()
                        .email("info@dots.md")
                        .fullName("Admin")
                        .password(passwordEncoder.encode("Dots123!"))
                        .role(UserRole.ADMIN)
                        .image("images/users/man-300x300.png")
                        .build();
                userRepository.save(admin);
            }
        };
    }
}
