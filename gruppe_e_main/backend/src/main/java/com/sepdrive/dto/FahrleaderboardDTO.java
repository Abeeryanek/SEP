package com.sepdrive.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class FahrleaderboardDTO {
    @NotNull
    private String driverUsername;

    private String driverFullName;
    private double totalDistanceTravelled;
    private Double avgRating;
    private Double totalDurationTravelled;
    private Integer totalRidesTravelled;
    private BigDecimal totalMoney;
}
