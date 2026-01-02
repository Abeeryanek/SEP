package com.sepdrive.service.impl;

import com.sepdrive.controller.SimulationWebSocketBroadcaster;
import com.sepdrive.exception.BroadcastException;
import com.sepdrive.service.SimulationStateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class InMemorySimulationStateService implements SimulationStateService {

    @Autowired
    private SimulationWebSocketBroadcaster broadcaster;

    // concurrentHashMap for many users
    private final Map<Long, Double> speedMap = new ConcurrentHashMap<>();

    @Override
    public void updateSpeed(Long rideMatchId, Double speed) {
        try {
            speedMap.put(rideMatchId, speed);

            broadcaster.broadcastSpeedChanged(rideMatchId, speed);

        }catch (Exception e){
            throw new BroadcastException("broadcast speed fail: " + e);
        }
    }

    @Override
    public Double getSpeed(Long rideMatchId) {
        return speedMap.getOrDefault(rideMatchId, 10.0);
    }
}
