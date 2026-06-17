package com.example.dean12.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "phan_hoi")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Feedback implements java.io.Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "ma_sv")
    private SinhVien sinhVien;

    private String tieuDe; // Subject
    private String noiDung; // Content
    private String loaiDon; // Type: Leave, Confirmation, Feedback
    private LocalDateTime ngayGui;
    private String trangThai; // Pending, Approved, Rejected
}
