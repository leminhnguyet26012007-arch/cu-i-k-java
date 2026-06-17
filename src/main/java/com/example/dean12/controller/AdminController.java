package com.example.dean12.controller;

import com.example.dean12.service.*;
import com.example.dean12.model.*;
import com.example.dean12.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired private SinhVienService sinhVienService;
    @Autowired private GiangVienService giangVienService;
    @Autowired private MonHocService monHocService;
    @Autowired private DaoTaoService daoTaoService;
    @Autowired private ThongBaoService thongBaoService;
    @Autowired private LopHocPhanRepository lhpRepo; // Added for new class

    @GetMapping("/classes")
    public String classes(Model model) {
        model.addAttribute("active", "classes");
        model.addAttribute("openClasses", daoTaoService.getOpenClasses());
        return "admin/classes";
    }

    @GetMapping("/classes/new")
    public String newClass(Model model) {
        model.addAttribute("active", "classes");
        model.addAttribute("lhp", new LopHocPhan());
        model.addAttribute("courses", monHocService.getAll());
        model.addAttribute("teachers", giangVienService.getAll());
        return "admin/class-form";
    }

    @PostMapping("/classes/save")
    public String saveClass(@ModelAttribute LopHocPhan lhp) {
        daoTaoService.saveLopHocPhan(lhp);
        return "redirect:/admin/classes";
    }

    @GetMapping("/courses")
    public String courses(Model model) {
        model.addAttribute("active", "courses");
        model.addAttribute("courses", monHocService.getAll());
        return "admin/courses";
    }

    @GetMapping("/courses/new")
    public String newCourse(Model model) {
        model.addAttribute("active", "courses");
        model.addAttribute("course", new MonHoc());
        return "admin/course-form";
    }

    @PostMapping("/courses/save")
    public String saveCourse(@ModelAttribute MonHoc mh) {
        monHocService.save(mh);
        return "redirect:/admin/courses";
    }

    @GetMapping("/students/new")
    public String newStudent(Model model) {
        model.addAttribute("active", "users");
        model.addAttribute("student", new SinhVien());
        return "admin/student-form";
    }

    @PostMapping("/students/save")
    public String saveStudent(@ModelAttribute SinhVien sv) {
        sinhVienService.save(sv);
        return "redirect:/admin/dashboard";
    }

    @GetMapping("/notifications/new")
    public String newNotification(Model model) {
        model.addAttribute("active", "notifications");
        model.addAttribute("notification", new ThongBao());
        return "admin/notification-form";
    }

    @PostMapping("/notifications/save")
    public String saveNotification(@ModelAttribute ThongBao tb) {
        tb.setNgayTao(java.time.LocalDateTime.now());
        thongBaoService.save(tb);
        return "redirect:/admin/dashboard";
    }
}

