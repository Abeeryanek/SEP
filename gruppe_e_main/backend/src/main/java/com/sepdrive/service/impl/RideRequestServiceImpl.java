package com.sepdrive.service.impl;

import com.sepdrive.model.*;
import com.sepdrive.repository.RideRequestRepository;
import com.sepdrive.repository.UserRepository;
import com.sepdrive.service.RideRequestService;
import com.sepdrive.service.RoutePlanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class RideRequestServiceImpl implements RideRequestService {
    @Autowired
    private RideRequestRepository rideRequestRepository;
    @Autowired
    private UserRepository userRepository;

    //Fahrtplanung
    @Autowired
    private RoutePlanService routePlanService;
    //Fahrtplanung

    @Override
    @Transactional
    public RideRequest createRideRequest(RideRequest request, User customer) {
        // Prüfen, ob schon eine aktive Anfrage existiert
        Optional<RideRequest> active = rideRequestRepository.findByCustomerAndStatus(customer, RideRequestStatus.AKTIV);
        if (active.isPresent()) {
            throw new RuntimeException("Customer already has an active ride request");
        }
        request.setCustomer(customer);
        request.setStatus(RideRequestStatus.AKTIV);
        request.setCreatedAt(LocalDateTime.now());
        request.setUpdatedAt(LocalDateTime.now());
        request.setRoutePlan(routePlanService.completeRoutePlan(request.getRoutePlan(), request.getVehicleClass()));     //Fahrtplanung
        request.setHasBeenAccepted(false);
        return rideRequestRepository.save(request);
    }

    @Override
    public Optional<RideRequest> getActiveRideRequest(User customer) {
        return rideRequestRepository.findByCustomerAndStatus(customer, RideRequestStatus.AKTIV);
    }

    @Override
    @Transactional
    public void completeRideRequest(Long requestId, User customer) {
        RideRequest request = rideRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Ride request not found"));
                
        // Prüfe, ob der User der Besitzer der Fahranfrage ist
        if (!request.getCustomer().getId().equals(customer.getId())) {
            throw new RuntimeException("Not authorized: You can only complete your own ride requests");
        }
        
        // Prüfe, ob die Fahranfrage aktiv ist
        if (request.getStatus() != RideRequestStatus.AKTIV) {
            throw new RuntimeException("Cannot complete ride request: Request is not active");
        }
        
        request.setStatus(RideRequestStatus.ABGESCHLOSSEN);
        request.setUpdatedAt(LocalDateTime.now());
        rideRequestRepository.save(request);
    }

    @Override
    public List<RideRequest> getAllRideRequestsByCustomer(User customer) {
        return rideRequestRepository.findAllByCustomer(customer);
    }

    @Override
    @Transactional
    public void deleteActiveRideRequest(User customer) {
        Optional<RideRequest> active = rideRequestRepository.findByCustomerAndStatus(customer, RideRequestStatus.AKTIV);
        active.ifPresent(rideRequestRepository::delete);
    }

    @Override
    @Transactional
    public void deleteRideRequestById(Long id, User customer) {
        RideRequest request = rideRequestRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ride request not found"));
                
        // Prüfe, ob der User der Besitzer der Fahranfrage ist
        if (!request.getCustomer().getId().equals(customer.getId())) {
            throw new RuntimeException("Not authorized: You can only delete your own ride requests");
        }
        
        // Prüfe, ob die Fahranfrage aktiv ist
        if (request.getStatus() != RideRequestStatus.AKTIV) {
            throw new RuntimeException("Cannot delete ride request: Request is not active");
        }
        
        rideRequestRepository.delete(request);
    }

    @Override
    public List<RideRequest> getAllOpenRideRequests() {
        return rideRequestRepository.findAllByStatus(RideRequestStatus.AKTIV);
    }

    @Override
    public Optional<RideRequest> getRideRequestById(Long rideRequestId) {
        return rideRequestRepository.findById(rideRequestId);
    }
} 