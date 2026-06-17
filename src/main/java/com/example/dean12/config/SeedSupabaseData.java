package com.example.dean12.config;

import com.example.dean12.desktop.network.ServerDao;

public class SeedSupabaseData {
    public static void main(String[] args) {
        ServerDao dao = new ServerDao();
        dao.initializeDatabaseSchema();
        dao.seedSampleDataIfEmpty();
        System.out.println("Seed completed. Demo accounts: admin, gv01-gv05, sv01-sv60. Password: 123");
    }
}
