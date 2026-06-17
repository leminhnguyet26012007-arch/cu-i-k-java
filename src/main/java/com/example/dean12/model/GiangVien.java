package com.example.dean12.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "giang_vien")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GiangVien implements java.io.Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    private String maGV;

    private String hoTen;
    private String email;
    private String sdt;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;
}


