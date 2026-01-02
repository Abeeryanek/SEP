package com.sepdrive.model;

import lombok.Data;

import java.util.List;

@Data
public class RideMatchVO {

    private Long id;
    private LatLng startPoint;
    private List<LatLng> stopovers;
    private LatLng endPoint;
    private double totalDistanceKm;
    private double totalDurationMin;
    private double expectedPrice;
    private SimulationStatus simulationStatus;
    private Integer currentSimulationIndex;
    private PaymentStatus paymentStatus;
    private Integer ratingCustomer;
    private Integer ratingDriver;

    // joseph
    private UserVO customer;
    private UserVO driver;
}
