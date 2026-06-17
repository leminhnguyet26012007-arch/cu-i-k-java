package com.example.dean12.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "sinh_vien")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SinhVien implements java.io.Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    private String maSV;

    private String hoTen;
    private LocalDate ngaySinh;
    private String lop;
    private String email;
    private String sdt;
    private boolean tuitionPaid; // New field for tuition status

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;
}
