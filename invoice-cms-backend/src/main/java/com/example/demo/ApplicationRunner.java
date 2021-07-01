package com.example.demo;

import com.example.demo.model.City;
import com.example.demo.model.Country;
import com.example.demo.model.User;
import com.example.demo.model.enums.UserRole;
import com.example.demo.repository.CityRepository;
import com.example.demo.repository.CountryRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.io.File;
import java.io.FileNotFoundException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

@SpringBootApplication
@EnableScheduling
public class ApplicationRunner {

    public static void main(String[] args) {
        SpringApplication.run(ApplicationRunner.class, args);
    }

    @Bean
    public CommandLineRunner init(UserRepository userRepository,
                                  BCryptPasswordEncoder passwordEncoder,
                                  CountryRepository countryRepository,
                                  CityRepository cityRepository) {
        return args -> {
            addAdminUser(userRepository, passwordEncoder);
            if (cityRepository.count() == 0)
                addCountriesAndCities(countryRepository, cityRepository);
        };
    }

    private void addAdminUser(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder) {
        boolean addUser = false;
        User user = User.builder()
                .firstName("DOTS")
                .lastName("DOTS")
                .email("info@dots.md")
                .role(UserRole.ADMIN)
                .password(passwordEncoder.encode("Dots123!"))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        if (userRepository.findByEmail("info@dots.md").isPresent()) {
            if (userRepository.findByEmail("info@dots.md").get().getRole().equals(UserRole.USER)) {
                userRepository.deleteById(userRepository.findByEmail("info@dots.md").get().getId());
                addUser = true;
            }
        } else {
            addUser = true;
        }
        if (addUser)
            userRepository.save(user);
    }

    private void addCountriesAndCities(CountryRepository countryRepository, CityRepository cityRepository) throws FileNotFoundException {
        File file = new File("src/main/resources/GEODATASOURCE-WORLD-MAJOR-CITIES.TXT");
        Scanner scanner = new Scanner(file);
        String country = "";
        List<String> cities = new ArrayList<>();
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            String[] components = line.split("\t");
            if (!components[1].toLowerCase().equals(country.toLowerCase())) {
                if (!country.equals("")) {
                    Country countryToAdd = Country.builder()
                            .name(country)
                            .build();
                    countryToAdd = countryRepository.save(countryToAdd);
                    for (String city : cities) {
                        City cityToAdd = City.builder()
                                .name(city)
                                .country(countryToAdd)
                                .build();
                        cityToAdd = cityRepository.save(cityToAdd);
                    }
                }
                country = components[1];
                cities.clear();
            }
            cities.add(components[components.length - 4]);
        }
        Country countryToAdd = Country.builder()
                .name(country)
                .build();
        countryToAdd = countryRepository.save(countryToAdd);
        for (String city : cities) {
            City cityToAdd = City.builder()
                    .name(city)
                    .country(countryToAdd)
                    .build();
            cityToAdd = cityRepository.save(cityToAdd);
        }
    }
}
