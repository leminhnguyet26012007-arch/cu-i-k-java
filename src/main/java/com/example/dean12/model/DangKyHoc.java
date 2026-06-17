package com.example.dean12.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "dang_ky_hoc")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DangKyHoc implements java.io.Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "ma_sv")
    private SinhVien sinhVien;

    @ManyToOne
    @JoinColumn(name = "lhp_id")
    private LopHocPhan lopHocPhan;

    private LocalDateTime ngayDk;

    private Double diemQT;
    private Double diemThi;
    private Double diemTk;
    private String diemChu;

    @PrePersist
    public void onCreate() {
        this.ngayDk = LocalDateTime.now();
    }
}
