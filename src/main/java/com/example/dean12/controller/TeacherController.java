package com.example.dean12.controller;

import com.example.dean12.model.*;
import com.example.dean12.service.*;
import com.example.dean12.repository.AttendanceRepository;
import com.example.dean12.repository.LopHocPhanRepository;
import com.example.dean12.repository.DiemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.time.LocalDate;

@Controller
@RequestMapping("/teacher")
public class TeacherController {

    @Autowired private GiangVienService giangVienService;
    @Autowired private DaoTaoService daoTaoService; // Need getClassesByTeacher if exists, else add it
    @Autowired private LopHocPhanRepository lhpRepo;
    @Autowired private AttendanceRepository attendanceRepo;
    @Autowired private DiemService diemService;
    @Autowired private DiemRepository diemRepo;
    // @Autowired private DangKyHocService dangKyHocService; // Removed

    // Use existing dashboard from AppController or redefine here? 
    // Let's stick to new features here.

    @GetMapping("/my-classes")
    public String myClasses(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        GiangVien gv = giangVienService.getByUsername(auth.getName());
        if (gv != null) {
            model.addAttribute("classes", lhpRepo.findByGiangVienMaGV(gv.getMaGV()));
        }
        return "teacher/my-classes";
    }

    @GetMapping("/attendance/{lhpId}")
    public String attendancePage(@PathVariable Long lhpId, Model model) {
        LopHocPhan lhp = lhpRepo.findById(lhpId).orElse(null);
        if (lhp != null) {
             model.addAttribute("lhp", lhp);
             // Get students in this class
             List<DangKyHoc> registrations = daoTaoService.getStudentsInClass(lhpId);
             model.addAttribute("registrations", registrations);
             
             // Check if attendance already exists for today? (Simplified: just show form)
             model.addAttribute("today", LocalDate.now());
        }
        return "teacher/attendance";
    }

    @PostMapping("/attendance/save")
    public String saveAttendance(@RequestParam Long lhpId, @RequestParam List<String> studentIds, RedirectAttributes redirectAttributes) {
        // studentIds contains maSV of present students
        LopHocPhan lhp = lhpRepo.findById(lhpId).orElse(null);
        List<DangKyHoc> registrations = daoTaoService.getStudentsInClass(lhpId);
        
        for (DangKyHoc dk : registrations) {
             Attendance att = new Attendance();
             att.setLopHocPhan(lhp);
             att.setSinhVien(dk.getSinhVien());
             att.setNgayDiemDanh(LocalDate.now());
             att.setCoMat(studentIds.contains(dk.getSinhVien().getMaSV()));
             attendanceRepo.save(att);
        }
        redirectAttributes.addFlashAttribute("message", "Đã lưu điểm danh!");
        return "redirect:/teacher/attendance/" + lhpId;
    }

    @GetMapping("/grading/{lhpId}")
    public String gradingPage(@PathVariable Long lhpId, Model model) {
         LopHocPhan lhp = lhpRepo.findById(lhpId).orElse(null);
         if (lhp == null) return "redirect:/teacher/my-classes";
         
         model.addAttribute("lhp", lhp);
         // Get or create Diem entries for students
         List<Diem> diems = diemService.getOrCreateGradesForClass(lhp);
         model.addAttribute("diems", diems);
         
         return "teacher/grading";
    }
    
    @PostMapping("/grading/save")
    public String saveGrades(@RequestParam Long lhpId, @RequestParam List<Long> diemIds, 
                             @RequestParam List<Double> diemQTs, @RequestParam List<Double> diemThis,
                             RedirectAttributes redirectAttributes) {
        
        for (int i = 0; i < diemIds.size(); i++) {
            Diem d = diemRepo.findById(diemIds.get(i)).orElse(null);
            if (d != null && !d.getLocked()) {
                d.setDiemQT(diemQTs.get(i));
                d.setDiemThi(diemThis.get(i));
                d.tinhDiem();
                diemRepo.save(d);
            }
        }
        redirectAttributes.addFlashAttribute("message", "Đã lưu bảng điểm!");
        return "redirect:/teacher/grading/" + lhpId;
    }
    
    @PostMapping("/grading/lock")
    public String lockGrades(@RequestParam Long lhpId, RedirectAttributes redirectAttributes) {
        List<Diem> diems = diemRepo.findByLopHocPhanId(lhpId); // Need this method
        for (Diem d : diems) {
            d.setLocked(true);
            diemRepo.save(d);
        }
        redirectAttributes.addFlashAttribute("message", "Đã khóa bảng điểm! Không thể chỉnh sửa nữa.");
        return "redirect:/teacher/grading/" + lhpId;
    }
}
