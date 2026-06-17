package com.example.dean12;

import com.example.dean12.model.User;
import com.example.dean12.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "app.seed-on-startup", havingValue = "true")
@Order(1)
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        saveDemoUser("admin", "ADMIN", "admin@school.com");
        for (int i = 1; i <= 5; i++) {
            String u = String.format("gv%02d", i);
            saveDemoUser(u, "TEACHER", u + "@school.com");
        }
        for (int i = 1; i <= 60; i++) {
            String u = String.format("sv%02d", i);
            saveDemoUser(u, "STUDENT", u + "@school.com");
        }
        System.out.println("[DataInitializer] Tài khoản: admin, gv01-gv05, sv01-sv60 — mật khẩu: 123");
    }

    private void saveDemoUser(String username, String role, String email) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            user = new User();
            user.setUsername(username);
        }
        user.setEmail(email);
        user.setRole(role);
        user.setPassword(passwordEncoder.encode("123"));
        user.setLocked(false);
        userRepository.save(user);
    }
}
