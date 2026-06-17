package com.example.dean12.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "mon_hoc")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MonHoc implements java.io.Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    private String maMH;

    private String tenMH;
    private int soTinChi;
}
