package com.sepdrive.service;

import com.sepdrive.model.RideRequest;
import com.sepdrive.model.User;
import java.util.List;
import java.util.Optional;

public interface RideRequestService {
    RideRequest createRideRequest(RideRequest request, User customer);
    Optional<RideRequest> getActiveRideRequest(User customer);
    void completeRideRequest(Long requestId, User customer);
    List<RideRequest> getAllRideRequestsByCustomer(User customer);
    void deleteActiveRideRequest(User customer);
    void deleteRideRequestById(Long id, User customer);
    List<RideRequest> getAllOpenRideRequests();
    Optional<RideRequest> getRideRequestById(Long rideRequestId);
} 