package com.sepdrive.service;

import com.sepdrive.model.Car;
import com.sepdrive.model.LatLng;
import com.sepdrive.model.RideRequest;
import com.sepdrive.model.RoutePlan;

import java.math.BigDecimal;
import java.util.List;

public interface RoutePlanService {

    BigDecimal calculatePrice(double distanceKm, Car carType);

    RoutePlan completeRoutePlan(RoutePlan routePlan, Car carType);

    RoutePlan createRoutePlan(LatLng startPoint, LatLng endPoint, List<LatLng> stopovers, Car carType);

    List<LatLng> getWaypointsList(LatLng startPoint, LatLng endPoint, List<LatLng> stopovers);
}
