package com.example.dean12;

import com.example.dean12.desktop.network.ConfigUtil;
import com.example.dean12.desktop.network.ServerDao;
import com.example.dean12.desktop.network.TcpSocketServer;

public class ServerMain {

    public static void main(String[] args) {
        int port = ConfigUtil.getIntProperty("server.port", 9000);
        ServerDao dao = new ServerDao();
        dao.initializeDatabaseSchema();
        dao.seedSampleDataIfEmpty();
        System.out.println("Starting QLSV TCP server on port " + port + "...");
        System.out.println("This is a desktop backend socket, not a web server.");
        boolean started = TcpSocketServer.start(port);
        if (!started) {
            System.exit(1);
        }
    }
}
