package com.sepdrive.controller;

import com.sepdrive.model.*;
import com.sepdrive.service.DriverService;
import com.sepdrive.service.impl.RideRequestServiceImpl;
import com.sepdrive.service.rideOfferService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/rideoffer")
public class RideOfferController {
    @Autowired
    private rideOfferService rideOfferService;
    @Autowired
    private RideRequestServiceImpl rideRequestService;
    @Autowired
    private DriverService driverService;



    // 1. Driver sends an offer
    @PostMapping("/create/{driverUserName}/{rideRequestId}")
    public ResponseEntity<?> createRideOffer(@PathVariable String driverUserName, @PathVariable Long rideRequestId) {
        try {
            DriverProfile foundDriver = driverService.searchDriverProfile(driverUserName);

            RideRequest foundRequest = rideRequestService.getRideRequestById(rideRequestId)
                    .orElseThrow(() -> new RuntimeException("Ride request not found"));

            if (!foundDriver.getCarType().equals(foundRequest.getVehicleClass())) {
                throw new RuntimeException("Driver cannot accept this ride: mismatched car class");
            }

            RideOffer rideOffer = rideOfferService.createRideOffer(foundRequest, foundDriver);

            return ResponseEntity.ok(rideOfferService.getRideOfferDTO(rideOffer));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("message", e.getMessage()));
        }
    }

    // 2. Driver withdraws his offer
    @DeleteMapping("/withdraw/{offerId}/{driverUserName}")
    public ResponseEntity<?> withdrawOffer(@PathVariable Long offerId, @PathVariable String driverUserName) {
        try {
            DriverProfile foundDriver = driverService.searchDriverProfile(driverUserName);
            rideOfferService.withdrawRideOffer(offerId, foundDriver.getId());
            return ResponseEntity.ok().build();

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", e.getMessage()));
        }
    }

    // 3. Customer accepts the offer
    @PostMapping("/accept/{offerId}")
    public ResponseEntity<?> acceptOffer(@PathVariable Long offerId) {
        try{
            RideOffer accepted = rideOfferService.acceptRideOffer(offerId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", e.getMessage()));
        }
    }

    // 4. Customer rejects the offer
    @PostMapping("/reject/{offerId}/{customerId}")
    public ResponseEntity<?> rejectOffer(@PathVariable Long offerId, @PathVariable Long customerId) {
        try {
            rideOfferService.rejectRideOffer(offerId, customerId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", e.getMessage()));
        }
    }

    // 5. Sorts the list of all offers for the customers ride request
    @GetMapping("/ride-request/{rideRequestId}")
    public ResponseEntity<List<RideOffer>> getSortedOffersForRideRequest(
            @PathVariable Long rideRequestId,
            @RequestParam(defaultValue = "rating") String sortBy,
            @RequestParam(defaultValue = "desc") String order) {

        Sort.Direction direction = order.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Sort sort = Sort.by(direction, sortBy);
        List<RideOffer> offers = rideOfferService.getSortedOffersForRideRequest(rideRequestId, sort);
        return ResponseEntity.ok(offers);
    }

    // 6. get all ride offers of the driver
    @GetMapping("/driver/{driverId}")
    public ResponseEntity<List<RideOffer>> getOffersByDriver(@PathVariable Long driverId) {
        return ResponseEntity.ok(rideOfferService.getRideOffersByDriver(driverId));
    }

    // 7. check whether the driver still has a pending offer
    @GetMapping("/driver/{driverUserName}/has-active")
    public ResponseEntity<Boolean> hasActiveOffer(@PathVariable String driverUserName) {
        return ResponseEntity.ok(rideOfferService.hasActiveOffer(driverUserName));
    }

    @GetMapping("/driver/{driverUserName}/pending-offer")
    public ResponseEntity<?> getPendingOfferByDriverUsername(@PathVariable String driverUserName) {
        Optional<RideOffer> optionalRideOffer = rideOfferService.getPendingOfferByDriverUsername(driverUserName);
        if (optionalRideOffer.isPresent()) {
            RideOfferDTO dto = rideOfferService.getRideOfferDTO(optionalRideOffer.get());
            return ResponseEntity.ok(dto);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Keine offene Fahrtangebote");
    }

    @GetMapping("/customer/{customerUserName}/pending-offers")
    public ResponseEntity<List<RideOfferDTO>> getAllPendingOffersForCustomer(@PathVariable String customerUserName) {
        List<RideOffer>rideOffers = rideOfferService.getAllPendingOffersForCustomer(customerUserName);
        List<RideOfferDTO> rideOfferDTOs = rideOffers.stream()
                .map(rideOfferService::getRideOfferDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(rideOfferDTOs);
    }



}
