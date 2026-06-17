package com.example.dean12.controller;

import com.example.dean12.model.*;
import com.example.dean12.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AppController {

    @Autowired private SinhVienService sinhVienService;
    @Autowired private GiangVienService giangVienService;
    @Autowired private MonHocService monHocService;
    @Autowired private ThongBaoService thongBaoService;
    @Autowired private DiemService diemService;

    @GetMapping("/")
    public String index() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String role = auth.getAuthorities().stream().findFirst().get().getAuthority();

        if (role.equals("ROLE_ADMIN")) {
            return "redirect:/admin/dashboard";
        } else if (role.equals("ROLE_TEACHER")) {
            return "redirect:/teacher/dashboard";
        } else if (role.equals("ROLE_STUDENT")) {
            return "redirect:/student/dashboard";
        }
        return "login";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    // --- ADMIN ROUTES ---
    @GetMapping("/admin/dashboard")
    public String adminDashboard(Model model) {
        model.addAttribute("students", sinhVienService.getAllStudents());
        model.addAttribute("teachers", giangVienService.getAll());
        model.addAttribute("courses", monHocService.getAll());
        model.addAttribute("active", "users");
        return "admin/dashboard"; // Maps to templates/admin/dashboard.html
    }

    // --- TEACHER ROUTES ---
    @GetMapping("/teacher/dashboard")
    public String teacherDashboard(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        GiangVien gv = giangVienService.getByUsername(auth.getName());
        if (gv != null) {
            model.addAttribute("myself", gv);
        }
        // In real app, filter students by teacher's class
        model.addAttribute("students", sinhVienService.getAllStudents());
        model.addAttribute("active", "dashboard");
        return "teacher/dashboard"; // Maps to templates/teacher/dashboard.html
    }

    @GetMapping("/teacher/profile")
    public String teacherProfile(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        GiangVien gv = giangVienService.getByUsername(auth.getName());
        if (gv != null) {
            model.addAttribute("myself", gv);
        }
        model.addAttribute("active", "profile");
        return "teacher/profile";
    }

    @GetMapping("/teacher/students")
    public String teacherStudents(Model model) {
        model.addAttribute("active", "students");
        model.addAttribute("students", sinhVienService.getAllStudents());
        return "teacher/students";
    }

    @GetMapping("/teacher/courses")
    public String teacherCourses(Model model) {
        model.addAttribute("active", "courses");
        model.addAttribute("courses", monHocService.getAll());
        return "teacher/courses";
    }

    @GetMapping("/teacher/grades")
    public String teacherGrades(Model model) {
        model.addAttribute("active", "grades");
        model.addAttribute("students", sinhVienService.getAllStudents());
        return "teacher/grades";
    }

    @GetMapping("/teacher/notifications")
    public String teacherNotifications(Model model) {
        model.addAttribute("active", "notifications");
        model.addAttribute("notifications", thongBaoService.getAll());
        return "teacher/notifications";
    }

    // --- STUDENT ROUTES ---
    @GetMapping("/student/dashboard")
    public String studentDashboard(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        SinhVien sv = sinhVienService.getByUsername(auth.getName());
        if (sv != null) {
            model.addAttribute("myself", sv);
            model.addAttribute("myScores", diemService.getByStudent(sv.getMaSV()));
        }
        model.addAttribute("active", "dashboard");
        return "student/dashboard"; // Maps to templates/student/dashboard.html
    }

    @PostMapping("/update-grade")
    public String updateGrade(@RequestParam Long diemId, @RequestParam Double diemQT, @RequestParam Double diemThi) {
        Diem d = diemService.getAll().stream().filter(x -> x.getId().equals(diemId)).findFirst().orElse(null);
        if (d != null) {
            d.setDiemQT(diemQT);
            d.setDiemThi(diemThi);
            d.tinhDiem();
            diemService.save(d);
        }
        return "redirect:/teacher/dashboard"; // Redirect back to teacher dashboard
    }
}
