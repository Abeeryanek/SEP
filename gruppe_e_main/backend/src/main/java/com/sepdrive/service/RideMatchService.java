package com.sepdrive.service;


import com.sepdrive.model.PauseRequestDTO;
import com.sepdrive.model.RatingRequestDTO;
import com.sepdrive.model.RideMatchVO;
import com.sepdrive.model.SimulationRouteUpdateMessage;


public interface RideMatchService {

    RideMatchVO findByUsername(String username);

    void startSimulation(Long rideMatchId);

    void pauseSimulation(PauseRequestDTO pauseRequest);

    void completeSimulation(RideMatchVO finalRoute);

    void resumeSimulation(Long rideMatchId);

    void updateSimulation(PauseRequestDTO pauseRequest);

    void processPayment(Long rideMatchId);

    void requestPayment(Long rideMatchId);

    void saveRating(RatingRequestDTO ratingRequest);

    void changeRoute(RideMatchVO updateRoute);
}
