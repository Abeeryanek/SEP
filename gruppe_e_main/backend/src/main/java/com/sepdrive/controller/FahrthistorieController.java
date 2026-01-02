package com.sepdrive.controller;




import com.sepdrive.model.RideMatch;

import org.springframework.beans.factory.annotation.Autowired;
import com.sepdrive.repository.RideMatchRepository;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import com.sepdrive.dto.FahrthistorieDTO;
import java.util.List;

@RestController
public class FahrthistorieController {
    @Autowired
    private RideMatchRepository rideMatchRepository;
    @GetMapping("/fahrthistorie")
    public List<FahrthistorieDTO> getFahrthistorie(@RequestParam String username) {
        List<RideMatch> rides= rideMatchRepository.findCompletedRidesByUsername(username);
        return rides.stream().map(this::toDto).collect(java.util.stream.Collectors.toList());
    }
    private FahrthistorieDTO toDto(RideMatch rideMatch) {
        FahrthistorieDTO dto = new FahrthistorieDTO();
        dto.setId(rideMatch.getId());
        dto.setCustomerId(rideMatch.getCustomer().getId());
        dto.setDriverId(rideMatch.getDriver().getId());
        dto.setUpdateTime(rideMatch.getUpdateTime());
        dto.setRidePrice(rideMatch.getRidePrice());
        dto.setRatingCustomer(rideMatch.getRatingCustomer());
        dto.setRatingDriver(rideMatch.getRatingDriver());
        dto.setCustomerFullName(rideMatch.getCustomer().getFirstname() + " " +rideMatch.getCustomer().getLastname());
        dto.setDriverFullName(rideMatch.getDriver().getFirstname() + " "+rideMatch.getDriver().getLastname());
        dto.setCustomerUsername(rideMatch.getCustomer().getUsername());
        dto.setDriverUsername(rideMatch.getDriver().getUsername());
        dto.setDurationMin(rideMatch.getRoutePlan().getDurationMin());
        dto.setTotalDistanceKm(rideMatch.getRoutePlan().getTotalDistanceKm());
        return dto;
    }

}
