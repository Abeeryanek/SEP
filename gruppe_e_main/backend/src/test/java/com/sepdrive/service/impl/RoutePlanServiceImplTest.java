package com.sepdrive.service.impl;

import com.sepdrive.model.Car;
import com.sepdrive.model.RideRequest;
import com.sepdrive.model.RoutePlan;
import com.sepdrive.service.RoutePlanService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static org.junit.jupiter.api.Assertions.*;

class RoutePlanServiceImplTest {

    private final RoutePlanService routePlanService = new RoutePlanServiceImpl();


    @Test
    void testCalculatePrice_deluxeClass_10point8Km() {
        RideRequest rideRequest = new RideRequest();
        rideRequest.setVehicleClass(Car.DELUXE);

        RoutePlan routePlan = new RoutePlan();
        routePlan.setTotalDistanceKm(10.8);
        rideRequest.setRoutePlan(routePlan);

        BigDecimal priceProKm = new BigDecimal("10.0");
        BigDecimal distance = new BigDecimal("10.8");

        BigDecimal expectedPrice = distance.multiply(priceProKm).setScale(2, RoundingMode.HALF_UP);
        assertEquals(expectedPrice, routePlanService.calculatePrice(rideRequest.getRoutePlan().getTotalDistanceKm(), rideRequest.getVehicleClass()) );
    }
}