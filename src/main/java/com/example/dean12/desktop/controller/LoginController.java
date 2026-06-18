package com.example.dean12.desktop.controller;

import com.example.dean12.desktop.data.DesktopDao;
import com.example.dean12.model.User;

public class LoginController {
    private final DesktopDao dao;

    public LoginController(DesktopDao dao) {
        this.dao = dao;
    }

    public User login(String username, String password) {
        return dao.login(username, password);
    }
}
