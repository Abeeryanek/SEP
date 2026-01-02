package com.sepdrive.controller;

import com.sepdrive.exception.BalanceException;
import com.sepdrive.model.*;
import com.sepdrive.service.RideMatchService;
import com.sepdrive.service.RoutePlanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/simulation")
public class SimulationController {

    @Autowired
    private RideMatchService rideMatchService;

    @GetMapping("/match/{username}")
    public ResponseEntity<RideMatchVO> getMatchForSimulation(@PathVariable String username) {

        RideMatchVO rideMatchVO = rideMatchService.findByUsername(username);
        return ResponseEntity.ok(rideMatchVO);
    }

    @PutMapping("/updateIndex")
    public ResponseEntity<Void> updateIndex(@RequestBody PauseRequestDTO pauseRequest) {

            rideMatchService.updateSimulation(pauseRequest);
            return ResponseEntity.ok().build();
    }


    @PutMapping("/start/{rideMatchId}")
    public ResponseEntity<Void> startSimulation(@PathVariable Long rideMatchId) {

            rideMatchService.startSimulation(rideMatchId);
            return ResponseEntity.ok().build();
    }

    @PutMapping("/pause")
    public ResponseEntity<Void> pauseSimulation(@RequestBody PauseRequestDTO pauseRequest) {

            rideMatchService.pauseSimulation(pauseRequest);
            return ResponseEntity.ok().build();
    }

    @PutMapping("/resume/{rideMatchId}")
    public ResponseEntity<Void> resumeSimulation(@PathVariable Long rideMatchId) {

            rideMatchService.resumeSimulation(rideMatchId);
            return ResponseEntity.ok().build();
    }

    @PutMapping("/complete")
    public ResponseEntity<Void> completeSimulation(@RequestBody RideMatchVO finalRoute) {

            rideMatchService.completeSimulation(finalRoute);
            return ResponseEntity.ok().build();
    }

    @PutMapping("/requestPayment/{rideMatchId}")
    public ResponseEntity<Void> requestPayment(@PathVariable Long rideMatchId) {

            rideMatchService.requestPayment(rideMatchId);
            return ResponseEntity.ok().build();
    }


    @PutMapping("/pay/{rideMatchId}")
    public ResponseEntity<Void> pay(@PathVariable Long rideMatchId) {

            rideMatchService.processPayment(rideMatchId);
            return ResponseEntity.ok().build();
    }

    @PutMapping("/rate")
    public ResponseEntity<Void> submitRating(@RequestBody RatingRequestDTO ratingRequest) {

            rideMatchService.saveRating(ratingRequest);
            return ResponseEntity.ok().build();
    }

    @PutMapping("/updateRoute")
    public ResponseEntity<Void> updateRoute(@RequestBody RideMatchVO updateRoute) {

            rideMatchService.changeRoute(updateRoute);
            return ResponseEntity.ok().build();
    }

}
