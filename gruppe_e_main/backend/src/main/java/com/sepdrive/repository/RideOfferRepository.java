package com.sepdrive.repository;

import com.sepdrive.model.*;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RideOfferRepository extends JpaRepository<RideOffer, Long> {


    // Gibt alle Angebote eines bestimmten Fahrers zurück
    List<RideOffer> findAllByDriverProfileId(Long driverId);



    //Returns a sorted list
    List<RideOffer> findAllByRideRequestId(Long rideRequestId, Sort sort);

    //Check whether the driver already has a pending request(only one pending offer allowed at a given time!
    boolean existsByDriverProfileIdAndOfferStatus(Long driverId, offerStatus status);



    //Rejects all other offers once the user accepts one specific offer
    @Modifying//notifies spring that this method changes data
    @Transactional//it has to be either fully changed or not
    @Query("UPDATE RideOffer r SET r.offerStatus = :status " +
            "WHERE r.rideRequest.id = :rideRequestId AND r.id <> :acceptedOfferId")
    void rejectAllOtherOffers(@Param("rideRequestId") Long rideRequestId,
                              @Param("acceptedOfferId") Long acceptedOfferId,
                              @Param("status") offerStatus status);


    boolean existsByDriverProfileUsernameAndOfferStatus(String driverUserName, offerStatus offerStatus);

    Optional<RideOffer> findRideOfferByDriverProfileUsernameAndOfferStatus(String driverProfileUsername, offerStatus offerStatus);

    List<RideOffer> findAllByRideRequestCustomerAndOfferStatus(User byUsername, offerStatus offerStatus);

    Optional<RideOffer> findByRideRequestStatusAndOfferStatus(RideRequestStatus rideRequestStatus, offerStatus offerStatus);
}
