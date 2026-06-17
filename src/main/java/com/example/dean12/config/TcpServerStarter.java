package com.example.dean12.config;

import com.example.dean12.desktop.network.ConfigUtil;
import com.example.dean12.desktop.network.TcpSocketServer;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(1)
public class TcpServerStarter implements CommandLineRunner {

    @Override
    public void run(String... args) {
        int tcpPort = ConfigUtil.getIntProperty("server.port", 9000);
        Thread tcpThread = new Thread(() -> TcpSocketServer.start(tcpPort), "TCP-Socket-Server");
        tcpThread.setDaemon(true);
        tcpThread.start();

        System.out.println("=========================================");
        System.out.println("QLSV server starting");
        System.out.println("Web: http://localhost:8081/login");
        System.out.println("TCP for desktop client: localhost:" + tcpPort);
        System.out.println("Login demo: admin/gv01/sv01 - password: 123");
        System.out.println("=========================================");
    }
}
