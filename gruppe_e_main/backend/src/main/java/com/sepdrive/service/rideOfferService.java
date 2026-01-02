package com.sepdrive.service;

import com.sepdrive.model.DriverProfile;
import com.sepdrive.model.RideOffer;
import com.sepdrive.model.RideOfferDTO;
import com.sepdrive.model.RideRequest;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Optional;

public interface rideOfferService {


    public  boolean hasActiveOffer(String driverUserName);//Does the driver already has a pending offer?
    public List<RideOffer> getRideOffersByDriver(Long driverId); //List of the offer the driver has pending(only one)
    public RideOffer acceptRideOffer(Long offerId);//Customer has to accept
    public List<RideOffer> getSortedOffersForRideRequest(Long rideRequestId, Sort sort); //Show the customer all rideoffers for his request
    public void withdrawRideOffer(Long offerId, Long driverId);//driver withdraws offer
    public RideOffer createRideOffer(RideRequest rideRequest, DriverProfile driver);//driver chooses the request he wants and sends an offer

    public void rejectRideOffer(Long offerId, Long customerId);
    public RideOfferDTO getRideOfferDTO(RideOffer rideOffer);

    public Optional<RideOffer> getPendingOfferByDriverUsername(String driverProfileUsername);
    public List<RideOffer> getAllPendingOffersForCustomer(String customerUsername);

    public Optional<RideOffer> getAcceptedOfferFromActiveRideRequest();


}
