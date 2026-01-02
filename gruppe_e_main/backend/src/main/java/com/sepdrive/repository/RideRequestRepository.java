package com.sepdrive.repository;

import com.sepdrive.model.RideRequest;
import com.sepdrive.model.User;
import com.sepdrive.model.RideRequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface RideRequestRepository extends JpaRepository<RideRequest, Long> {
    Optional<RideRequest> findByCustomerAndStatus(User customer, RideRequestStatus status);
    List<RideRequest> findAllByCustomer(User customer);
    List<RideRequest> findAllByStatus(RideRequestStatus status);
    RideRequest getRideRequestByRoutePlanId(Long id);
}