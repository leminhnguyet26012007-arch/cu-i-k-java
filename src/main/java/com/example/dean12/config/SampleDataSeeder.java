package com.example.dean12.config;

import com.example.dean12.desktop.network.ServerDao;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "app.seed-on-startup", havingValue = "true")
@Order(2)
public class SampleDataSeeder implements CommandLineRunner {

    @Override
    public void run(String... args) {
        ServerDao dao = new ServerDao();
        dao.initializeDatabaseSchema();
        dao.seedSampleDataIfEmpty();
    }
}
