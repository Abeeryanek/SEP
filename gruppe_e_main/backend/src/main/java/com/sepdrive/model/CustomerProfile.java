package com.sepdrive.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "customerProfile")
public class CustomerProfile {

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

    @ElementCollection
    private List<Integer> ratings = new ArrayList<>();
    private double rating;
    private Integer totalTrips;  // null
    private BigDecimal balance;

    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
