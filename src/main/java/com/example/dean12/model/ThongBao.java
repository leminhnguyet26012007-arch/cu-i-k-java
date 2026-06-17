package com.example.dean12.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "thong_bao")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ThongBao implements java.io.Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String tieuDe;

    @Column(columnDefinition = "TEXT")
    private String noiDung;

    private LocalDateTime ngayTao;
    private String doiTuong; // ALL, SINH_VIEN, GIANG_VIEN
}
