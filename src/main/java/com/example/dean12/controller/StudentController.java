package com.example.dean12.controller;

import com.example.dean12.model.*;
import com.example.dean12.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/student")
public class StudentController {

    @Autowired private SinhVienService sinhVienService;
    @Autowired private DaoTaoService daoTaoService;
    @Autowired private com.example.dean12.repository.FeedbackRepository feedbackRepository;

    @GetMapping("/register-course")
    public String registerPage(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        SinhVien sv = sinhVienService.getByUsername(auth.getName());

        List<LopHocPhan> openClasses = daoTaoService.getOpenClasses();
        List<DangKyHoc> myCourses = daoTaoService.getStudentSchedule(sv.getMaSV());

        model.addAttribute("openClasses", openClasses);
        model.addAttribute("myCourses", myCourses);
        model.addAttribute("myself", sv); // For sidebar name
        return "student/course-register";
    }

    @PostMapping("/register")
    public String doRegister(@RequestParam Long lhpId, RedirectAttributes redirectAttrs) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        SinhVien sv = sinhVienService.getByUsername(auth.getName());

        String result = daoTaoService.registerCourse(sv, lhpId);

        if (result.equals("Success")) {
            redirectAttrs.addFlashAttribute("message", "Đăng ký thành công!");
        } else {
            redirectAttrs.addFlashAttribute("error", result);
        }

        return "redirect:/student/register-course";
    }
    @Autowired private FinanceService financeService;

    @GetMapping("/tuition")
    public String tuitionPage(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        SinhVien sv = sinhVienService.getByUsername(auth.getName());

        // Recalculate tuition every time page opens (for demo)
        HocPhi hp = financeService.calculateAndSaveTuition(sv, "20231");

        model.addAttribute("invoice", hp);
        model.addAttribute("myself", sv);
        return "student/tuition";
    }

    @PostMapping("/pay-tuition")
    public String payTuition(@RequestParam Long hpId, RedirectAttributes redirectAttrs) {
        financeService.payTuition(hpId);
        redirectAttrs.addFlashAttribute("message", "Thanh toán thành công!");
        return "redirect:/student/tuition";
    }

    @GetMapping("/schedule")
    public String schedule(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        SinhVien sv = sinhVienService.getByUsername(auth.getName());
        model.addAttribute("myself", sv);
        model.addAttribute("schedule", daoTaoService.getStudentSchedule(sv.getMaSV()));
        return "student/schedule";
    }

    @GetMapping("/feedback")
    public String feedback(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        SinhVien sv = sinhVienService.getByUsername(auth.getName());
        model.addAttribute("myself", sv);
        model.addAttribute("feedbacks", feedbackRepository.findBySinhVienMaSV(sv.getMaSV()));
        model.addAttribute("newFeedback", new com.example.dean12.model.Feedback());
        return "student/feedback";
    }

    @PostMapping("/feedback/save")
    public String saveFeedback(@ModelAttribute com.example.dean12.model.Feedback feedback) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        SinhVien sv = sinhVienService.getByUsername(auth.getName());
        feedback.setSinhVien(sv);
        feedback.setNgayGui(java.time.LocalDateTime.now());
        feedback.setTrangThai("Chờ xử lý");
        feedbackRepository.save(feedback);
        return "redirect:/student/feedback";
    }
}

