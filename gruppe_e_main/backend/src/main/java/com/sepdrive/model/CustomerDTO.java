package com.sepdrive.model;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class CustomerDTO {
    private Long id;
    private String username;
    private Role role;
    private String firstname;
    private String lastname;
    private String email;
    private LocalDate birthdate;
    private String profilePictureBase64;
    private Double rating = 0.0;
    private Integer totalTrips = 0;
    private BigDecimal balance = BigDecimal.ZERO;

}
