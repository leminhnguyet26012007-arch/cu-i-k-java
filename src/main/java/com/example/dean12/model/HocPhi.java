package com.example.dean12.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "hoc_phi")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HocPhi implements java.io.Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "ma_sv")
    private SinhVien sinhVien;

    private String maHk; // 20231

    private BigDecimal tongTien;
    private BigDecimal daDong;
    private LocalDate hanNop;
    private String trangThai; // CHUA_DONG, DA_DONG
}
