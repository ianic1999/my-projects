package com.example.demo;

import com.example.demo.dto.SettingDTO;
import com.example.demo.dto.SettingValueDTO;
import com.example.demo.model.Setting;
import com.example.demo.model.SettingValue;
import com.example.demo.model.User;
import com.example.demo.model.UserRole;
import com.example.demo.repository.SettingRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.SettingService;
import com.example.demo.service.UserService;
import com.example.demo.util.PasswordUtil;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Properties;
import java.util.Random;

@SpringBootApplication
public class SpringRunner {

    public static void main(String[] args) {
        SpringApplication.run(SpringRunner.class, args);
    }

    @Bean
    public CommandLineRunner init(UserService userService,
                                  UserRepository userRepository,
                                  BCryptPasswordEncoder passwordEncoder,
                                  SettingRepository settingRepository,
                                  SettingService settingService) {
        return args -> {
            boolean addUser = true;
            if (userRepository.findByEmail("info@dots.md").isPresent()) {
                User user = userRepository.findByEmail("info@dots.md").get();
                if (user.getRole().equals(UserRole.USER))
                    userService.remove(user.getId());
                else {
                    addUser = false;
                    if (user.getImage() == null) {
                        user.setImage("images/users/cornilov.png");
                        userRepository.save(user);
                    }
                }
            }
            if (addUser) {
                User user = User.builder()
                        .email("info@dots.md")
                        .fullName("Dan Cornilov")
                        .role(UserRole.ADMIN)
                        .image("images/users/cornilov.png")
                        .build();
                user.setPassword(passwordEncoder.encode("dots1!"));
                userRepository.save(user);
            }
            addUser = true;
            if (userRepository.findByEmail("fablab.chisinau@gmail.com").isPresent()) {
                User user = userRepository.findByEmail("fablab.chisinau@gmail.com").get();
                if (user.getRole().equals(UserRole.USER))
                    userService.remove(user.getId());
                else {
                    addUser = false;
                    if (user.getImage() == null) {
                        user.setImage("images/users/fablab.png");
                        userRepository.save(user);
                    }
                }
            }
            if (addUser) {
                User user = User.builder()
                        .email("fablab.chisinau@gmail.com")
                        .fullName("FABLAB Admin")
                        .role(UserRole.ADMIN)
                        .image("images/users/fablab.png")
                        .build();
                user.setPassword(passwordEncoder.encode(PasswordUtil.generatePassword()));
                userRepository.save(user);
            }

            if (!settingRepository.findByKey("email").isPresent()) {
                SettingDTO setting = SettingDTO.builder()
                        .key("email")
                        .isPublic(true)
                        .value(
                                SettingValueDTO.builder()
                                        .ro("fabLab.chisinau@gmail.com")
                                        .en("fabLab.chisinau@gmail.com")
                                        .ru("fabLab.chisinau@gmail.com")
                                        .build()
                        )
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .build();
                settingService.add(setting);
            }

            if (!settingRepository.findByKey("phone").isPresent()) {
                SettingDTO setting = SettingDTO.builder()
                        .key("phone")
                        .isPublic(true)
                        .value(
                                SettingValueDTO.builder()
                                        .ro("+373 694 74 011")
                                        .en("+373 694 74 011")
                                        .ru("+373 694 74 011")
                                        .build()
                        )
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .build();
                settingService.add(setting);
            }

            if (!settingRepository.findByKey("homeTextFirst").isPresent()) {
                SettingDTO setting = SettingDTO.builder()
                        .key("homeTextFirst")
                        .isPublic(true)
                        .value(
                                SettingValueDTO.builder()
                                        .ro("Amplasat în zona unui campus studențesc, Fablab are drept scop dezoltarea programelor în domeniile inginerești, împreuna cu Universitatea Tehnică a Moldovei, pentru stimularea inovației, fabricării digitale și spiritul antreprenorial în rândul membrilor comunității.")
                                        .en("Amplasat în zona unui campus studențesc, Fablab are drept scop dezoltarea programelor în domeniile inginerești, împreuna cu Universitatea Tehnică a Moldovei, pentru stimularea inovației, fabricării digitale și spiritul antreprenorial în rândul membrilor comunității.")
                                        .ru("Amplasat în zona unui campus studențesc, Fablab are drept scop dezoltarea programelor în domeniile inginerești, împreuna cu Universitatea Tehnică a Moldovei, pentru stimularea inovației, fabricării digitale și spiritul antreprenorial în rândul membrilor comunității.")
                                        .build()
                        )
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .build();
                settingService.add(setting);
            }

            if (!settingRepository.findByKey("homeTextSecond").isPresent()) {
                SettingDTO setting = SettingDTO.builder()
                        .key("homeTextSecond")
                        .isPublic(true)
                        .value(
                                SettingValueDTO.builder()
                                        .ro("Pe lângă spațiu de lucru, Laboratorul Digital vine cu un set de echipamente și utilaje cum ar fi: mașini cu control numeric, care oferă posibilitatea membrilor comunității de a prototipa orice prototip utilizând imprimante 3D, echipament de gravare laser.")
                                        .en("Pe lângă spațiu de lucru, Laboratorul Digital vine cu un set de echipamente și utilaje cum ar fi: mașini cu control numeric, care oferă posibilitatea membrilor comunității de a prototipa orice prototip utilizând imprimante 3D, echipament de gravare laser.")
                                        .ru("Pe lângă spațiu de lucru, Laboratorul Digital vine cu un set de echipamente și utilaje cum ar fi: mașini cu control numeric, care oferă posibilitatea membrilor comunității de a prototipa orice prototip utilizând imprimante 3D, echipament de gravare laser.")
                                        .build()
                        )
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .build();
                settingService.add(setting);
            }

            if (!settingRepository.findByKey("contactText").isPresent()) {
                SettingDTO setting = SettingDTO.builder()
                        .key("contactText")
                        .isPublic(true)
                        .value(
                                SettingValueDTO.builder()
                                        .ro("bla Contactează-ne in sjsj uconsetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat.")
                                        .en("bla Contactează-ne in sjsj uconsetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat.")
                                        .ru("bla Contactează-ne in sjsj uconsetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat.")
                                        .build()
                        )
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .build();
                settingService.add(setting);
            }

            if (!settingRepository.findByKey("aboutTextFirst").isPresent()) {
                SettingDTO setting = SettingDTO.builder()
                        .key("aboutTextFirst")
                        .isPublic(true)
                        .value(
                                SettingValueDTO.builder()
                                        .ro("Cu programe extinse educaționale, FabLab Chișinău vine să contribuie la dezvoltarea competențelor noilor specialiști. Fablab pune la dispoziție sală pentru activități/ competiții/ evenimente cu o capacitate de peste 100 de persoane dotată cu echipament necesar acestor activități")
                                        .en("Cu programe extinse educaționale, FabLab Chișinău vine să contribuie la dezvoltarea competențelor noilor specialiști. Fablab pune la dispoziție sală pentru activități/ competiții/ evenimente cu o capacitate de peste 100 de persoane dotată cu echipament necesar acestor activități")
                                        .ru("Cu programe extinse educaționale, FabLab Chișinău vine să contribuie la dezvoltarea competențelor noilor specialiști. Fablab pune la dispoziție sală pentru activități/ competiții/ evenimente cu o capacitate de peste 100 de persoane dotată cu echipament necesar acestor activități")
                                        .build()
                        )
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .build();
                settingService.add(setting);
            }

            if (!settingRepository.findByKey("aboutTextSecond").isPresent()) {
                SettingDTO setting = SettingDTO.builder()
                        .key("aboutTextSecond")
                        .isPublic(true)
                        .value(
                                SettingValueDTO.builder()
                                        .ro("FabLab Chișinău - primul și cel mai mare MAKERspace din țară - se întindem pe o suprafață de peste 800 m2, cu instrumente de prelucrare a lemnului, metalului, printare 3D, zone de lucru.")
                                        .en("FabLab Chișinău - primul și cel mai mare MAKERspace din țară - se întindem pe o suprafață de peste 800 m2, cu instrumente de prelucrare a lemnului, metalului, printare 3D, zone de lucru.")
                                        .ru("FabLab Chișinău - primul și cel mai mare MAKERspace din țară - se întindem pe o suprafață de peste 800 m2, cu instrumente de prelucrare a lemnului, metalului, printare 3D, zone de lucru.")
                                        .build()
                        )
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .build();
                settingService.add(setting);
            }

            if (!settingRepository.findByKey("instagram").isPresent()) {
                SettingDTO setting = SettingDTO.builder()
                        .key("instagram")
                        .isPublic(true)
                        .value(
                                SettingValueDTO.builder()
                                        .ro("https://www.instagram.com/fablab.chisinau/")
                                        .en("https://www.instagram.com/fablab.chisinau/")
                                        .ru("https://www.instagram.com/fablab.chisinau/")
                                        .build()
                        )
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .build();
                settingService.add(setting);
            }

            if (!settingRepository.findByKey("facebook").isPresent()) {
                SettingDTO setting = SettingDTO.builder()
                        .key("facebook")
                        .isPublic(true)
                        .value(
                                SettingValueDTO.builder()
                                        .ro("https://www.facebook.com/fablabchisinau")
                                        .en("https://www.facebook.com/fablabchisinau")
                                        .ru("https://www.facebook.com/fablabchisinau")
                                        .build()
                        )
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .build();
                settingService.add(setting);
            }

            if (!settingRepository.findByKey("youtube").isPresent()) {
                SettingDTO setting = SettingDTO.builder()
                        .key("youtube")
                        .isPublic(true)
                        .value(
                                SettingValueDTO.builder()
                                        .ro("https://www.youtube.com/")
                                        .en("https://www.youtube.com/")
                                        .ru("https://www.youtube.com/")
                                        .build()
                        )
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .build();
                settingService.add(setting);
            }

            if (!settingRepository.findByKey("street").isPresent()) {
                SettingDTO setting = SettingDTO.builder()
                        .key("street")
                        .isPublic(true)
                        .value(
                                SettingValueDTO.builder()
                                        .ro("Str. Studenților 9/11")
                                        .en("Str. Studenților 9/11")
                                        .ru("Str. Studenților 9/11")
                                        .build()
                        )
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .build();
                settingService.add(setting);
            }
        };
    }

}
