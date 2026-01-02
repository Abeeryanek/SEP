package com.sepdrive.backend.Repository;


import com.sepdrive.controller.FahrleaderboardController;
import com.sepdrive.model.DriverProfile;
import com.sepdrive.model.RideMatch;
import com.sepdrive.model.RoutePlan;
import com.sepdrive.repository.DriverRepository;
import com.sepdrive.repository.RideMatchRepository;
import com.sepdrive.repository.RoutePlanRepository;
import com.sepdrive.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import com.sepdrive.model.User;
import java.util.List;

import com.sepdrive.dto.FahrleaderboardDTO;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;


@SpringBootTest
@Transactional
public class FahrleaderTest {
    @Autowired
    DriverRepository driverRepository;
    @Autowired
    RideMatchRepository rideMatchRepository;
    @Autowired
    private FahrleaderboardController fahrleaderboardController;
    @Autowired
    RoutePlanRepository routePlanRepository;
    @Autowired
    UserRepository userRepository;
    @Test
    public void RepoTest() {
        //arrange

        //driver
        User driver = User.builder().username("Arlo Zangari").build();
        userRepository.save(driver);
        DriverProfile driverPoki = DriverProfile.builder().username("Arlo Zangari").totalTrips(2).totalDrivenTime(20.0).drivenDistance(20.0).build();
        driverRepository.save(driverPoki);
        //customer
        User customer = User.builder().username("Mira Mendoza").build();
        userRepository.save(customer);
        //routeplan
        RoutePlan routePlan1= RoutePlan.builder().totalDistanceKm(10.0).durationMin(10.0).build();
        RoutePlan routePlan2= RoutePlan.builder().totalDistanceKm(10.0).durationMin(10.0).build();
        routePlanRepository.save(routePlan1);
        routePlanRepository.save(routePlan2);

        //ridematch
        RideMatch rideMatch1 = RideMatch.builder().driver(driver).customer(customer).ratingDriver(3).routePlan(routePlan1).build();
        RideMatch rideMatch2= RideMatch.builder().driver(driver).customer(customer).ratingDriver(4).routePlan(routePlan2).build();
        rideMatchRepository.save(rideMatch1);
        rideMatchRepository.save(rideMatch2);
        rideMatchRepository.flush();
        userRepository.flush();
        driverRepository.flush();
        routePlanRepository.flush();
        //expected values
        double expectedAvg = (4 + 3) / 2.0;
        double expectedTotalDistance = routePlan1.getTotalDistanceKm() + routePlan2.getTotalDistanceKm();
        double expectedTotalDuration = routePlan1.getDurationMin() + routePlan2.getDurationMin();
        int expectedTotalTrips = 2;
        // act
        List<FahrleaderboardDTO> fahrer = fahrleaderboardController.getFahrleaderboard();
        //assert
        FahrleaderboardDTO dto = fahrer.get(fahrer.size() - 1);
        Assertions.assertEquals(expectedAvg, dto.getAvgRating(), 0.001);
        Assertions.assertEquals(expectedTotalDistance, dto.getTotalDistanceTravelled(), 0.001);
        Assertions.assertEquals(expectedTotalDuration, dto.getTotalDurationTravelled(), 0.001);
        Assertions.assertEquals(expectedTotalTrips, dto.getTotalRidesTravelled());



    }

}
