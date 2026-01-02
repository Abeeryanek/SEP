package com.sepdrive.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    // long is big, in java bigint_type is Long
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    private String firstname;
    private String lastname;
    private String email;
    private LocalDate birthdate;
    @Lob
    @Column(columnDefinition = "MEDIUMBLOB")
    private byte[] profilePicture;
    private String profilePictureContentType;

    @Enumerated(EnumType.STRING)
    private Role role;

    private String password;
    private String twoFactorCode;
    private LocalDateTime twoFactorExpiry;
    private String superCode;

    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    private BigDecimal balance;

}
