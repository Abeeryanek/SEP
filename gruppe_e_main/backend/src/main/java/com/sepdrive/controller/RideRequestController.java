package com.sepdrive.controller;

import com.sepdrive.model.RideRequest;
import com.sepdrive.model.RoutePlan;
import com.sepdrive.model.User;
import com.sepdrive.service.RideRequestService;
import com.sepdrive.service.RoutePlanService;
import com.sepdrive.service.UserService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sepdrive.dto.RideRequestDTO;
import com.sepdrive.service.CustomerService;

import javax.crypto.SecretKey;
import java.util.List;
import java.util.Optional;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/ride-requests")
public class RideRequestController {
    @Autowired
    private RideRequestService rideRequestService;
    @Autowired
    private UserService userService;
    @Autowired
    private SecretKey jwtSecret;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private CustomerService customerService;
    @Autowired
    private RoutePlanService routePlanService;

    // Hilfsmethode: User aus JWT-Token extrahieren
    private User getUserFromToken(String token) {
        Claims claims = Jwts.parserBuilder().setSigningKey(jwtSecret).build().parseClaimsJws(token.replace("Bearer ", "")).getBody();
        String username = claims.getSubject();
        return userService.findByUsername(username);
    }

    @PostMapping
    public ResponseEntity<?> createRideRequest(@RequestBody RideRequestDTO dto, @RequestHeader("Authorization") String token) {
        User customer = getUserFromToken(token);
        try {
            RideRequest entity = new RideRequest();
            entity.setCustomer(customer);
            entity.setVehicleClass(com.sepdrive.model.Car.valueOf(dto.getVehicleClass()));
            entity.setStatus(com.sepdrive.model.RideRequestStatus.AKTIV);
            entity.setCreatedAt(java.time.LocalDateTime.now());
            entity.setUpdatedAt(java.time.LocalDateTime.now());
            // Speichere Start, Ziel, Stopps als JSON
            entity.setStartAddress(objectMapper.writeValueAsString(dto.getStart()));
            entity.setDestinationAddress(objectMapper.writeValueAsString(dto.getDestination()));
            entity.setStopovers(objectMapper.writeValueAsString(dto.getStopovers()));
            entity.setRoutePlan(dto.getRoutePlan());    //Fahrtplanung
            RideRequest created = rideRequestService.createRideRequest(entity, customer);
            return ResponseEntity.ok(created);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Fehler beim Verarbeiten der Fahranfrage: " + e.getMessage());
        }
    }

    @GetMapping("/active")
    public ResponseEntity<?> getActiveRideRequest(@RequestHeader("Authorization") String token) {
        User customer = getUserFromToken(token);
        Optional<RideRequest> active = rideRequestService.getActiveRideRequest(customer);
        if (active.isPresent()) {
            RideRequest req = active.get();
            Map<String, Object> map = new HashMap<>();
            map.put("id", req.getId());
            try {
                map.put("start", objectMapper.readValue(req.getStartAddress(), Map.class));
            } catch (Exception e) { map.put("start", null); }
            try {
                map.put("destination", objectMapper.readValue(req.getDestinationAddress(), Map.class));
            } catch (Exception e) { map.put("destination", null); }
            try {
                map.put("stopovers", objectMapper.readValue(req.getStopovers(), List.class));
            } catch (Exception e) { map.put("stopovers", null); }
            map.put("vehicleClass", req.getVehicleClass());
            map.put("status", req.getStatus());
            map.put("createdAt", req.getCreatedAt());
            map.put("updatedAt", req.getUpdatedAt());
            map.put("routePlan", req.getRoutePlan());   //Fahrtplanung
            return ResponseEntity.ok(map);
        } else {
            return ResponseEntity.noContent().build();
        }
    }

