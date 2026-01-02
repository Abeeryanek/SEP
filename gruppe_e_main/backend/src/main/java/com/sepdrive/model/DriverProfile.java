package com.sepdrive.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "driverProfile")
public class DriverProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Enumerated(EnumType.STRING)
    private Role role;

    private String firstname;
    private String lastname;
    private String email;
    private LocalDate birthdate;

    @Lob
    @Column(columnDefinition = "MEDIUMBLOB")
    private byte[] profilePicture;
    private String profilePictureContentType;

    @Enumerated(EnumType.STRING)
    private Car carType;

    @ElementCollection
    private List<Integer> ratings = new ArrayList<>();
    private double rating;

    private Integer totalTrips;
    private BigDecimal balance;

    private double drivenDistance;

    private double totalDrivenTime = 0.0;

    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
