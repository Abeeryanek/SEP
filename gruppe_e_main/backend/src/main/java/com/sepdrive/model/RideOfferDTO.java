package com.sepdrive.model;

import lombok.Data;

@Data
public class RideOfferDTO {

    private Long id;
    private String driverName;
    private Long driverId;
    private Long rideRequestId;
    private Double rating;
    private Integer totalTrips;
    private Double drivenDistance;
    private Long customerId;
    private Double price;
    private offerStatus status;
    private String customerUsername;
}
