package com.sepdrive.model;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class DriverDTO {

    private Long id;
    private String username;
    private Role role;
    private String firstname;
    private String lastname;
    private String email;
    private LocalDate birthdate;
    private String profilePictureBase64;
    private Car carType;
    private Double rating = 0.0;
    private Integer totalTrips = 0;
    private BigDecimal balance = BigDecimal.ZERO;
    private double drivenDistance = 0.0;

}
