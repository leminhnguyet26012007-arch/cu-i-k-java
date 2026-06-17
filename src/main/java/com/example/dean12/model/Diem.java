package com.example.dean12.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "diem")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Diem implements java.io.Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "ma_sv")
    private SinhVien sinhVien;

    @ManyToOne
    @JoinColumn(name = "ma_mh")
    private MonHoc monHoc;

    @ManyToOne
    @JoinColumn(name = "ma_gv")
    private GiangVien giangVien;

    @ManyToOne
    @JoinColumn(name = "ma_lhp")
    private LopHocPhan lopHocPhan;

    private Double diemQT;
    private Double diemThi;
    private Double diemTongKet;
    private String diemChu;
    private Boolean locked = false; // Add locked status

    @PrePersist
    @PreUpdate
    public void tinhDiem() {
        if (diemQT != null && diemThi != null) {
            this.diemTongKet = (diemQT * 0.4) + (diemThi * 0.6);
            if (diemTongKet >= 8.5)
                this.diemChu = "A";
            else if (diemTongKet >= 7.0)
                this.diemChu = "B";
            else if (diemTongKet >= 5.5)
                this.diemChu = "C";
            else if (diemTongKet >= 4.0)
                this.diemChu = "D";
            else
                this.diemChu = "F";
        }
    }
}

