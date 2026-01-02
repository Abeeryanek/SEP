package com.sepdrive.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class FahrthistorieDTO {
    private Long id;
    private Long customerId;
    private Long driverId;
    private double durationMin;
    private LocalDateTime updateTime;
    private BigDecimal ridePrice;
    private Integer ratingCustomer;
    private Integer ratingDriver;
    private String CustomerFullName;
    private String CustomerUsername;
    private String DriverFullName;
    private String DriverUsername;
    private double totalDistanceKm;
}