    @PostMapping("/complete/{id}")
    public ResponseEntity<?> completeRideRequest(@PathVariable Long id, @RequestHeader("Authorization") String token) {
        User customer = getUserFromToken(token);
        rideRequestService.completeRideRequest(id, customer);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> getAllRideRequests(@RequestHeader("Authorization") String token) {
        User customer = getUserFromToken(token);
        List<RideRequest> list = rideRequestService.getAllRideRequestsByCustomer(customer);
        List<Map<String, Object>> response = list.stream().map(req -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", req.getId());
            try {
                map.put("start", objectMapper.readValue(req.getStartAddress(), Map.class));
            } catch (Exception e) { map.put("start", null); }
            try {
                map.put("destination", objectMapper.readValue(req.getDestinationAddress(), Map.class));
            } catch (Exception e) { map.put("destination", null); }
            try {
                map.put("stopovers", objectMapper.readValue(req.getStopovers(), List.class));
            } catch (Exception e) { map.put("stopovers", null); }
            map.put("vehicleClass", req.getVehicleClass());
            map.put("status", req.getStatus());
            map.put("createdAt", req.getCreatedAt());
            map.put("updatedAt", req.getUpdatedAt());
            map.put("routePlan", req.getRoutePlan());   //Fahrtplanung
            return map;
        }).collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/active")
    public ResponseEntity<?> deleteActiveRideRequest(@RequestHeader("Authorization") String token) {
        User customer = getUserFromToken(token);
        rideRequestService.deleteActiveRideRequest(customer);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteRideRequest(@PathVariable Long id, @RequestHeader("Authorization") String token) {
        User customer = getUserFromToken(token);
        rideRequestService.deleteRideRequestById(id, customer);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/open")
    public ResponseEntity<List<Map<String, Object>>> getAllOpenRideRequests(@RequestHeader("Authorization") String token) {
        User user = getUserFromToken(token);
        // Optional: Prüfe, ob User ein Fahrer ist
        if (!"FAHRER".equalsIgnoreCase(user.getRole().name())) {
            return ResponseEntity.status(403).build();
        }
        List<RideRequest> list = rideRequestService.getAllOpenRideRequests();
        List<Map<String, Object>> response = new java.util.ArrayList<>();
        for (RideRequest req : list) {
            try {
                Map<String, Object> map = new HashMap<>();
                map.put("id", req.getId());
                map.put("createdAt", req.getCreatedAt());
                // Startpunkt (Adresse, Lat/Lng)
                try {
                    Map<String, Object> start = objectMapper.readValue(req.getStartAddress(), Map.class);
                    map.put("startLat", start.getOrDefault("lat", req.getStartLat()));
                    map.put("startLng", start.getOrDefault("lng", req.getStartLng()));
                } catch (Exception e) {
                    map.put("startLat", req.getStartLat());
                    map.put("startLng", req.getStartLng());
                }
                // Kundenname und Bewertung
                if (req.getCustomer() != null) {
                    map.put("customerName", req.getCustomer().getFirstname() + " " + req.getCustomer().getLastname());
                    Double rating = 0.0;
                    try {
                        var customer = customerService.searchCustomer(req.getCustomer().getUsername());
                        if (customer != null && customer.getRating() != null) {
                            rating = customer.getRating();
                        }
                    } catch (Exception ex) { }
                    map.put("customerRating", rating);
                } else {
                    map.put("customerName", "Unbekannt");
                    map.put("customerRating", 0.0);
                }
                map.put("vehicleClass", req.getVehicleClass());
                //Fahrtplanung
                map.put("totalDistanceKm", req.getRoutePlan().getTotalDistanceKm());
                map.put("durationMin", req.getRoutePlan().getDurationMin());
                map.put("price", req.getRoutePlan().getPrice());
                //Fahrtplanung
                map.put("hasBeenAccepted", req.getHasBeenAccepted());
                response.add(map);
            } catch (Exception ex) {
                // Fehler beim Mapping einer Fahrt: Überspringen, aber nicht abbrechen
                System.err.println("Fehler beim Mapping einer Fahranfrage: " + ex.getMessage());
            }
        }
        System.out.println("Open ride requests: " + response);
        return ResponseEntity.ok(response);
    }

    //Fahrtplanung
    @PostMapping("/route-plan")
    public ResponseEntity<?> addRoutePlanToRideRequest(@RequestBody RideRequest rideRequest) {
        try {
            RoutePlan routePlan = routePlanService.completeRoutePlan(rideRequest.getRoutePlan(), rideRequest.getVehicleClass());
            rideRequest.setRoutePlan(routePlan);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Fehler beim Routenservice: " + e.getMessage());
        }
        return ResponseEntity.ok(rideRequest);
    }
} 