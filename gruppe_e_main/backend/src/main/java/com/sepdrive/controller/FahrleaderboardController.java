package com.sepdrive.controller;

import com.sepdrive.dto.FahrleaderboardDTO;
import com.sepdrive.model.DriverProfile;
import com.sepdrive.repository.DriverRepository;
import com.sepdrive.repository.RideMatchRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;


@RestController
public class FahrleaderboardController {
    @Autowired
    RideMatchRepository rideMatchRepository;
    @Autowired
    DriverRepository driverRepository;

    @GetMapping("/fahrleaderboard")
    public List<FahrleaderboardDTO> getFahrleaderboard() {
       List<DriverProfile> drivers = driverRepository.findAll();
       return drivers.stream()
                .map(d -> {
                    Double averageRatingDriver = rideMatchRepository.getAverageRatingDriver(d.getUsername());
                    return toDto(averageRatingDriver, d);
                })
                .collect(Collectors.toList());

    }
    private FahrleaderboardDTO toDto(Double averageRatingDriver, DriverProfile driverProfile) {
        FahrleaderboardDTO dto = new FahrleaderboardDTO();
        dto.setAvgRating(averageRatingDriver);
        dto.setDriverUsername(driverProfile.getUsername());
        dto.setDriverFullName(driverProfile.getFirstname() + " " + driverProfile.getLastname());
        dto.setTotalDistanceTravelled(driverProfile.getDrivenDistance());
        dto.setTotalDurationTravelled(driverProfile.getTotalDrivenTime());
        dto.setTotalRidesTravelled(driverProfile.getTotalTrips());
        dto.setTotalMoney(driverProfile.getBalance());

        return dto;
    }
}
