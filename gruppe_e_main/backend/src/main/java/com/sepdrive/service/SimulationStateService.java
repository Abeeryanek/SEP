package com.sepdrive.service;

public interface SimulationStateService {
    void updateSpeed(Long rideMatchId, Double speed);
    Double getSpeed(Long rideMatchId);
}
