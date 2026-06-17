package com.example.dean12.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "lop_hoc_phan")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LopHocPhan implements java.io.Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String maLhp; // e.g., IT001.1

    @ManyToOne
    @JoinColumn(name = "ma_mh")
    private MonHoc monHoc;

    private String maHk;

    @ManyToOne
    @JoinColumn(name = "ma_gv")
    private GiangVien giangVien;

    private Integer siSoToiDa;
    private Integer siSoHienTai;
    private String phongHoc;
    private String lichHoc;
}
