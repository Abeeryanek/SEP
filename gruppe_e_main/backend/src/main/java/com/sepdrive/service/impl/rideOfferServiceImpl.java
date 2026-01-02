package com.sepdrive.service.impl;

import com.sepdrive.controller.RideOfferController;
import com.sepdrive.model.*;
import com.sepdrive.repository.RideMatchRepository;
import com.sepdrive.repository.RideOfferRepository;
import com.sepdrive.repository.RideRequestRepository;
import com.sepdrive.service.NotificationService;
import com.sepdrive.service.UserService;
import com.sepdrive.service.rideOfferService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class rideOfferServiceImpl implements rideOfferService {

    @Autowired
    private RideOfferRepository rideOfferRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private RideMatchRepository rideMatchRepository;
    @Autowired
    private RideRequestRepository rideRequestRepository;
    @Autowired RoutePlanServiceImpl routePlanService;
    @Autowired
    NotificationService notificationService;


    //Pick the ride request you want as a driver and send a request
      public RideOffer createRideOffer(RideRequest rideRequest, DriverProfile driver) throws RuntimeException{
          //1.Check whether the selected riderequest is still active and if the driver hasnt already sent an offer to the given request
          if (rideRequest.getStatus() != RideRequestStatus.AKTIV) {
              throw new RuntimeException("Diese Fahranfrage ist nicht aktiv!");
          } else if(rideOfferRepository.existsByDriverProfileIdAndOfferStatus(driver.getId(), offerStatus.PENDING)) {
              throw new RuntimeException("Sie haben schon eine Anfrage angenommen!");
          }

          //2. If its active then proceed to create the offer and send it to the customer
          RideOffer  rideOffer = new RideOffer();
          rideOffer.setDriverProfile(driver);
          rideOffer.setRideRequest(rideRequest); //Actually a onetoone relationship, since a driver can only send one request at a given time
          rideOffer.setOfferStatus(offerStatus.PENDING);//Waiting for customer response
          rideOffer.setCreatedOn(LocalDateTime.now());
          rideOffer.setPrice(routePlanService.calculatePrice(rideRequest.getRoutePlan().getTotalDistanceKm(), rideRequest.getVehicleClass()).doubleValue());

          RideOffer savedRideOffer = rideOfferRepository.save(rideOffer);

          RideOfferDTO rideOfferDTO = getRideOfferDTO(savedRideOffer);

          // Notify customer
          notificationService.sendOfferNotification(

                   rideOfferDTO.getCustomerUsername(), rideOfferDTO
          );


          return savedRideOffer;

      }



      //Driver can withdraw his offer
          public void withdrawRideOffer(Long offerId, Long driverId) throws RuntimeException {
              RideOffer offer = rideOfferRepository.findById(offerId)
                      .orElseThrow(() -> new RuntimeException("RideOffer not found")); //find the ride offer with the given id

              //check whether the offers driver id matches the current one(the caller)
              if (!offer.getDriverProfile().getId().equals(driverId)) {
                  throw new RuntimeException("Nicht berechtigt, dieses Angebot zurückzuziehen.");
              }

              //check if its a pending offer
              if (offer.getOfferStatus() != offerStatus.PENDING) {
                  throw new RuntimeException("Das Angebot kann nicht zurückgezogen werden, da es bereits angenommen oder abgelehnt wurde.");
              }

              rideOfferRepository.delete(offer);

          }



    public List<RideOffer> getSortedOffersForRideRequest(Long rideRequestId, Sort sort) {
        return rideOfferRepository.findAllByRideRequestId(rideRequestId, sort);
    }


    //When customer accepts the offer
    @Transactional //Database update
    public RideOffer acceptRideOffer(Long offerId) {
        RideOffer acceptedOffer = rideOfferRepository.findById(offerId)
                .orElseThrow(() -> new RuntimeException("RideOffer not found"));

        if (acceptedOffer.getOfferStatus() != offerStatus.PENDING) {
            throw new RuntimeException("Offer is not in pending state.");
        }

        // 1. Set accepted offer to ACCEPTED
        acceptedOffer.setOfferStatus(offerStatus.ACCEPTED);
        acceptedOffer = rideOfferRepository.save(acceptedOffer);

        // 2. Set all other offers to REJECTED
        rideOfferRepository.rejectAllOtherOffers(
                acceptedOffer.getRideRequest().getId(),
                acceptedOffer.getId(), offerStatus.REJECTED
        );

        //3. Create a ride match for drive simulation
        RideMatch rideMatch = new RideMatch();

        RoutePlan routePlan = acceptedOffer.getRideRequest().getRoutePlan();
        rideMatch.setRoutePlan(routePlan);

        User customer = acceptedOffer.getRideRequest().getCustomer();
        rideMatch.setCustomer(customer);
        rideMatch.setRidePrice(routePlan.getPrice());

        User driver = userService.findByUsername(acceptedOffer.getDriverProfile().getUsername());
        rideMatch.setDriver(driver);

        rideMatch.setCreateTime(LocalDateTime.now());
        rideMatch.setUpdateTime(LocalDateTime.now());

        //4. Set RideRequest to accepted
        RideRequest rideRequest = acceptedOffer.getRideRequest();
        rideRequest.setHasBeenAccepted(true);
        rideRequestRepository.save(rideRequest);

        rideMatchRepository.save(rideMatch);
        return acceptedOffer;
    }


    //returns all the drivers offers in a list
    public List<RideOffer> getRideOffersByDriver(Long driverId) {
        return rideOfferRepository.findAllByDriverProfileId(driverId);
    }


    //check if the driver has an active offer(only one allowed at a given time)
    public boolean hasActiveOffer(String driverUsername) {
        return rideOfferRepository.existsByDriverProfileUsernameAndOfferStatus(driverUsername, offerStatus.PENDING);
    }


    //Allows the customer to reject an offer
    public void rejectRideOffer(Long offerId, Long customerId) throws RuntimeException {
        RideOffer offer = rideOfferRepository.findById(offerId)
                .orElseThrow(() -> new RuntimeException("RideOffer not found"));

        // Prüfe, ob der Kunde wirklich zu der Fahranfrage gehört
        if (!offer.getRideRequest().getCustomer().getId().equals(customerId)) {
            throw new RuntimeException("Not authorized to reject this offer.");
        }

        if (offer.getOfferStatus() != offerStatus.PENDING) {
            throw new RuntimeException("Only pending offers can be rejected.");
        }

        offer.setOfferStatus(offerStatus.REJECTED);
        rideOfferRepository.save(offer);
    }

    public RideOfferDTO getRideOfferDTO(RideOffer rideOffer) {
        RideOfferDTO rideOfferDTO = new RideOfferDTO();
        rideOfferDTO.setId(rideOffer.getId());
        rideOfferDTO.setDriverName(rideOffer.getDriverProfile().getFirstname() + " " + rideOffer.getDriverProfile().getLastname());
        rideOfferDTO.setDriverId(rideOffer.getDriverProfile().getId());
        rideOfferDTO.setRating(rideOffer.getDriverProfile().getRating());
        rideOfferDTO.setTotalTrips(rideOffer.getDriverProfile().getTotalTrips());
        rideOfferDTO.setDrivenDistance(rideOffer.getDriverProfile().getDrivenDistance());
        rideOfferDTO.setRideRequestId(rideOffer.getRideRequest().getId());
        rideOfferDTO.setCustomerId(rideOffer.getRideRequest().getCustomer().getId());
        rideOfferDTO.setStatus(rideOffer.getOfferStatus());
        rideOfferDTO.setPrice(rideOffer.getPrice());
        rideOfferDTO.setCustomerUsername(
                rideOffer.getRideRequest().getCustomer().getUsername());
        return rideOfferDTO;
    }

    public Optional<RideOffer> getPendingOfferByDriverUsername(String driverProfileUsername) {
        return rideOfferRepository.findRideOfferByDriverProfileUsernameAndOfferStatus(driverProfileUsername, offerStatus.PENDING);
    }

    public List<RideOffer> getAllPendingOffersForCustomer(String customerUsername) {
        return rideOfferRepository.findAllByRideRequestCustomerAndOfferStatus(userService.findByUsername(customerUsername), offerStatus.PENDING);
    }

    public Optional<RideOffer> getAcceptedOfferFromActiveRideRequest() {
        return rideOfferRepository.findByRideRequestStatusAndOfferStatus(RideRequestStatus.AKTIV, offerStatus.ACCEPTED);
    }


}





