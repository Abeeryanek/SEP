package com.sepdrive.service.impl;

import com.sepdrive.model.Car;
import com.sepdrive.model.LatLng;
import com.sepdrive.model.RideRequest;
import com.sepdrive.model.RoutePlan;
import com.sepdrive.service.RoutePlanService;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
public class RoutePlanServiceImpl implements RoutePlanService {

    @Override
    public BigDecimal calculatePrice(double distanceKm, Car carType) {
        BigDecimal priceProKm = switch (carType) {
            case KLEIN -> BigDecimal.valueOf(1.0);
            case MEDIUM -> BigDecimal.valueOf(2.0);
            case DELUXE -> BigDecimal.valueOf(10.0);
            default -> BigDecimal.ZERO;
        };
        BigDecimal distance = BigDecimal.valueOf(distanceKm);
        return distance.multiply(priceProKm).setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public RoutePlan completeRoutePlan(RoutePlan routePlan, Car carType) {
        List<LatLng> wayPoints = this.getWaypointsList(routePlan.getStartPoint(), routePlan.getEndPoint(), routePlan.getStopovers());

        StringBuilder coordinate = new StringBuilder();
        for (LatLng wayPoint : wayPoints) {
            if (!coordinate.isEmpty()) {
                coordinate.append(";");
            }
            coordinate.append(String.format(Locale.US, "%f,%f", wayPoint.getLng(), wayPoint.getLat()));
        }
        String url = String.format(Locale.US, "https://router.project-osrm.org/route/v1/driving/%s?overview=false",
                coordinate);


        RestTemplate restTemplate = new RestTemplate();
        String response = restTemplate.getForObject(url, String.class);

        JSONObject json = new JSONObject(response);
        JSONObject route = json.getJSONArray("routes").getJSONObject(0);

        double totalDistanceKm = Math.round((route.getDouble("distance") / 1000) * 100.0) / 100.0;
        double durationMin = Math.round(route.getDouble("duration") / 60);


        routePlan.setTotalDistanceKm(totalDistanceKm);
        routePlan.setDurationMin(durationMin);
        routePlan.setPrice(calculatePrice(routePlan.getTotalDistanceKm(), carType));
        return routePlan;
    }

    public RoutePlan createRoutePlan(LatLng startPoint, LatLng endPoint, List<LatLng> stopovers, Car carType) {
        RoutePlan routePlan = new RoutePlan();
        routePlan.setStartPoint(startPoint);
        routePlan.setEndPoint(endPoint);
        routePlan.setStopovers(stopovers);
        return completeRoutePlan(routePlan, carType);
    }

    @Override
    public List<LatLng> getWaypointsList(LatLng startPoint, LatLng endPoint, List<LatLng> stopovers) {
        List<LatLng> waypointsList = new ArrayList<>();

        waypointsList.add(startPoint);

        if (!stopovers.isEmpty()) {
            waypointsList.addAll(stopovers);
        }

        waypointsList.add(endPoint);
        return waypointsList;
    }
}
